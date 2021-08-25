/*
 * Copyright (c) Henrik Niehaus
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
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.CHAN;
import org.hampelratte.svdrp.commands.NEWT;
import org.hampelratte.svdrp.responses.highlevel.Channel;
import org.hampelratte.svdrp.responses.highlevel.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

import devplugin.ActionMenu;
import devplugin.ButtonAction;
import devplugin.Marker;
import devplugin.Plugin;
import devplugin.PluginCenterPanel;
import devplugin.PluginCenterPanelWrapper;
import devplugin.PluginInfo;
import devplugin.PluginTreeNode;
import devplugin.Program;
import devplugin.ProgramReceiveTarget;
import devplugin.Version;
import lazybones.gui.MainDialog;
import lazybones.gui.RecordingsCenterPanel;
import lazybones.gui.TimelineCenterPanel;
import lazybones.gui.TimersCenterPanel;
import lazybones.gui.settings.DescriptionSelectorItem;
import lazybones.gui.settings.SeriesTitleSelectorItem;
import lazybones.gui.settings.VDRSettingsPanel;
import lazybones.logging.DebugConsoleHandler;
import lazybones.logging.PopupHandler;
import lazybones.logging.SimpleFormatter;
import lazybones.programmanager.ProgramDatabase;
import lazybones.programmanager.ProgramManager;
import tvbrowser.core.Settings;

/**
 * A remote control plugin for VDR
 *
 * @author <a href="hampelratte@users.sf.net">hampelratte@users.sf.net</a>
 *
 */
public class LazyBones extends Plugin implements TimersChangedListener {

    private static Logger logger = LoggerFactory.getLogger(LazyBones.class);

    /** Translator */
    private static final util.i18n.Localizer mLocalizer = util.i18n.Localizer.getLocalizerFor(LazyBones.class);

    private MainDialog mainDialog;

    private static Properties props;

    private final ContextMenuFactory cmf = new ContextMenuFactory();

    private static LazyBones instance;

    public static final String TIMER_MENU_KEY = "TIMER_MENU_KEY";

    private PluginCenterPanelWrapper wrapper;

    private final TimerManager timerManager;
    private final RecordingManager recordingManager;

    public LazyBones() {
        timerManager = new TimerManager();
        recordingManager = new RecordingManager();
        timerManager.setRecordingManager(recordingManager);
        recordingManager.setTimerManager(timerManager);
    }

    public static LazyBones getInstance() {
        return instance;
    }

    @Override
    public ActionMenu getContextMenuActions(final Program program) {
        return cmf.createActionMenu(program);
    }

    @Override
    public ActionMenu getContextMenuActions(final devplugin.Channel channel) {
        return cmf.createChannelActionMenu(channel);
    }

    @Override
    public PluginCenterPanelWrapper getPluginCenterPanelWrapper() {
        return wrapper;
    }

    @Override
    public ActionMenu getButtonAction() {
        var buttonAction = new ButtonAction();
        buttonAction.setActionListener(e -> getMainDialog().setVisible(true));
        buttonAction.setBigIcon(createImageIcon("lazybones/vdr24.png"));
        buttonAction.setSmallIcon(createImageIcon("lazybones/vdr16.png"));
        buttonAction.setShortDescription(LazyBones.getTranslation("lazybones", "Lazy Bones"));
        buttonAction.setText(LazyBones.getTranslation("lazybones", "Lazy Bones"));
        return new ActionMenu(buttonAction);
    }

    @Override
    public String getPluginCategory() {
        return Plugin.CATEGORY_REMOTE_CONTROL_SOFTWARE;
    }

    /**
     * Called by TimerSelectionDialog, if a VDR-Program has been selected
     *
     * @param selectedProgram
     */
    public void timerSelectionCallBack(Program selectedProgram) {
        int bufferBefore = Integer.parseInt(props.getProperty("timer.before"));
        int bufferAfter = Integer.parseInt(props.getProperty("timer.after"));

        LazyBonesTimer t = ((TimerProgram) selectedProgram).getTimer();
        // start the recording x min before the beggining of the program
        t.getStartTime().add(Calendar.MINUTE, -bufferBefore);
        // stop the recording x min after the end of the program
        t.getEndTime().add(Calendar.MINUTE, bufferAfter);
        Response response = VDRConnection.send(new NEWT(t));
        if (response.getCode() == 250) {
            timerManager.assignProgramToTimer(selectedProgram, t);
        } else {
            logger.error(LazyBones.getTranslation("couldnt_create", "Couldn\'t create timer:") + " " + response.getMessage()); // NOSONAR
        }
    }

    @Override
    public String getMarkIconName() {
        return "lazybones/vdr16.png";
    }

    @Override
    public PluginInfo getInfo() {
        String name = LazyBones.getTranslation("lazybones", "Lazy Bones");
        String description = LazyBones.getTranslation("desc", "This plugin is a remote control for a VDR (by Klaus Schmidinger).");
        String author = "Henrik Niehaus, henrik.niehaus@gmx.de";
        return new PluginInfo(getClass(), name, description, author, "BSD", "http://hampelratte.org/blog/?page_id=6");
    }

    public static Version getVersion() {
        return new Version(1, 60, 0, true);
    }

    public MainDialog getMainDialog() {
        if (mainDialog == null) {
            mainDialog = new MainDialog(getParent(), timerManager, recordingManager);
        }
        return mainDialog;
    }

    @Override
    public devplugin.SettingsTab getSettingsTab() {
        return new VDRSettingsPanel(timerManager);
    }

    @Override
    public void onDeactivation() {
        String surviveOnExit = props.getProperty("surviveOnExit");
        if (Boolean.FALSE.toString().toLowerCase().equals(surviveOnExit)) {
            try {
                Player.stop();
            } catch (Exception e) {
            	// fail silently, it's fine
            }
        }
    }

    @Override
    public void loadSettings(Properties props) { // NOSONAR
        LazyBones.props = props;

        // load data
        loadData();

        String host = props.getProperty("host");
        host = host == null ? "localhost" : host;
        props.setProperty("host", host);
        String charset = props.getProperty("charset");
        charset = charset == null ? "UTF-8" : charset;
        props.setProperty("charset", charset);
        String streamurl = props.getProperty("streamurl");
        streamurl = streamurl == null ? "http://<host>:3000/<streamtype>/<channel>" : streamurl;
        props.setProperty("streamurl", streamurl);
        String streamtype = props.getProperty("streamtype");
        streamtype = streamtype == null ? "TS" : streamtype;
        props.setProperty("streamtype", streamtype);
        String port = props.getProperty("port");
        port = port == null ? "6419" : port;
        props.setProperty("port", port);
        String timeout = props.getProperty("timeout");
        timeout = timeout == null ? "500" : timeout;
        props.setProperty("timeout", timeout);
        String threshold = props.getProperty("percentageThreshold");
        threshold = threshold == null ? "45" : threshold;
        props.setProperty("percentageThreshold", threshold);

        String timerBefore = props.getProperty("timer.before");
        timerBefore = timerBefore == null ? "5" : timerBefore;
        String timerAfter = props.getProperty("timer.after");
        timerAfter = timerAfter == null ? "10" : timerAfter;
        String timerPrio = props.getProperty("timer.prio");
        timerPrio = timerPrio == null ? "50" : timerPrio;
        String timerLifetime = props.getProperty("timer.lifetime");
        timerLifetime = timerLifetime == null ? "50" : timerLifetime;
        props.setProperty("timer.before", timerBefore);
        props.setProperty("timer.after", timerAfter);
        props.setProperty("timer.prio", timerPrio);
        props.setProperty("timer.lifetime", timerLifetime);

        String vpsDefault = props.getProperty("vps.default");
        vpsDefault = vpsDefault == null ? "false" : vpsDefault;
        props.setProperty("vps.default", vpsDefault);

        String numberOfCards = props.getProperty("numberOfCards");
        numberOfCards = numberOfCards == null ? "1" : numberOfCards;
        props.setProperty("numberOfCards", numberOfCards);

        String previewUrl = props.getProperty("preview.url");
        previewUrl = previewUrl == null ? "http://localhost:8000/preview.jpg" : previewUrl;
        String previewPath = props.getProperty("preview.path");
        previewPath = previewPath == null ? "/pub/web/preview.jpg" : previewPath;
        String previewMethod = props.getProperty("preview.method");
        previewMethod = previewMethod == null ? "SVDRP" : previewMethod;
        props.setProperty("preview.url", previewUrl);
        props.setProperty("preview.path", previewPath);
        props.setProperty("preview.method", previewMethod);

        String switchBefore = props.getProperty("switchBefore");
        switchBefore = switchBefore == null ? "false" : switchBefore;
        props.setProperty("switchBefore", switchBefore);

        String surviveOnExit = props.getProperty("surviveOnExit");
        surviveOnExit = surviveOnExit == null ? "false" : surviveOnExit;
        props.setProperty("surviveOnExit", surviveOnExit);

        String recordingURL = props.getProperty("recording.url");
        recordingURL = recordingURL == null ? "http://<host>:3000/TS/<recording_number>.rec.ts" : recordingURL;
        props.setProperty("recording.url", recordingURL);

        String logConnectionErrors = props.getProperty("logConnectionErrors");
        logConnectionErrors = logConnectionErrors == null ? "true" : logConnectionErrors;
        props.setProperty("logConnectionErrors", logConnectionErrors);

        String logEPGErrors = props.getProperty("logEPGErrors");
        logEPGErrors = logEPGErrors == null ? "true" : logEPGErrors;
        props.setProperty("logEPGErrors", logEPGErrors);

        String showTimerOptionsDialog = props.getProperty("showTimerOptionsDialog");
        showTimerOptionsDialog = showTimerOptionsDialog == null ? "true" : showTimerOptionsDialog;
        props.setProperty("showTimerOptionsDialog", showTimerOptionsDialog);

        String descSourceTvb = props.getProperty("descSourceTvb");
        descSourceTvb = descSourceTvb == null ? DescriptionSelectorItem.LONGEST : descSourceTvb;
        props.setProperty("descSourceTvb", descSourceTvb);

        String seriesTitle = props.getProperty("timer.series.title");
        seriesTitle = seriesTitle == null ? SeriesTitleSelectorItem.VDR : seriesTitle;
        props.setProperty("timer.series.title", seriesTitle);

        String minChannelNumber = props.getProperty("minChannelNumber");
        minChannelNumber = minChannelNumber == null ? "0" : minChannelNumber;
        props.setProperty("minChannelNumber", minChannelNumber);
        String maxChannelNumber = props.getProperty("maxChannelNumber");
        maxChannelNumber = maxChannelNumber == null ? "0" : maxChannelNumber;
        props.setProperty("maxChannelNumber", maxChannelNumber);

        String timelineStartHour = props.getProperty("timelineStartHour");
        timelineStartHour = timelineStartHour == null ? "5" : timelineStartHour;
        props.setProperty("timelineStartHour", timelineStartHour);

        VDRConnection.host = host;
        VDRConnection.port = Integer.parseInt(port);
        VDRConnection.timeout = Integer.parseInt(timeout);
        VDRConnection.charset = charset;
        VDRConnection.persistentConnection = true;

        init();
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        XStream xstream = new XStream();

        // load title mapping
        try {
            Map<String, String> titleMapping = (HashMap<String, String>) xstream.fromXML(props.getProperty("titleMapping"));
            timerManager.getTitleMapping().setMappingFromMap(titleMapping);
        } catch (Exception e) {
            logger.warn("Couldn't load title mapping", e);
        }

        // load channel mapping
        try {
            Map<String, Channel> channelMapping = (Hashtable<String, Channel>) xstream.fromXML(props.getProperty("channelMapping")); // NOSONAR this gets serialized, can't change the type
            ChannelManager.setChannelMapping(channelMapping);
        } catch (Exception e) {
            logger.warn("Couldn't load channel mapping", e);
        }

        // load timers
        try {
            List<LazyBonesTimer> timers = (ArrayList<LazyBonesTimer>) xstream.fromXML(props.getProperty("timers"));
            timerManager.setStoredTimers(timers);
        } catch (Exception e) {
            logger.warn("Couldn't load timers", e);
        }

        // load channel list
        try {
            List<Channel> channelList = (List<Channel>) xstream.fromXML(props.getProperty("channelList"));
            ChannelManager.getInstance().setChannels(channelList);
        } catch (Exception e) {
            logger.warn("Couldn't load channel list", e);
        }

        // remove outdated timers
        Calendar today = Calendar.getInstance();
        for (Iterator<LazyBonesTimer> iter = timerManager.getStoredTimers().iterator(); iter.hasNext();) {
            LazyBonesTimer timer = iter.next();
            if (timer.getEndTime().before(today) & !timer.isRepeating()) { // NOSONAR java:S2178
                iter.remove();
            }
        }
    }

    @Override
    public void handleTvBrowserStartFinished() {
        // upload channel list from vdr
        logger.debug("Updating channel list");
        ChannelManager.getInstance().update();

        // synchronize timers and recordings
        synchronize();
    }

    private void init() {
        instance = this;

        wrapper = new PluginCenterPanelWrapper() {
            //@formatter:off
            PluginCenterPanel[] panels =  new PluginCenterPanel[] {
                    new TimelineCenterPanel(timerManager),
                    new TimersCenterPanel(timerManager, recordingManager),
                    new RecordingsCenterPanel(recordingManager)/*,
                    new RemoteControlCenterPanel()*/
            };
            //@formatter:on

            @Override
            public PluginCenterPanel[] getCenterPanels() {
                return panels;
            }
        };

        // observe the timer list
        timerManager.addTimersChangedListener(this);

        // initialize logging
        initLogging();
    }

    private void initLogging() {
        String logDirectory = Settings.propLogdirectory.getString();
        if (logDirectory != null && System.getProperty("java.util.logging.config.file") == null) {
		    // no logging config file is set, so we can adjust the logging level by ourselves
		    java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
		    rootLogger.setLevel(Level.FINE);
		    logger.info("No logging configuration defined. Setting logging level to Level.FINE");
		}

        // create our special logging components
        SimpleFormatter formatter = new SimpleFormatter();
        Handler eph = new PopupHandler();
        Handler dch = new DebugConsoleHandler();
        dch.setLevel(Level.FINEST);

        // add our special handlers to all lazybones.* messages
        LoggerFactory.getLogger("lazybones");
        java.util.logging.Logger lazyLogger = java.util.logging.Logger.getLogger("lazybones");
        lazyLogger.addHandler(eph);
        lazyLogger.addHandler(dch);
        eph.setFormatter(formatter);

        // add our special handlers to all svdrp messages
        LoggerFactory.getLogger("org.hampelratte.svdrp");
        java.util.logging.Logger svdrpLogger = java.util.logging.Logger.getLogger("org.hampelratte.svdrp");
        svdrpLogger.setLevel(Level.FINE);
        svdrpLogger.addHandler(dch);

        // add our special handlers to the popuplogger
        LoggerFactory.getLogger(PopupHandler.KEYWORD);
        java.util.logging.Logger popupLogger = java.util.logging.Logger.getLogger(PopupHandler.KEYWORD);
        popupLogger.addHandler(eph);
        popupLogger.addHandler(dch);
        popupLogger.setLevel(Level.INFO);
        eph.setLevel(Level.INFO);

        // create a custom file handler only for lazy bones
        if (logDirectory != null) {
            Handler fh = null;
            try {
                fh = new FileHandler(new File(logDirectory, "lazybones.log").getAbsolutePath());
                fh.setFormatter(formatter);
                fh.setLevel(Level.FINE);
            } catch (Exception e) {
                logger.warn("Couldn't add file handler for Lazy Bones", e);
            }

            if (fh != null) {
                lazyLogger.addHandler(fh);
                svdrpLogger.addHandler(fh);
                popupLogger.addHandler(fh);
            }
        }
    }

    @Override
    public Properties storeSettings() {
        storeData();
        return props;
    }

    private void storeData() {
        XStream xstream = new XStream();
        props.setProperty("channelMapping", xstream.toXML(ChannelManager.getChannelMapping()));
        props.setProperty("timers", xstream.toXML(timerManager.getTimers()));
        props.setProperty("titleMapping", xstream.toXML(timerManager.getTitleMapping().getAsMap()));
        props.setProperty("channelList", xstream.toXML(ChannelManager.getInstance().getChannels()));
    }

    public static Properties getProperties() {
        return props;
    }

    public Icon getIcon(String path) {
        return createImageIcon(path);
    }

    @Override
    public void handleTvDataUpdateFinished() {
        ProgramManager.getInstance().markPrograms(timerManager);
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

        for (LazyBonesTimer timer : timerManager.getTimers()) {
            if (timer.isAssigned()) {
            	addProgramsToPluginTree(node, timer.getTvBrowserProgIDs());
            }
        }

        node.update();
    }

    private void addProgramsToPluginTree(PluginTreeNode node, List<String> tvBrowserProgIDs) {
    	for (String progID : tvBrowserProgIDs) {
    		Program prog = ProgramDatabase.getProgram(progID);
    		if (prog != null) {
    			node.addProgram(prog);
    		} else { // can be null, if program time is near 00:00, because then
    			// the wrong day is taken to ask tvb for the programm
    			prog = ProgramDatabase.getProgram(progID);
    			if (prog != null) {
    				node.addProgram(prog);
    			}
    		}
    	}
	}

	@Override
    public boolean canUseProgramTree() {
        return true;
    }

    public static String getTranslation(String key, String altText) {
        return mLocalizer.msg(key, altText);
    }

    public static String getTranslation(String key, String altText, String arg1) {
        return mLocalizer.msg(key, altText, arg1);
    }

    public static String getTranslation(String key, String altText, String arg1, String arg2) {
        return mLocalizer.msg(key, altText, arg1, arg2);
    }

    public static String getTranslation(String key, String altText, String arg1, String arg2, String arg3) {
        return mLocalizer.msg(key, altText, arg1, arg2, arg3);
    }

    public JPopupMenu getSimpleContextMenu(LazyBonesTimer timer) {
        return cmf.createSimpleActionMenu(timer);
    }

    @Override
    public void timersChanged(TimersChangedEvent evt) {
    	updateTree();
    }
    
    public void synchronize() {
        timerManager.synchronize();
        recordingManager.synchronize();
    }

    private class ContextMenuFactory {
        public ActionMenu createChannelActionMenu(final devplugin.Channel chan) {
            final Channel vdrChan = ChannelManager.getChannelMapping().get(chan.getId());
            if (vdrChan == null) {
                // selected channel is not mapped to VDR channel, so it does not make sense to show a menu at all
                return null;
            }

            ActionMenu[] actions = new ActionMenu[2];
            AbstractAction watch = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Player.play(vdrChan.getChannelNumber());
                }
            };
            watch.putValue(Action.NAME, LazyBones.getTranslation("watch", "Watch this channel"));
            watch.putValue(Action.SMALL_ICON, createImageIcon("actions", "media-playback-start", 16));
            actions[0] = new ActionMenu(watch);

            AbstractAction switchToChan = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    // switch to channel
                    logger.info("Switch to channel {}", vdrChan.getName());
                    CHAN chan = new CHAN(Integer.toString(vdrChan.getChannelNumber()));
                    Response resp = VDRConnection.send(chan);
                    if (resp.getCode() != 250) {
                        logger.error(LazyBones.getTranslation("couldnt_switch", "Couldn't switch to channel", resp.getMessage()));
                    }
                }
            };
            switchToChan.putValue(Action.NAME, LazyBones.getTranslation("switch_to", "Switch to this channel"));
            switchToChan.putValue(Action.SMALL_ICON, createImageIcon("lazybones/remote-control.png"));
            actions[1] = new ActionMenu(switchToChan);

            String name = LazyBones.getTranslation("lazybones", "Lazy Bones");
            ImageIcon icon = createImageIcon("lazybones/vdr16.png");
            return new ActionMenu(name, icon, actions);
        }

        public ActionMenu createActionMenu(final Program program) {
            final Channel vdrChan = ChannelManager.getChannelMapping().get(program.getChannel().getId());
            if (vdrChan == null) {
                // selected channel is not mapped to VDR channel, so it does not make sense to show a menu at all
                return null;
            }

            Marker[] markers = program.getMarkerArr();
            boolean marked = false;
            for (int i = 0; i < markers.length; i++) {
                if (markers[i].getId().equals(getId())) {
                    marked = true;
                    break;
                }
            }

            boolean notAssignedTimersExist = !timerManager.getNotAssignedTimers().isEmpty();

            int size = 4;

            if (marked || notAssignedTimersExist) {
                size++;
            }

            List<ActionMenu> actions = new ArrayList<>(size);
            AbstractAction watch = new AbstractAction() {
            	@Override
            	public void actionPerformed(ActionEvent evt) {
            		Player.play(program);
            	}
            };
            watch.putValue(Action.NAME, LazyBones.getTranslation("watch", "Watch this channel"));
            watch.putValue(Action.SMALL_ICON, createImageIcon("actions", "media-playback-start", 16));
            actions.add(new ActionMenu(watch));
            
            if (marked) {
            	addActionMenuForMarkedProgram(actions, program);
            } else {
                addActionMenuForUnmarkedProgram(actions, program, notAssignedTimersExist);
            }

            AbstractAction switchToChan = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    // switch to channel
                    devplugin.Channel tvbChan = program.getChannel();
                    Channel vdrChan = ChannelManager.getChannelMapping().get(tvbChan.getId());
                    if (vdrChan != null) {
                        logger.info("Swtich to channel {} of program {}", vdrChan.getName(), program.getTitle());
                        CHAN chan = new CHAN(Integer.toString(vdrChan.getChannelNumber()));
                        Response resp = VDRConnection.send(chan);
                        if (resp.getCode() != 250) {
                            logger.error(LazyBones.getTranslation("couldnt_switch", "Couldn't switch to channel", resp.getMessage()));
                        }
                    }
                }
            };
            switchToChan.putValue(Action.NAME, LazyBones.getTranslation("switch_to", "Switch to this channel"));
            switchToChan.putValue(Action.SMALL_ICON, createImageIcon("lazybones/remote-control.png"));
            actions.add(new ActionMenu(switchToChan));

            String name = LazyBones.getTranslation("lazybones", "Lazy Bones");
            ImageIcon icon = createImageIcon("lazybones/vdr16.png");
            return new ActionMenu(name, icon, actions.toArray(new ActionMenu[0]));
        }

        private void addActionMenuForUnmarkedProgram(List<ActionMenu> actions, Program program, boolean notAssignedTimersExist) {
            AbstractAction action1 = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    timerManager.createTimer(program, false);
                }
            };
            action1.putValue(Action.NAME, LazyBones.getTranslation("capture", "Capture with VDR"));
            action1.putValue(Action.SMALL_ICON, createImageIcon("lazybones/capture.png"));
            actions.add(new ActionMenu(action1));

            AbstractAction action2 = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    LazyBones.getInstance().synchronize();
                }
            };
            action2.putValue(Action.NAME, LazyBones.getTranslation("resync", "Synchronize with VDR"));
            action2.putValue(Action.SMALL_ICON, createImageIcon("actions", "view-refresh", 16));
            actions.add(new ActionMenu(action2));

            if (notAssignedTimersExist) {
                Action[] timers = new Action[timerManager.getNotAssignedTimers().size()];
                List<LazyBonesTimer> timerList = timerManager.getNotAssignedTimers();
                for (int i = 0; i < timers.length; i++) {
                    final LazyBonesTimer timer = timerList.get(i);
                    timers[i] = new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ProgramManager.getInstance().assignTimerToProgram(program, timer);
                            timerManager.assignProgramToTimer(program, timer);
                            updateTree();
                        }
                    };
                    timers[i].putValue(Action.NAME, timerList.get(i).getDisplayTitle());
                    timers[i].putValue(LazyBones.TIMER_MENU_KEY, timerList.get(i));
                    timers[i].putValue(Action.SMALL_ICON, createImageIcon("lazybones/appointment-new.png"));
                }
                String name = LazyBones.getTranslation("assign", "Assign");
                ImageIcon icon = createImageIcon("lazybones/appointment-new.png");
                actions.add(new ActionMenu(name, icon, timers));
            }
		}

		private void addActionMenuForMarkedProgram(List<ActionMenu> actions, Program program) {
            AbstractAction action1 = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    timerManager.deleteTimer(program);
                }
            };
            action1.putValue(Action.NAME, LazyBones.getTranslation("dont_capture", "Delete timer"));
            action1.putValue(Action.SMALL_ICON, createImageIcon("actions", "edit-delete", 16));
            actions.add(new ActionMenu(action1));

            AbstractAction action2 = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    logger.info("Looking up timer for {}", program);
                    LazyBonesTimer timer = timerManager.getTimer(program);
                    logger.info("Found timer {}", timer);
                    timerManager.editTimer(timer);
                }
            };
            action2.putValue(Action.NAME, LazyBones.getTranslation("edit", "Edit Timer"));
            action2.putValue(Action.SMALL_ICON, createImageIcon("actions", "document-edit", 16));
            actions.add(new ActionMenu(action2));

            AbstractAction action3 = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    LazyBones.getInstance().synchronize();
                }
            };
            action3.putValue(Action.NAME, LazyBones.getTranslation("resync", "Synchronize with VDR"));
            action3.putValue(Action.SMALL_ICON, createImageIcon("actions", "view-refresh", 16));
            actions.add(new ActionMenu(action3));
		}

		JPopupMenu simpleMenu;

        public JPopupMenu createSimpleActionMenu(final LazyBonesTimer timer) {
            if (simpleMenu == null) {
                simpleMenu = new JPopupMenu();
                JMenuItem delItem = new JMenuItem(LazyBones.getTranslation("dont_capture", "Delete timer"), createImageIcon("actions", "edit-delete", 16));
                delItem.addActionListener(e -> timerManager.deleteTimer(timer));

                JMenuItem editItem = new JMenuItem(LazyBones.getTranslation("edit", "Edit Timer"), createImageIcon("actions", "document-edit", 16));
                editItem.addActionListener(e -> timerManager.editTimer(timer));

                simpleMenu.add(editItem);
                simpleMenu.add(delItem);
            }

            return simpleMenu;
        }

    }

    // ################ to receive Programs from other plugins ######################
    private static final String TARGET_WATCH = "watch";
    private static final String TARGET_CAPTURE = "capture";

    @Override
    public boolean canReceiveProgramsWithTarget() {
        return true;
    }

    @Override
    public ProgramReceiveTarget[] getProgramReceiveTargets() {
        return new ProgramReceiveTarget[] { new ProgramReceiveTarget(this, LazyBones.getTranslation("capture", "Capture with VDR"), TARGET_CAPTURE),
                new ProgramReceiveTarget(this, LazyBones.getTranslation("watch", "Watch this channel"), TARGET_WATCH) };
    }

    @Override
    public boolean receivePrograms(int eventType, Program[] programArr, ProgramReceiveTarget receiveTarget) {
        logger.debug("Program received for target [{}]", receiveTarget.getTargetId());
        if (TARGET_CAPTURE.equals(receiveTarget.getTargetId())) {
            // store current property values
            String threshold = props.getProperty("percentageThreshold");
            String showTimerOptionsDialog = props.getProperty("showTimerOptionsDialog");

            String logEpgErrors = props.getProperty("logEPGErrors");
            String logConnectionErrors = props.getProperty("logConnectionErrors");

            // set properties to "automatic mode"
            props.setProperty("percentageThreshold", "0");
            props.setProperty("showTimerOptionsDialog", "false");
            props.setProperty("logEPGErrors", "false");
            props.setProperty("logConnectionErrors", "false");

            // create timers for all programs
            for (int i = 0; i < programArr.length; i++) {
                Program prog = programArr[i];
                timerManager.createTimer(prog, true);
            }

            // restore old properties
            props.setProperty("percentageThreshold", threshold);
            props.setProperty("showTimerOptionsDialog", showTimerOptionsDialog);
            props.setProperty("logEPGErrors", logEpgErrors);
            props.setProperty("logConnectionErrors", logConnectionErrors);
        } else if (TARGET_WATCH.equals(receiveTarget.getTargetId())) {
            Player.play(programArr[0]);
        }
        return true;
    }

    @Override
    public int getMarkPriorityMaxForProgram(Program p) {
        LazyBonesTimer timer = timerManager.getTimer(p);
        if (timer != null && !timer.isActive()) {
		    return Program.PRIORITY_MARK_MIN;
		}
        return Program.getHighlightingPriorityMaximum() - 1;
    }

    @Override
    public String getProgramTableIconText() {
        return LazyBones.getTranslation("vps_activated", "VPS activated");
    }

    @Override
    public Icon[] getProgramTableIcons(Program program) {
        LazyBonesTimer timer = timerManager.getTimer(program);
        if (timer != null && timer.hasState(Timer.VPS)) {
		    Icon[] icons = new ImageIcon[1];
		    icons[0] = LazyBones.getInstance().getIcon("lazybones/vps16.png");
		    return icons;
		}
        return super.getProgramTableIcons(program);
    }
}