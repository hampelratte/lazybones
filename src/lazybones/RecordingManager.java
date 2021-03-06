/*
 * Copyright (c) Henrik Niehaus & Lazy Bones development team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the project (Lazy Bones) nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package lazybones;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.swing.SwingUtilities;

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.LSTR;
import org.hampelratte.svdrp.commands.PLAY;
import org.hampelratte.svdrp.parsers.RecordingParser;
import org.hampelratte.svdrp.responses.highlevel.DiskStatus;
import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lazybones.actions.ListRecordingsAction;
import lazybones.actions.StatDiskAction;

/**
 * Class to manage all recordings.
 *
 * @author <a href="hampelratte@users.sf.net">hampelratte@users.sf.net</a>
 */
public class RecordingManager extends Observable {

    private static transient Logger logger = LoggerFactory.getLogger(RecordingManager.class);

    /**
     * Stores all recordings as Recording objects
     */
    private List<Recording> recordings;

    /**
     * Contains stats about the disk usage of the VDR
     */
    private DiskStatus diskStatus;

    private TimerManager timerManager;

    public RecordingManager() {
        recordings = new ArrayList<Recording>();
    }

    public void removeAll() {
        recordings.clear();
        setChanged();
        notifyObservers(recordings);
    }

    /**
     * @return an List of Recording objects
     */
    public List<Recording> getRecordings() {
        return recordings;
    }

    /**
     * @return disk usage stats like total space, free space and usage in percent
     */
    public DiskStatus getDiskStatus() {
        return diskStatus;
    }

    /**
     * Fetches the recording list from vdr
     */
    public synchronized void synchronize() {
        synchronize(null);
    }

    /**
     * Fetches the recording list from vdr
     *
     * @param callback
     *            will be called after the synchronization has finished
     */
    public synchronized void synchronize(final Runnable callback) {
        logger.debug("Getting recordings from VDR");

        // fetch current recording list from vdr
        VDRCallback<ListRecordingsAction> _callback = new VDRCallback<ListRecordingsAction>() {
            @Override
            public void receiveResponse(ListRecordingsAction lstr, Response response) {
                if (lstr.isSuccess()) {
                    // clear recording list
                    recordings = lstr.getRecordings();
                    boolean loadInfos = Boolean.TRUE.toString().equals(LazyBones.getProperties().getProperty("loadRecordInfos"));
                    if (loadInfos) {
                        for (Recording rec : recordings) {
                            logger.trace("Getting info for recording {}", rec.getId());
                            Response resp = VDRConnection.send(new LSTR(rec.getId()));
                            if (resp != null && resp.getCode() == 215) {
                                // parse epg information
                                try {
                                    new RecordingParser().parseRecording(rec, resp.getMessage());
                                } catch (ParseException e) {
                                    logger.error("Couldn't parse epg information", e);
                                }
                            }
                        }
                    }
                }

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        setChanged();
                        notifyObservers(recordings);
                    }
                });

                if (callback != null) {
                    callback.run();
                }
            }
        };
        ListRecordingsAction lstr = new ListRecordingsAction(_callback);
        lstr.enqueue();

        // fetch the disk usage stats
        VDRCallback<StatDiskAction> sdaCallback = new VDRCallback<StatDiskAction>() {
            @Override
            public void receiveResponse(StatDiskAction cmd, Response response) {
                diskStatus = cmd.getDiskStatus();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        setChanged();
                        notifyObservers(diskStatus);
                    }
                });
            }
        };
        StatDiskAction sda = new StatDiskAction(sdaCallback);
        sda.enqueue();
    }

    // public void loadInfo(Recording rec) {
    // LazyBones.getInstance().getMainDialog().setCursor(new Cursor(Cursor.WAIT_CURSOR));
    // Response response = VDRConnection.send(new LSTR(rec.getNumber()));
    // if (response != null && response.getCode() == 215) {
    // // parse epg information
    // try {
    // new RecordingParser().parseRecording(rec, response.getMessage());
    // } catch (ParseException e) {
    // logger.error("Couldn't parse epg information", e);
    // }
    // }
    // LazyBones.getInstance().getMainDialog().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    // }

    public void playOnVdr(Recording rec) {
        Response res = VDRConnection.send(new PLAY(rec.getId()));
        if (res.getCode() != 250) {
            logger.error(res.getMessage());
        }
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public void setTimerManager(TimerManager timerManager) {
        this.timerManager = timerManager;
    }
}