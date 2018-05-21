package lazybones.gui.components.timeline;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.SOUTHWEST;
import static java.awt.GridBagConstraints.WEST;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import devplugin.Program;
import lazybones.LazyBones;
import lazybones.LazyBonesTimer;
import lazybones.TimerManager;
import lazybones.conflicts.Conflict;
import lazybones.conflicts.ConflictFinder;
import lazybones.conflicts.ConflictResolver;
import lazybones.conflicts.ConflictUnresolvableException;
import lazybones.utils.Utilities;
import util.programmouseevent.ProgramMouseEventHandler;
import util.settings.PluginPictureSettings;
import util.ui.ProgramList;

public class ConflictPanel extends JPanel implements Observer, ActionListener {
    private static final Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);
    private static final int NO_PADDING = 0;
    private static final int NO_WEIGHT = 0;
    private static final int FULL_WEIGHT = 1;

    private static final String MSG_NO_SOLUTION = LazyBones.getTranslation("timer_conflict_no_solution", "No solution found");
    private static final String MSG_SOLUTION = LazyBones.getTranslation("timer_conflict_solution", "Possible solution found");

    private TimerManager timerManager;
    private Calendar calendar;
    private Set<Conflict> conflictsToday = new HashSet<>();
    private Conflict displayedConflict;

    private DefaultListModel<Object> programListModel = new DefaultListModel<>();
    private ProgramList programList;
    private JScrollPane programListScrollPane;

    private JTextArea description;
    private JLabel dummy;

    private JButton reprogramTimersButton = new JButton(LazyBones.getTranslation("timer_conflict_change_timers", "Change timers"));

    public ConflictPanel(TimerManager timerManager) {
        this.timerManager = timerManager;
        timerManager.addObserver(this);

        initGUI();
    }

    private void initGUI() {
        setLayout(new GridBagLayout());

        JLabel header = new JLabel(LazyBones.getTranslation("timer_conflict_resolution", "Conflict resolution"));
        header.setFont(new Font("SansSerif", Font.PLAIN, 18));
        header.setPreferredSize(new Dimension(280, 30));
        add(header, new GridBagConstraints(0, 0, 1, 1, FULL_WEIGHT, NO_WEIGHT, WEST, HORIZONTAL, DEFAULT_INSETS, NO_PADDING, NO_PADDING));

        description = new JTextArea(MSG_NO_SOLUTION);
        description.setEditable(false);
        description.setRows(2);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setBackground(UIManager.getColor("Panel.background"));
        description.setBorder(null);
        JScrollPane scrollPane = new JScrollPane(description);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(0, 50));
        add(scrollPane, new GridBagConstraints(0, 1, 1, 1, FULL_WEIGHT, FULL_WEIGHT, WEST, HORIZONTAL, DEFAULT_INSETS, NO_PADDING, NO_PADDING));

        programList = new ProgramList(programListModel, new PluginPictureSettings(PluginPictureSettings.NO_PICTURE_TYPE));
        programList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        programList.addMouseListener(new ProgramListMouseAdapter());
        programListScrollPane = new JScrollPane(programList);
        programListScrollPane.setMinimumSize(new Dimension(0, 100));
        programListScrollPane.setPreferredSize(new Dimension(0, 250));
        add(programListScrollPane, new GridBagConstraints(0, 2, 1, 1, FULL_WEIGHT, NO_WEIGHT, SOUTHWEST, BOTH, DEFAULT_INSETS, NO_PADDING, NO_PADDING));

        reprogramTimersButton.addActionListener(this);
        add(reprogramTimersButton, new GridBagConstraints(0, 3, 1, 1, NO_WEIGHT, NO_WEIGHT, WEST, NONE, DEFAULT_INSETS, NO_PADDING, NO_PADDING));

        dummy = new JLabel();
        add(dummy, new GridBagConstraints(0, 4, 1, 1, FULL_WEIGHT, FULL_WEIGHT, WEST, BOTH, DEFAULT_INSETS, NO_PADDING, NO_PADDING));
    }

    private void update() {
        List<LazyBonesTimer> allTimers = timerManager.getTimers();
        Set<Conflict> conflicts = new ConflictFinder().findConflictingTimers(allTimers);

        conflictsToday.clear();
        for (Conflict conflict : conflicts) {
            for (LazyBonesTimer timer : conflict.getInvolvedTimers()) {
                if (Utilities.timerRunsOnDate(timer, calendar)) {
                    conflictsToday.add(conflict);
                }
            }
        }

        if (conflictsToday.isEmpty()) {
            setVisible(false);
        } else {
            setVisible(true);
            description.setText(MSG_NO_SOLUTION);
            for (Conflict conflict : conflictsToday) {
                try {
                    List<Program> solution = new ConflictResolver(conflict, timerManager.getTimers()).solveConflict();
                    description.setText(MSG_SOLUTION);
                    setPrograms(solution);
                    dummy.setVisible(false);
                    programListScrollPane.setVisible(true);
                    reprogramTimersButton.setVisible(true);
                    displayedConflict = conflict;
                    break;
                } catch (ConflictUnresolvableException e) {
                    description.setText(MSG_NO_SOLUTION);
                    programListScrollPane.setVisible(false);
                    reprogramTimersButton.setVisible(false);
                    dummy.setVisible(true);
                }
            }
        }
    }

    private void setPrograms(List<Program> solution) {
        programListModel.removeAllElements();
        for (Program program : solution) {
            programListModel.addElement(program);
        }
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        this.calendar.set(Calendar.HOUR_OF_DAY, 0);
        this.calendar.set(Calendar.MINUTE, 0);
        this.calendar.set(Calendar.SECOND, 0);
        this.calendar.set(Calendar.MILLISECOND, 0);

        update();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof TimerManager) {
            update();
        }
    }

    class ProgramListMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2) {
                int index = programList.locationToIndex(e.getPoint());
                programList.setSelectedIndex(index);
                Program prog = (Program) programList.getSelectedValue();
                ProgramMouseEventHandler.handleProgramClick(prog, LazyBones.getInstance(), false, e);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                int index = programList.locationToIndex(e.getPoint());
                programList.setSelectedIndex(index);
                Program prog = (Program) programList.getSelectedValue();
                JPopupMenu popup = LazyBones.getPluginManager().createPluginContextMenu(prog, null);
                popup.setLocation(e.getPoint());
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == reprogramTimersButton) {
            int result = JOptionPane.showConfirmDialog(LazyBones.getInstance().getMainDialog(), LazyBones.getTranslation("timer_conflict_change_confirm", ""),
                    "", JOptionPane.YES_NO_OPTION);

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            // save solution for the timer creation later
            List<Program> solution = new ArrayList<>();
            Enumeration<Object> en = programListModel.elements();
            while (en.hasMoreElements()) {
                Program prog = (Program) en.nextElement();
                solution.add(prog);
            }

            // delete the old timers:
            // sort by descending timer id, so that we can delete them from highest ID to lowest
            // otherwise we might get the error "Timer x not defined"
            List<LazyBonesTimer> timers = new ArrayList<>(displayedConflict.getInvolvedTimers());
            Collections.sort(timers, new Comparator<LazyBonesTimer>() {
                @Override
                public int compare(LazyBonesTimer o1, LazyBonesTimer o2) {
                    if (o1.getID() > o2.getID()) {
                        return -1;
                    } else if (o1.getID() < o2.getID()) {
                        return 1;
                    } else {
                        throw new RuntimeException("2 timers with same ID. This should'nt happen");
                    }
                }
            });
            for (LazyBonesTimer timer : timers) {
                timerManager.deleteTimer(timer);
            }

            // create timers for programs from the conflict-free solution
            for (Program program : solution) {
                timerManager.createTimer(program, true);
            }
        }
    }
}
