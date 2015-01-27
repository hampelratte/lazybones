package lazybones.gui.components.timeline;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import lazybones.LazyBones;
import lazybones.LazyBonesTimer;
import lazybones.TimerManager;
import lazybones.conflicts.ConflictFinder;
import lazybones.conflicts.ConflictResolver;
import lazybones.conflicts.ConflictUnresolvableException;
import lazybones.conflicts.ConflictingTimersSet;
import lazybones.utils.Utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.ui.ProgramList;
import devplugin.Program;

public class ConflictPanel extends JPanel implements Observer {
    private static final transient Logger logger = LoggerFactory.getLogger(ConflictPanel.class);

    private static final Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);
    private static final int NO_PADDING = 0;
    private static final int NO_WEIGHT = 0;
    private static final int FULL_WEIGHT = 1;

    private static final String MSG_NO_SOLUTION = LazyBones.getTranslation("timer_conflict_no_solution", "No solution found");
    private static final String MSG_SOLUTION = LazyBones.getTranslation("timer_conflict_solution", "Possible solution found");

    private TimerManager timerManager;
    private Calendar calendar;

    private JTextArea description;
    private JScrollPane programs;

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
        add(header, new GridBagConstraints(0, 0, 1, 1, NO_WEIGHT, NO_WEIGHT, WEST, NONE, DEFAULT_INSETS, NO_PADDING, NO_PADDING));

        description = new JTextArea(MSG_NO_SOLUTION);
        description.setRows(2);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setBackground(UIManager.getColor("Panel.background"));
        description.setBorder(null);
        JScrollPane scrollPane = new JScrollPane(description);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(300, 50));
        add(scrollPane, new GridBagConstraints(0, 1, 1, 1, NO_WEIGHT, NO_WEIGHT, WEST, HORIZONTAL, DEFAULT_INSETS, NO_PADDING, NO_PADDING));
    }

    private void update() {
        List<LazyBonesTimer> allTimers = timerManager.getTimers();
        Set<ConflictingTimersSet<LazyBonesTimer>> conflicts = new ConflictFinder().findConflictingTimers(allTimers);

        Set<ConflictingTimersSet<LazyBonesTimer>> conflictsToday = new HashSet<>();
        for (ConflictingTimersSet<LazyBonesTimer> conflict : conflicts) {
            for (LazyBonesTimer timer : conflict) {
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
            for (ConflictingTimersSet<LazyBonesTimer> conflict : conflictsToday) {
                try {
                    List<Program> solution = new ConflictResolver(conflict).solveConflict();
                    description.setText(MSG_SOLUTION);
                    setPrograms(solution);
                    logger.info(solution.toString());
                    break;
                } catch (ConflictUnresolvableException e) {
                    description.setText(MSG_NO_SOLUTION);
                }
            }
        }

        invalidate();
    }

    private void setPrograms(List<Program> solution) {
        if (programs != null) {
            this.remove(programs);
        }

        Program[] a = new Program[solution.size()];
        ProgramList programList = new ProgramList(solution.toArray(a));
        programs = new JScrollPane(programList);
        // programs.setPreferredSize(new Dimension(800, 500));
        add(programs, new GridBagConstraints(0, 2, 1, 1, FULL_WEIGHT, FULL_WEIGHT, WEST, BOTH, DEFAULT_INSETS, NO_PADDING, NO_PADDING));
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

}
