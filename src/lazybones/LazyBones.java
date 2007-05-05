/* $Id: LazyBones.java,v 1.72 2007-05-05 18:06:27 hampelratte Exp $
 * 
 * Copyright (c) 2005, Henrik Niehaus & Lazy Bones development team
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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;

import lazybones.gui.MainDialog;
import lazybones.gui.ProgramSelectionDialog;
import lazybones.gui.TimerOptionsDialog;
import lazybones.gui.TimerSelectionDialog;
import lazybones.gui.settings.VDRSettingsPanel;
import lazybones.utils.Utilities;

import org.hampelratte.svdrp.Connection;
import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.VDRVersion;
import org.hampelratte.svdrp.commands.LSTE;
import org.hampelratte.svdrp.commands.NEWT;
import org.hampelratte.svdrp.commands.UPDT;
import org.hampelratte.svdrp.responses.R250;
import org.hampelratte.svdrp.responses.highlevel.Channel;
import org.hampelratte.svdrp.responses.highlevel.EPGEntry;
import org.hampelratte.svdrp.responses.highlevel.VDRTimer;
import org.hampelratte.svdrp.util.EPGParser;

import com.thoughtworks.xstream.XStream;

import devplugin.*;
import devplugin.Date;

/**
 * A VDRRemoteControl Plugin
 * 
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 * 
 */
public class LazyBones extends Plugin implements Observer {

    public static final Logger logger = Logger.getLogger();

    /** Translator */
    private static final util.ui.Localizer mLocalizer = util.ui.Localizer
            .getLocalizerFor(LazyBones.class);

    private MainDialog mainDialog;

    private static Properties props;
    
    private ContextMenuFactory cmf = new ContextMenuFactory();
    
    private static LazyBones instance;
    
    public static LazyBones getInstance() {
        return instance;
    }
    
    public ActionMenu getContextMenuActions(final Program program) {
        return cmf.createActionMenu(program);
    }

    private ButtonAction buttonAction;
    public ActionMenu getButtonAction() {
        buttonAction = new ButtonAction();
        buttonAction.setActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getMainDialog().setVisible(true);
            }
        });

        buttonAction.setBigIcon(createImageIcon("lazybones/vdr24.png"));
        buttonAction.setSmallIcon(createImageIcon("lazybones/vdr16.png"));
        buttonAction.setShortDescription(LazyBones.getTranslation("lazybones", "Lazy Bones"));
        buttonAction.setText(LazyBones.getTranslation("lazybones", "Lazy Bones"));

        return new ActionMenu(buttonAction);
    }

    private void watch(Program program) {
        Player.play(program);
    }
    
    public void createTimer() {
        Timer timer = new Timer();
        timer.setChannelNumber(1);
        Program prog = ProgramManager.getInstance().getProgram(timer);
        
        // in this situation it makes sense to show the timer options
        // so we override the user setting (hide options dialog)
        boolean showTimerOptions = Boolean.TRUE.toString().equals(props.getProperty("showTimerOptionsDialog"));
        props.setProperty("showTimerOptionsDialog",Boolean.TRUE.toString());
        createTimer(prog);
        props.setProperty("showTimerOptionsDialog",Boolean.toString(showTimerOptions));
    }
    
    private void createTimer(Program prog) {
        if (prog.isExpired()) {
            logger.log(LazyBones.getTranslation(
                    "expired", "This program has expired"), Logger.OTHER, Logger.ERROR);
            return;
        }

        Calendar cal = GregorianCalendar.getInstance();
        Date date = prog.getDate();
        cal.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
        cal.set(Calendar.MONTH, date.getMonth() - 1);
        cal.set(Calendar.YEAR, date.getYear());
        cal.set(Calendar.HOUR_OF_DAY, prog.getHours());
        cal.set(Calendar.MINUTE, prog.getMinutes());
        cal.add(Calendar.MINUTE, prog.getLength() / 2);
        long millis = cal.getTimeInMillis();

        Object o = ProgramManager.getChannelMapping().get(prog.getChannel().getId());
        if (o == null) {
            logger.log(LazyBones.getTranslation("no_channel_defined",
                    "No channel defined", prog.toString()), Logger.OTHER, Logger.ERROR);
            return;
        }
        int id = ((Channel) o).getChannelNumber();
        Response res = VDRConnection.send(new LSTE(Integer.toString(id), "at "
                + Long.toString(millis / 1000)));

        if (res != null && res.getCode() == 215) {
            List epgList = EPGParser.parse(res.getMessage());

            if (epgList.size() <= 0) {
                noEPGAvailable(prog, id);
                return;
            }

            /*
             * VDR 1.3 already returns the matching entry, for 1.2 we need to
             * search for a match
             */
            VDRVersion version = Connection.getVersion();
            boolean isOlderThan1_3 = version.getMajor() < 1
                    || (version.getMajor() == 1 && version.getMinor() < 3);
            EPGEntry vdrEPG = isOlderThan1_3 ? filterEPGDate(epgList,
                    ((Channel) o).getName(), millis) : (EPGEntry) epgList
                    .get(0);

            Timer timer = new Timer();
            timer.setChannelNumber(id);
            timer.addTvBrowserProgID(prog.getID());
            int prio = Integer.parseInt(getProperties().getProperty("timer.prio"));
            timer.setPriority(prio);
            int lifetime = Integer.parseInt(getProperties().getProperty("timer.lifetime"));
            timer.setLifetime(lifetime);

            int buffer_before = Integer.parseInt(props
                    .getProperty("timer.before"));
            int buffer_after = Integer.parseInt(props
                    .getProperty("timer.after"));

            if (vdrEPG != null) {
                Calendar calStart = vdrEPG.getStartTime();
                timer.setUnbufferedStartTime((Calendar) calStart.clone());
                // start the recording x min before the beggining of the program
                calStart.add(Calendar.MINUTE, -buffer_before);
                timer.setStartTime(calStart);

                Calendar calEnd = vdrEPG.getEndTime();
                timer.setUnbufferedEndTime((Calendar) calEnd.clone());
                // stop the recording x min after the end of the program
                calEnd.add(Calendar.MINUTE, buffer_after);
                timer.setEndTime(calEnd);

                timer.setFile(vdrEPG.getTitle());
                timer.setDescription(vdrEPG.getDescription());
            } else { // VDR has no EPG data
                noEPGAvailable(prog, id);
                return;
            }

            boolean showOptionsDialog = Boolean.TRUE.toString().equals(props.getProperty("showTimerOptionsDialog"));
            
            if(showOptionsDialog) {
                new TimerOptionsDialog(this,timer, prog,false);
            } else {
                createTimerCallBack(timer, prog, false);
            }
            

        } else if(res != null && res.getCode() == 550 & "No schedule found\n".equals(res.getMessage())) {
            noEPGAvailable(prog, id);
        } else {
            String msg = res != null ? res.getMessage() : "Reason unknown";
            logger.log(LazyBones.getTranslation("couldnt_create",
                    "Couldn\'t create timer\n: ") + " " + msg,Logger.OTHER, Logger.ERROR);
        }
    }
    
    /**
     * Called by TimerOptionsDialog, when the user confirms the dialog
     * @param timer The new created / updated Timer
     * @param prog The according Program
     * @param update If the Timer is a new one or if the timer has been edited
     */
    public void createTimerCallBack(Timer timer, Program prog, boolean update) {
        int id = -1;
        if(prog != null) {
            Object o = ProgramManager.getChannelMapping().get(prog.getChannel().getId());
            if (o == null) {
                logger.log(LazyBones.getTranslation("no_channel_defined","No channel defined", prog.toString()), Logger.OTHER, Logger.ERROR);
                return;
            }
            id = ((Channel) o).getChannelNumber();
        }
        
        if (update) {
            Response response = VDRConnection.send(new UPDT(timer.toNEWT()));

            if (response == null) {
                String mesg = LazyBones.getTranslation(
                        "couldnt_change", "Couldn\'t change timer:")
                        + "\n"
                        + LazyBones.getTranslation("couldnt_connect",
                                "Couldn\'t connect to VDR");
                logger.log(mesg, Logger.CONNECTION, Logger.ERROR);                
                return;
            }

            if (response instanceof R250) {
                // since we dont have the ID of the new timer, we have to
                // get the whole timer list again :-(
                TimerManager.getInstance().synchronize();
            } else {
                String mesg =  LazyBones.getTranslation(
                        "couldnt_change", "Couldn\'t change timer:")
                        + " " + response.getMessage();
                logger.log(mesg, Logger.OTHER, Logger.ERROR);
            }
        } else {
            if (timer.getTitle() != null) {
                int percentage = Utilities.percentageOfEquality(
                        prog.getTitle(), timer.getTitle());
                if (timer.getFile().indexOf("EPISODE") >= 0
                        || timer.getFile().indexOf("TITLE") >= 0
                        || timer.isRepeating()) {
                    percentage = 100;
                }
                int threshold = Integer.parseInt(props.getProperty("percentageThreshold"));
                if (percentage > threshold) {
                    Response response = VDRConnection.send(new NEWT(timer.toNEWT()));
                    
                    if (response == null) {
                        String mesg = LazyBones.getTranslation("couldnt_create",
                                "Couldn\'t create timer:")
                                + "\n"
                                + LazyBones.getTranslation("couldnt_connect",
                                        "Couldn\'t connect to VDR");
                        logger.log(mesg, Logger.CONNECTION, Logger.ERROR);                
                        return;
                    }
                    
                    if (response instanceof R250) {
                        // since we dont have the ID of the new timer, we
                        // have to get the whole timer list again :-(
                        TimerManager.getInstance().synchronize();
                    } else {
                        logger.log(LazyBones.getTranslation("couldnt_create",
                                "Couldn\'t create timer:")
                                + " " + response.getMessage(), Logger.OTHER,
                                Logger.ERROR);
                    }
                } else {
                    logger.log("Looking in title mapping for timer "+timer, Logger.OTHER, Logger.DEBUG);
                    // lookup in mapping history
                    TimerManager tm = TimerManager.getInstance();
                    String timerTitle = (String)tm.getTitleMapping().getVdrTitle(prog.getTitle());
                    if(timer.getTitle().equals(timerTitle)) {
                        Response response = VDRConnection.send(new NEWT(timer.toNEWT()));
                        if (response.getCode() == 250) {
                            timerCreatedOK(prog, timer);
                        } else {
                            logger.log(LazyBones.getTranslation("couldnt_create",
                                    "Couldn\'t create timer:")
                                    + " " + response.getMessage(), Logger.OTHER,
                                    Logger.ERROR);
                        }
                    } else { // no mapping found -> ask the user
                        showTimerConfirmDialog(timer, prog);
                    }
                }
            } else { // VDR has no EPG data
                noEPGAvailable(prog, id);
            }
        }
    }

    private void noEPGAvailable(Program prog, int channelID) {
        int buffer_before = Integer.parseInt(props.getProperty("timer.before"));
        int buffer_after = Integer.parseInt(props.getProperty("timer.after"));

        boolean dontCare = Boolean.FALSE.toString().equals(props.getProperty("logEPGErrors"));
        int result = JOptionPane.NO_OPTION;
        if(!dontCare) {
            result = JOptionPane.showConfirmDialog(null, LazyBones.getTranslation(
                "noEPGdata", ""), "", JOptionPane.YES_NO_OPTION);
        }
        if (dontCare || result == JOptionPane.OK_OPTION) {
            Timer newTimer = new Timer();
            newTimer.setState(VDRTimer.ACTIVE);
            newTimer.setChannelNumber(channelID);
            int prio = Integer.parseInt(getProperties().getProperty("timer.prio"));
            int lifetime = Integer.parseInt(getProperties().getProperty("timer.lifetime"));
            newTimer.setLifetime(lifetime);
            newTimer.setPriority(prio);
            newTimer.setTitle(prog.getTitle());
            newTimer.addTvBrowserProgID(prog.getID());

            Date d = prog.getDate();
            Calendar startTime = d.getCalendar();

            int start = prog.getStartTime();
            int hour = start / 60;
            int minute = start % 60;
            startTime.set(Calendar.HOUR_OF_DAY, hour);
            startTime.set(Calendar.MINUTE, minute);

            Calendar endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.MINUTE, prog.getLength());

            newTimer.setUnbufferedStartTime((Calendar) startTime.clone());
            newTimer.setUnbufferedEndTime((Calendar) endTime.clone());
            
            // add buffers
            startTime.add(Calendar.MINUTE, -buffer_before);
            newTimer.setStartTime(startTime);
            endTime.add(Calendar.MINUTE, buffer_after);
            newTimer.setEndTime(endTime);

            new TimerOptionsDialog(this, newTimer, prog, false);
        }
    }
    
    /**
     * Called by TimerSelectionDialog, if a VDR-Program has been selected
     * @param selectedProgram
     */
    public void timerSelectionCallBack(Program selectedProgram, Program originalProgram) {
        int buffer_before = Integer.parseInt(props.getProperty("timer.before"));
        int buffer_after = Integer.parseInt(props.getProperty("timer.after"));
        
        Timer t = ((TimerProgram) selectedProgram).getTimer();
        // start the recording x min before the beggining of
        // the program
        t.getStartTime().add(Calendar.MINUTE, -buffer_before);
        // stop the recording x min after the end of the
        // program
        t.getEndTime().add(Calendar.MINUTE, buffer_after);
        Response response = VDRConnection.send(new NEWT(t.toNEWT()));
        if (response.getCode() == 250) {
            timerCreatedOK(selectedProgram, t);
        } else {
            logger.log(LazyBones.getTranslation("couldnt_create",
                    "Couldn\'t create timer:")
                    + " " + response.getMessage(),
                    Logger.OTHER, Logger.ERROR);
        }
        
        logger.log("Storing " + originalProgram.getTitle() + "-"+t.getTitle()+ " in tvb2vdr", Logger.OTHER, Logger.DEBUG);
        TimerManager.getInstance().getTitleMapping().put(originalProgram.getTitle(), t.getTitle());
    }

    public void timerCreatedOK(Program prog, Timer timer) {
        timer.addTvBrowserProgID(prog.getID());
        TimerManager.getInstance().replaceStoredTimer(timer);
        
        // since we dont have the ID of the new timer, we have
        // to get the whole timer list again :-(
        TimerManager.getInstance().synchronize();
    }

    /**
     * If a Program can't be assigned to a VDR-Program, this method shows a
     * dialog to select the right VDR-Program
     * 
     * @param prog
     *            the Program selected in TV-Browser
     * @param timerOptions
     *            the timer from TimerOptionsDialog
     */
    private void showTimerConfirmDialog(Timer timerOptions, Program prog) {
        // get all programs 2 hours before and after the given program
        Calendar cal = GregorianCalendar.getInstance();
        Date date = prog.getDate();
        cal.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
        cal.set(Calendar.MONTH, date.getMonth() - 1);
        cal.set(Calendar.YEAR, date.getYear());
        cal.set(Calendar.HOUR_OF_DAY, prog.getHours());
        cal.set(Calendar.MINUTE, prog.getMinutes());
        cal.add(Calendar.MINUTE, prog.getLength() / 2);

        devplugin.Channel chan = prog.getChannel();

        TreeSet<Timer> programSet = new TreeSet<Timer>();
        for (int i = 10; i <= 120; i += 10) {
            // get the program before the given one
            Calendar c = GregorianCalendar.getInstance();
            c.setTimeInMillis(cal.getTimeInMillis());
            c.add(Calendar.MINUTE, i * -1);
            Timer t1 = getVDRProgramAt(c, chan);
            if (t1 != null) {
                programSet.add(t1);
            }

            // get the program after the given one
            c = GregorianCalendar.getInstance();
            c.setTimeInMillis(cal.getTimeInMillis());
            c.add(Calendar.MINUTE, i);
            Timer t2 = getVDRProgramAt(c, chan);
            if (t2 != null) {
                programSet.add(t2);
            }
        }

        Program[] programs = new Program[programSet.size()];
        int i = 0;
        for (Iterator iter = programSet.iterator(); iter.hasNext();) {
            Timer timer = (Timer) iter.next();
            Calendar time = timer.getStartTime();
            TimerProgram p = new TimerProgram(chan, new Date(time), time
                    .get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
            p.setTitle(timer.getTitle());
            p.setDescription("");
            p.setTimer(timer);
            programs[i++] = p;
        }

        // reverse the order of the programs
        Program[] temp = new Program[programs.length];
        for (int j = 0; j < programs.length; j++) {
            temp[j] = programs[programs.length - 1 - j];
        }
        programs = temp;

        // show dialog
        new TimerSelectionDialog(this, programs, timerOptions, prog);
    }

    /**
     * If a timer can't be assigned to a Program, this method shows a dialog to
     * select the right Program
     * 
     * @param timer
     *            the timer received from the VDR
     */
    private void showProgramConfirmDialog(Timer timer) {
        Calendar cal = timer.getStartTime();

        Calendar start = GregorianCalendar.getInstance();
        start.setTimeInMillis(cal.getTimeInMillis());

        Enumeration en = ProgramManager.getChannelMapping().keys();
        devplugin.Channel chan = null;
        while (en.hasMoreElements()) {
            String channelID = (String) en.nextElement();
            Channel channel = (Channel) ProgramManager.getChannelMapping().get(channelID);
            if (channel.getChannelNumber() == timer.getChannelNumber()) {
                chan = ProgramManager.getInstance().getChannelById(channelID);
            }
        }

        // if we cant find the channel, stop
        if (chan == null)
            return;

        // get all programs 3 hours before and after the given program
        HashSet<Program> programSet = new HashSet<Program>();
        for (int i = 0; i <= 180; i++) { 
            // get the program before the given one
            Calendar c = GregorianCalendar.getInstance();
            c.setTimeInMillis(start.getTimeInMillis());
            c.add(Calendar.MINUTE, i * -1);
            Program p1 = ProgramManager.getInstance().getProgramAt(c, c, chan);
            if (p1 != null) {
                programSet.add(p1);
            }

            // get the program after the given one
            c = GregorianCalendar.getInstance();
            c.setTimeInMillis(start.getTimeInMillis());
            c.add(Calendar.MINUTE, i);
            Program p2 = ProgramManager.getInstance().getProgramAt(c, c, chan);
            if (p2 != null) {
                programSet.add(p2);
            }
        }

        Program[] programs = new Program[programSet.size()];
        int i = 0;
        for (Iterator iter = programSet.iterator(); iter.hasNext();) {
            Program p = (Program) iter.next();
            programs[i++] = p;
        }
        Arrays.sort(programs, new ProgramComparator());

        // show dialog
        new ProgramSelectionDialog(this, programs, timer);
    }

    protected Timer getVDRProgramAt(Calendar cal, devplugin.Channel chan) {
        long millis = cal.getTimeInMillis() / 1000;
        Object o = ProgramManager.getChannelMapping().get(chan.getId());
        int id = ((Channel) o).getChannelNumber();

        LSTE cmd = new LSTE(Integer.toString(id), "at " + Long.toString(millis));
        Response res = VDRConnection.send(cmd);
        if (res != null && res.getCode() == 215) {
            List epg = EPGParser.parse(res.getMessage());
            if (epg.size() > 0) {
                EPGEntry entry = (EPGEntry) epg.get(0);
                VDRTimer timer = new VDRTimer();
                timer.setChannelNumber(id);
                timer.setTitle(entry.getTitle());
                timer.setStartTime(entry.getStartTime());
                timer.setEndTime(entry.getEndTime());
                timer.setDescription(entry.getDescription());
                int prio = Integer.parseInt(getProperties().getProperty("timer.prio"));
                int lifetime = Integer.parseInt(getProperties().getProperty("timer.lifetime"));
                timer.setLifetime(lifetime);
                timer.setPriority(prio);
                return new Timer(timer);
            }
        }
        return null;
    }

    public String getMarkIconName() {
        return "lazybones/vdr16.png";
    }

    public PluginInfo getInfo() {
        String name = LazyBones.getTranslation("lazybones", "Lazy Bones");
        String description = LazyBones.getTranslation("desc",
                        "This plugin is a remote control for a VDR (by Klaus Schmidinger).");
        String author = "Henrik Niehaus, henrik.niehaus@gmx.de";
        return new PluginInfo(name, description, author, new Version(0, 4, false, "CVS-2007-05-04"));
    }

    
    public MainDialog getMainDialog() {
        if(mainDialog == null) {
            mainDialog = new MainDialog(getParent(), LazyBones.getTranslation(
                "lazybones", "Lazy Bones"), false, this);
        }
        return mainDialog;
    }

    
    public devplugin.SettingsTab getSettingsTab() {
        return new VDRSettingsPanel(this);
    }
    
    /**
     * Called to mark all Programs
     * 
     * @param affectedTimers
     *            An ArrayList with all timers
     * @param haltOnNoChannel
     *            Determines, if the method should terminate, if the channel
     *            couldn't be found for a timer. This is useful, if all timers
     *            have the same channel ({@see #markPrograms(ChannelDayProgram
     *            prog)})
     * @see LazyBones#markPrograms(ChannelDayProgram)
     */
    private void markPrograms(ArrayList affectedTimers, boolean haltOnNoChannel) {
        Iterator iter = affectedTimers.iterator();

        // for every timer
        while (iter.hasNext()) {
            Timer timer = (Timer) iter.next();
            devplugin.Channel chan = ProgramManager.getInstance().getChannel(timer);
            if (chan == null) {
                timer.setReason(Timer.NO_CHANNEL);

                // leave method only, if the haltOnNoChannel is true
                // this is the case, if the call comes from markPrograms(ChannelDayProgram)
                if (haltOnNoChannel) {
                    return;
                }

                // we couldn't find a channel for this timer, continue with the
                // next timer
                continue;
            }

            markSingularTimer(timer, chan);
        }
        handleNotAssignedTimers();
    }
    
    /**
     * Handles all timers, which couldn't be assigned automatically
     *
     */
    private void handleNotAssignedTimers() {
        if (Boolean.TRUE.toString().equals(
                props.getProperty("supressMatchDialog"))) {
            return;
        }
        Iterator iterator = TimerManager.getInstance().getNotAssignedTimers().iterator();
        logger.log("Not assigned timers: "
                + TimerManager.getInstance().getNotAssignedTimers().size(),
                Logger.OTHER, Logger.DEBUG);
        while (iterator.hasNext()) {
            Timer timer = (Timer) iterator.next();
            switch(timer.getReason()) {
            case Timer.NOT_FOUND:
                showProgramConfirmDialog(timer);
                break;
            case Timer.NO_EPG:
                logger.log("Couldn't assign timer: " + timer, Logger.EPG, Logger.WARN);
                String mesg = LazyBones.getTranslation("noEPGdataTVB","<html>TV-Browser has no EPG-data the timer {0}.<br>Please update your EPG-data!</html>",timer.toString());
                logger.log(mesg, Logger.EPG, Logger.ERROR);
                break;
            case Timer.NO_CHANNEL:
                mesg = LazyBones.getTranslation("no_channel_defined", "No channel defined", timer.toString()); 
                logger.log(mesg, Logger.EPG, Logger.ERROR);
                break;
            case Timer.NO_PROGRAM:
                // do nothing
                break;
            default:
                logger.log("Not assigned timer: " + timer.toString(), Logger.OTHER,
                        Logger.DEBUG);
            }
        }
    }

    /**
     * 
     * @param timer
     * @param chan
     */
    private void markSingularTimer(Timer timer, devplugin.Channel chan) {
        // create a clone of the timer and subtract the recording buffers
        Timer bufferLessTimer = (Timer) timer.clone();
        removeTimerBuffers(bufferLessTimer);

        int day = bufferLessTimer.getStartTime().get(Calendar.DAY_OF_MONTH);
        int month = bufferLessTimer.getStartTime().get(Calendar.MONTH) + 1;
        int year = bufferLessTimer.getStartTime().get(Calendar.YEAR);

        Date date = new Date(year, month, day);

        Iterator it = getPluginManager().getChannelDayProgram(date, chan);
        if (it != null) {
            // contains programs, which could be the right program for the timer
            TreeMap<Integer, Program> candidates = new TreeMap<Integer, Program>();
            
            // contains programs, which start and stop between the start and the stop time
            // of the timer and could be part of a Doppelpack
            ArrayList<Program> doppelPack = new ArrayList<Program>();
            
            while (it.hasNext()) { // iterate over all programs of one day and
                                    // compare start and end time
                Program prog = (Program) it.next();
                int startTime = prog.getStartTime();
                int endTime = startTime + prog.getLength();

                Calendar timerStartCal = bufferLessTimer.getStartTime();
                int hour = timerStartCal.get(Calendar.HOUR_OF_DAY);
                int minute = timerStartCal.get(Calendar.MINUTE);
                int timerstart = hour * 60 + minute;

                Calendar timerEndCal = bufferLessTimer.getEndTime();
                hour = timerEndCal.get(Calendar.HOUR_OF_DAY);
                minute = timerEndCal.get(Calendar.MINUTE);
                int timerend = hour * 60 + minute;
                timerend = timerend < timerstart ? timerend + 1440 : timerend;

                int deltaStart = startTime - timerstart;
                int deltaEnd = endTime - timerend;

                // MAYBE zeittoleranz als option anbieten
                // collect candidates
                if (Math.abs(deltaStart) <= 15 && Math.abs(deltaEnd) <= 15) {
                    candidates.put(new Integer(Math.abs(deltaStart)), prog);
                }
                
                // collect doppelpack candidates
                // use timer with buffers
                timerStartCal = timer.getStartTime();
                hour = timerStartCal.get(Calendar.HOUR_OF_DAY);
                minute = timerStartCal.get(Calendar.MINUTE);
                timerstart = hour * 60 + minute;
                timerEndCal = timer.getEndTime();
                hour = timerEndCal.get(Calendar.HOUR_OF_DAY);
                minute = timerEndCal.get(Calendar.MINUTE);
                timerend = hour * 60 + minute;
                timerend = timerend < timerstart ? timerend + 1440 : timerend;
                if(startTime >= timerstart && endTime <= timerend) {
                    doppelPack.add(prog);
                }
            }

            if (candidates.size() == 0) {
                if (doppelPack.size() > 1) {
                    timer.setReason(Timer.NOT_FOUND); // if no doppelpack is found, this timer
                    // is a timer with weird start and end times
                    // then we have to show a ProgramConfirmDialog, so we set
                    // the reason to not found and if a doppelpack is detected we
                    // set the reason to no_reason
                    ArrayList<String> list = new ArrayList<String>();
                    String doppelpackTitle = null;
                    for (Iterator iter = doppelPack.iterator(); iter.hasNext();) {
                        String title = ((Program)iter.next()).getTitle();
                        if(list.contains(title)) {
                            logger.log("Doppelpack found: " + title, Logger.OTHER, Logger.DEBUG);
                            timer.setReason(Timer.NO_REASON);
                            doppelpackTitle = title;
                        } else {
                            list.add(title);
                        }
                    }
                    
                    // mark all doppelpack programs
                    if(doppelpackTitle != null) {
                        for (Iterator iter = doppelPack.iterator(); iter.hasNext();) {
                            Program prog = (Program) iter.next();
                            if(prog.getTitle().equals(doppelpackTitle)) {
                                prog.mark(this);
                                timer.addTvBrowserProgID(prog.getID());
                            }
                        }

                    }
                    
                    // this timer is no doppelpack, but the times are weird
                    // we now look if the user has made a mapping before
                    if(timer.getReason() == Timer.NOT_FOUND) {
                        // lookup old mappings
                        boolean found = lookUpTimer(timer, null);
                        if (!found) { // we have no mapping
                            logger.log("Couldn't find a program with that title: "
                                    + timer.getTitle(), Logger.OTHER, Logger.WARN);
                            logger.log("Couldn't assign timer: " + timer, Logger.OTHER, Logger.WARN);
                            timer.setReason(Timer.NOT_FOUND);
                        } else {
                            timer.setReason(Timer.NO_REASON);
                        }
                    }
                } else {
                    boolean found = lookUpTimer(timer, null);
                    if (!found) {
                        timer.setReason(Timer.NOT_FOUND);
                    }
                }
                return;
            }
            
            // get the best fitting candidate. this is the first key, because
            // TreeMap is sorted
            Program progMin = (Program) candidates.get(candidates.firstKey());

            // calculate the precentage of common words
            int percentage = 0;
            if(!timer.getPath().equals("")) {
                int percentagePath = Utilities.percentageOfEquality(timer.getPath(), progMin.getTitle());
                int percentageTitle = Utilities.percentageOfEquality(timer.getTitle(), progMin.getTitle());
                percentage = Math.max(percentagePath, percentageTitle);
            } else {
                percentage = Utilities.percentageOfEquality(timer.getTitle(), progMin.getTitle());
            }

            // override the percentage
            if (timer.getFile().indexOf("EPISODE") >= 0
                    || timer.getFile().indexOf("TITLE") >= 0
                    || timer.isRepeating()) {
                percentage = 100;
            }
            
            logger.log("Percentage:"+percentage + " " + timer.toString(), Logger.OTHER, Logger.DEBUG);

            int threshold = Integer.parseInt(props.getProperty("percentageThreshold"));
            // if the percentage of common words is
            // higher than the config value percentageThreshold, mark this
            // program
            if (percentage >= threshold) {
                progMin.mark(this);
                timer.addTvBrowserProgID(progMin.getID());
                if (timer.isRepeating()) {
                    Date d = progMin.getDate();
                    timer.getStartTime().set(Calendar.DAY_OF_MONTH, d.getDayOfMonth());
                    timer.getStartTime().set(Calendar.MONTH, d.getMonth()-1);
                    timer.getStartTime().set(Calendar.YEAR, d.getYear());
                }
            } else {
                boolean found = lookUpTimer(timer, progMin);
                if (!found) { // we have no mapping
                    logger.log("Couldn't find a program with that title: "
                            + timer.getTitle(), Logger.OTHER, Logger.WARN);
                    logger.log("Couldn't assign timer: " + timer, Logger.OTHER, Logger.WARN);
                    timer.setReason(Timer.NOT_FOUND);
                }
            }
        } else { // no channeldayprogram was found
            if(!timer.isRepeating()) {
                timer.setReason(Timer.NO_EPG);
            }
        }
    }

    public void removeTimerBuffers(Timer timer) {
        int buffer_before = Integer.parseInt(props.getProperty("timer.before"));
        timer.getStartTime().add(Calendar.MINUTE, buffer_before);
        int buffer_after = Integer.parseInt(props.getProperty("timer.after"));
        timer.getEndTime().add(Calendar.MINUTE, -buffer_after);
    }

    public void onDeactivation() {
        try {
            Player.stop();
        } catch (Exception e) {
        }
    }

    public void loadSettings(Properties props) {
        LazyBones.props = props;
        
        // load data
        loadData();
        
        String host = props.getProperty("host");
        host = host == null ? "localhost" : host;
        props.setProperty("host", host);
        String streamtype = props.getProperty("streamtype");
        streamtype = streamtype == null ? "TS" : streamtype;
        props.setProperty("streamtype", streamtype);
        String port = props.getProperty("port");
        port = port == null ? "2001" : port;
        props.setProperty("port", port);
        String timeout = props.getProperty("timeout");
        timeout = timeout == null ? "500" : timeout;
        props.setProperty("timeout", timeout);
        String threshold = props.getProperty("percentageThreshold");
        threshold = threshold == null ? "45" : threshold;
        props.setProperty("percentageThreshold", threshold);

        String timer_before = props.getProperty("timer.before");
        timer_before = timer_before == null ? "5" : timer_before;
        String timer_after = props.getProperty("timer.after");
        timer_after = timer_after == null ? "10" : timer_after;
        String timer_prio = props.getProperty("timer.prio");
        timer_prio = timer_prio == null ? "50" : timer_prio;
        String timer_lifetime = props.getProperty("timer.lifetime");
        timer_lifetime = timer_lifetime == null ? "50" : timer_lifetime;
        props.setProperty("timer.before", timer_before);
        props.setProperty("timer.after", timer_after);
        props.setProperty("timer.prio", timer_prio);
        props.setProperty("timer.lifetime", timer_lifetime);
        
        String numberOfCards = props.getProperty("numberOfCards");
        numberOfCards = numberOfCards == null ? "1" : numberOfCards;
        props.setProperty("numberOfCards", numberOfCards);

        String preview_url = props.getProperty("preview.url");
        preview_url = preview_url == null ? "http://localhost:8000/preview.jpg" : preview_url;
        String preview_path = props.getProperty("preview.path");
        preview_path = preview_path == null ? "/pub/web/preview.jpg" : preview_path;
        String preview_method = props.getProperty("preview.method");
        preview_method = preview_method == null ? "HTTP" : preview_method;
        props.setProperty("preview.url", preview_url);
        props.setProperty("preview.path", preview_path);
        props.setProperty("preview.method", preview_method);

        String switchBefore = props.getProperty("switchBefore");
        switchBefore = switchBefore == null ? "false" : switchBefore;
        props.setProperty("switchBefore", switchBefore);
        
        String logConnectionErrors = props.getProperty("logConnectionErrors");
        logConnectionErrors = logConnectionErrors == null ? "true" : logConnectionErrors;
        props.setProperty("logConnectionErrors", logConnectionErrors);
        Logger.logConnectionErrors = new Boolean(logConnectionErrors).booleanValue();
        
        String logEPGErrors = props.getProperty("logEPGErrors");
        logEPGErrors = logEPGErrors == null ? "true" : logEPGErrors;
        props.setProperty("logEPGErrors", logEPGErrors);
        Logger.logEPGErrors = new Boolean(logEPGErrors).booleanValue();
        
        String showTimerOptionsDialog = props.getProperty("showTimerOptionsDialog");
        showTimerOptionsDialog = showTimerOptionsDialog == null ? "true" : showTimerOptionsDialog;
        props.setProperty("showTimerOptionsDialog", showTimerOptionsDialog);
        
        String minChannelNumber = props.getProperty("minChannelNumber");
        minChannelNumber = minChannelNumber == null ? "0" : minChannelNumber;
        props.setProperty("minChannelNumber", minChannelNumber);
        String maxChannelNumber = props.getProperty("maxChannelNumber");
        maxChannelNumber = maxChannelNumber == null ? "0" : maxChannelNumber;
        props.setProperty("maxChannelNumber", maxChannelNumber);
        
        VDRConnection.host = host;
        VDRConnection.port = Integer.parseInt(port);
        VDRConnection.timeout = Integer.parseInt(timeout);
        
        init();
    }
    
    @SuppressWarnings("unchecked")
    private void loadData() {
        XStream xstream = new XStream();
        
        // load title mapping
        try {
            HashMap titleMapping = (HashMap) xstream.fromXML(props.getProperty("titleMapping"));
            TimerManager.getInstance().setTitleMappingValues(titleMapping);
        } catch (Exception e) {
            logger.log("Couldn't load title mapping: " + e, Logger.OTHER, Logger.WARN);
        }
        
        // load channel mapping
        try {
            Hashtable channelMapping = (Hashtable) xstream.fromXML(props.getProperty("channelMapping"));
            ProgramManager.setChannelMapping(channelMapping);
        } catch (Exception e) {
            logger.log("Couldn't load channel mapping: " + e, Logger.OTHER, Logger.WARN);
        }
        
        // load timers
        try {
            ArrayList timers = (ArrayList) xstream.fromXML(props.getProperty("timers"));
            TimerManager.getInstance().setStoredTimers(timers);
        } catch (Exception e) {
            logger.log("Couldn't load timers: " + e, Logger.OTHER, Logger.WARN);
        }
        
        // load channel list
        try {
            List channelList = (List) xstream.fromXML(props.getProperty("channelList")); 
            VDRChannelList.getInstance().setChannels(channelList);
        } catch (Exception e) {
            logger.log("Couldn't load channel list: " + e, Logger.OTHER, Logger.WARN);
        }
        
        // remove outdated timers
        Calendar today = GregorianCalendar.getInstance();
        for (ListIterator iter = TimerManager.getInstance().getStoredTimers()
                .listIterator(); iter.hasNext();) {
            Timer timer = (Timer) iter.next();
            if (timer.getEndTime().before(today) & !timer.isRepeating()) {
                iter.remove();
            }
        }
    }

    public void handleTvBrowserStartFinished() {
        // upload channel list from vdr
        logger.log("Updating channel list", Logger.OTHER, Logger.DEBUG);
        VDRChannelList.getInstance().update();
        
        // get all timers from vdr
        TimerManager.getInstance().synchronize();
    }
    
    private void init() {
        instance = this;
        
        // observe the timer list
        TimerManager.getInstance().addObserver(this);
    }

    public Properties storeSettings() {
        storeData();        
        return props;
    }

    private void storeData() {
        XStream xstream = new XStream();
        props.setProperty( "channelMapping", xstream.toXML(ProgramManager.getChannelMapping()) );
        props.setProperty( "timers", xstream.toXML(TimerManager.getInstance().getTimers()) );
        props.setProperty( "titleMapping", xstream.toXML(TimerManager.getInstance().getTitleMappingValues()) );
        props.setProperty( "channelList", xstream.toXML(VDRChannelList.getInstance().getChannels()) );
    }

    public static Properties getProperties() {
        return props;
    }

    public Icon getIcon(String path) {
        return createImageIcon(path);
    }

    public void handleTvDataAdded(ChannelDayProgram newProg) {
        markPrograms(newProg);
    }

    /**
     * Called if handleTVData* has been called.
     * In this case only one channel has to be marked again
     * @param prog the ChannelDayProgram, which has to be marked
     */
    private void markPrograms(ChannelDayProgram prog) {
        ArrayList<Timer> affectedTimers = new ArrayList<Timer>();
        Iterator iterator = TimerManager.getInstance().getTimers().iterator();
        while (iterator.hasNext()) {
            Timer timer = (Timer) iterator.next();
            devplugin.Channel timerChannel = ProgramManager.getInstance().getChannel(timer);
            devplugin.Channel progChannel = prog.getChannel();
            
            // timer couldn't be assigned before
            if(!timer.isAssigned()) {
                return;
            } 
            
            // timer channel couldn't be found
            // no channel available for this timer
            if(timerChannel == null) {
                return;
            } 
            
            // timer channel and prog channel are equal
            // add this timer to affected timers
            if (timerChannel.equals(progChannel)) {
                affectedTimers.add(timer);
            }
        }

        // markPrograms only for one channel -> haltOnNoChannel = true
        logger.log("Affected timers " + affectedTimers.size(), Logger.OTHER, Logger.DEBUG);
        markPrograms(affectedTimers, true);
    }

    public void handleTvDataDeleted(ChannelDayProgram oldProg) {
        markPrograms(oldProg);
    }

    public Frame getParent() {
        return getParentFrame();
    }

    /**
     * Updates the pluginview of TV-Browser
     */
    public void updateTree() {
        PluginTreeNode node = getRootNode();
        node.removeAllActions();
        node.removeAllChildren();
        
        Iterator it = TimerManager.getInstance().getTimers().iterator();
        while(it.hasNext()) {
            Timer timer = (Timer)it.next();
            if(timer.isAssigned()) {
                Timer bufferLess = (Timer)timer.clone();
                removeTimerBuffers(bufferLess);
                for (Iterator iter = timer.getTvBrowserProgIDs().iterator(); iter.hasNext();) {
                    String progID = (String)iter.next();
                    Program prog = ProgramManager.getInstance().getProgram(timer.getStartTime(), progID);
                    if(prog != null) {
                        node.addProgram(prog);
                    } else { // can be null, if program time is near 00:00, because then
                             // the wrong day is taken to ask tvb for the programm
                        prog = ProgramManager.getInstance().getProgram(timer.getEndTime(), progID);
                        if(prog != null) {
                            node.addProgram(prog);
                        }
                    }
                }
            }
        }

        node.update();
    }

    public boolean canUseProgramTree() {
        return true;
    }

    public void editTimer(Timer timer) {
        new TimerOptionsDialog(this, timer, null, true);
    }

    /*
     * public boolean canReceivePrograms() { return true; }
     * 
     * public void receivePrograms(Program[] progs) {
     * if(VDRConnection.isAvailable()) { for (int i = 0; i < progs.length; i++) {
     * createTimer(progs[i]); } } else { JOptionPane.showMessageDialog(null,
     * "Couldn't connect to VDR"); } }
     */

    /**
     * Filters a list of EPGEntries by a provided VDR channel name and a time
     * which has to be in between a EPGEntries start and end time.
     * 
     * @param epgList
     *            list of EPGEntries retrieved from VDR
     * @param vdrChannelName
     *            VDR channel name which has to match a EPGEntry channel name
     * @param middleTime
     *            time of a program which has to be between start and end time
     *            of a EPGEntry
     * @return EPGEntry from the list which matches channel and time
     */
    private static EPGEntry filterEPGDate(List epgList, String vdrChannelName,
            long middleTime) {
        for (Iterator iter = epgList.iterator(); iter.hasNext();) {
            EPGEntry element = (EPGEntry) iter.next();
            if (element.getStartTime().getTimeInMillis() <= middleTime
                    && middleTime <= element.getEndTime().getTimeInMillis()
                    && vdrChannelName.equals(element.getChannelName())) {
                return element;
            }
        }
        return null;
    }
    
    private boolean lookUpTimer(Timer timer, Program candidate) {
        logger.log("Looking in storedTimers for: " + timer.toString(),Logger.OTHER, Logger.DEBUG);
        ArrayList<String> progIDs = TimerManager.getInstance().hasBeenMappedBefore(timer);
        if (progIDs != null) { // we have a mapping of this timer to a program
            for (Iterator iter = progIDs.iterator(); iter.hasNext();) {
                String progID = (String) iter.next();

                if(progID.equals("NO_PROGRAM")) {
                    logger.log("Timer " + timer.toString()+" should never be assigned",Logger.OTHER, Logger.DEBUG);
                    timer.setReason(Timer.NO_PROGRAM);
                    return true;
                } else {
                    devplugin.Channel c = ProgramManager.getInstance().getChannel(timer);
                    if (c != null) {
                        Date date = new Date(timer.getStartTime());
                        Iterator iterator = getPluginManager()
                                .getChannelDayProgram(date, c);
                        while (iterator.hasNext()) {
                            Program p = (Program) iterator.next();
                            if (p.getID().equals(progID)
                                    && p.getDate().equals(date)) {
                                p.mark(this);
                                timer.setTvBrowserProgIDs(progIDs);
                                logger.log("Mapping found for: " + timer.toString(),
                                        Logger.OTHER, Logger.DEBUG);
                                return true;
                            }
                        }
                    }
                }
            }
        } else  {
            logger.log("No mapping found for: " + timer.toString(),Logger.OTHER, Logger.DEBUG);
            if(candidate != null) {
                logger.log("Looking up old mappings", Logger.OTHER, Logger.DEBUG);
                TimerManager tm = TimerManager.getInstance();
                String progTitle = (String)tm.getTitleMapping().getTvbTitle(timer.getTitle());
                if(candidate.getTitle().equals(progTitle)) {
                    candidate.mark(this);
                    timer.addTvBrowserProgID(candidate.getID());
                    logger.log("Old mapping found for: " + timer.toString(),
                            Logger.OTHER, Logger.DEBUG);
                    return true;
                }
            }
        }

        return false;
    }
    
    public static String getTranslation(String key, String altText) {
        return mLocalizer.msg(key,altText);
    }
    
    public static String getTranslation(String key, String altText, String arg1) {
        return mLocalizer.msg(key,altText, arg1);
    }
    
    public static String getTranslation(String key, String altText, String arg1, String arg2) {
        return mLocalizer.msg(key,altText, arg1, arg2);
    }
    
    public ImageIcon getImageIcon(String name) {
        return super.createImageIcon(name);
    }
    
    public JPopupMenu getSimpleContextMenu(Timer timer) {
        return cmf.createSimpleActionMenu(timer);
    }
    
    public void update(Observable arg0, Object arg1) {
        // mark all "timed" programs (haltOnNoChannel = false, because we can
        // have multiple channels)
        markPrograms(TimerManager.getInstance().getTimers(), false);

        // update the plugin tree
        updateTree();
    }
    
    public void synchronize() {
        TimerManager.getInstance().synchronize();
        RecordingManager.getInstance().synchronize();
    }
    
    // TODO standard icons von tvbrowser nehmen und über createImageIcon laden
    private class ContextMenuFactory {
        public ActionMenu createActionMenu(final Program program) {
            AbstractAction action = new AbstractAction() {
                public void actionPerformed(ActionEvent evt) {

                }
            };
            action.putValue(Action.NAME, LazyBones.getTranslation("lazybones", "Lazy Bones"));
            action.putValue(Action.SMALL_ICON, createImageIcon("lazybones/vdr16.png"));

            Marker[] markers = program.getMarkerArr();
            boolean marked = false;
            for (int i = 0; i < markers.length; i++) {
                if (markers[i].getId().equals(getId())) {
                    marked = true;
                    break;
                }
            }

            int size = 3;
            if(marked) {
                size++;
            }
            
            Action[] actions = null;
            if (marked) {
                actions = new Action[size];

                actions[1] = new AbstractAction() {
                    public void actionPerformed(ActionEvent evt) {
                        TimerManager.getInstance().deleteTimer(program);
                    }
                };
                actions[1].putValue(Action.NAME, LazyBones.getTranslation("dont_capture", "Delete timer"));
                actions[1].putValue(Action.SMALL_ICON, createImageIcon("lazybones/cancel.png"));

                actions[2] = new AbstractAction() {
                    public void actionPerformed(ActionEvent evt) {
                        Timer timer = (Timer) TimerManager.getInstance().getTimer(program.getID());
                        editTimer(timer);
                    }
                };
                actions[2].putValue(Action.NAME, LazyBones.getTranslation("edit", "Edit Timer"));
                actions[2].putValue(Action.SMALL_ICON, createImageIcon("lazybones/edit.png"));

                actions[3] = new AbstractAction() {
                    public void actionPerformed(ActionEvent evt) {
                        LazyBones.getInstance().synchronize();
                    }
                };
                actions[3].putValue(Action.NAME, LazyBones.getTranslation("resync", "Synchronize with VDR"));
                actions[3].putValue(Action.SMALL_ICON, createImageIcon("lazybones/reload.png"));
            } else {
                actions = new Action[size];

                actions[1] = new AbstractAction() {
                    public void actionPerformed(ActionEvent evt) {
                        createTimer(program);
                    }
                };
                actions[1].putValue(Action.NAME, LazyBones.getTranslation("capture", "Capture with VDR"));
                actions[1].putValue(Action.SMALL_ICON, createImageIcon("lazybones/capture.png"));

                actions[2] = new AbstractAction() {
                    public void actionPerformed(ActionEvent evt) {
                        LazyBones.getInstance().synchronize();
                    }
                };
                actions[2].putValue(Action.NAME, LazyBones.getTranslation("resync", "Synchronize with VDR"));
                actions[2].putValue(Action.SMALL_ICON, createImageIcon("lazybones/reload.png"));
            }

            actions[0] = new AbstractAction() {
                public void actionPerformed(ActionEvent evt) {
                    watch(program);
                }
            };
            actions[0].putValue(Action.NAME, LazyBones.getTranslation("watch", "Watch this channel"));
            actions[0].putValue(Action.SMALL_ICON, createImageIcon("lazybones/play.png"));

            return new ActionMenu(action, actions);
        }
        
        JPopupMenu simpleMenu;
        public JPopupMenu createSimpleActionMenu(final Timer timer) {
            if(simpleMenu == null) {
                simpleMenu = new JPopupMenu();
                JMenuItem delItem = new JMenuItem(LazyBones.getTranslation("dont_capture", "Delete timer"), createImageIcon("lazybones/cancel.png"));
                delItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        TimerManager.getInstance().deleteTimer(timer);
                    }
                    
                });
                
                JMenuItem editItem = new JMenuItem(LazyBones.getTranslation("edit", "Edit Timer"), createImageIcon("lazybones/edit.png"));
                editItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        editTimer(timer);
                    }
                    
                });
                
                simpleMenu.add(editItem);
                simpleMenu.add(delItem);
            }
            
            return simpleMenu;
        }
        
    }
}