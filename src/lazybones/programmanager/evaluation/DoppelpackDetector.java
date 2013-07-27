package lazybones.programmanager.evaluation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import lazybones.LazyBones;
import lazybones.LazyBonesTimer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devplugin.Program;

public class DoppelpackDetector {
    private static transient Logger logger = LoggerFactory.getLogger(DoppelpackDetector.class);

    private List<Program> searchArea;

    public DoppelpackDetector(List<Program> searchArea) {
        this.searchArea = searchArea;
    }

    public boolean markDoppelpackTimer(LazyBonesTimer timer) {
        Iterator<Program> it = searchArea.iterator();
        if (it == null) {
            if (!timer.isRepeating()) {
                timer.setReason(LazyBonesTimer.NO_EPG);
            }
            return false;
        }

        List<Program> doppelpackCandidates = collectDoppelpackCandidates(timer, it);

        boolean doppelpackFound = false;
        if (doppelpackCandidates.size() > 1) {
            timer.setReason(LazyBonesTimer.NOT_FOUND);
            String doppelpackTitle = null;
            for (int i = 0; i < doppelpackCandidates.size(); i++) {
                Program prog = doppelpackCandidates.get(i);
                String title = prog.getTitle();
                while (i < doppelpackCandidates.size() - 1) {
                    Program next = doppelpackCandidates.get(i + 1);
                    if (title.equals(next.getTitle())) {
                        logger.debug("Doppelpack found: {}", title);
                        doppelpackFound = true;
                        doppelpackTitle = title;
                        timer.setReason(LazyBonesTimer.NO_REASON);
                    } else {
                        // There is a program with a different name. If this is a real doppelpack, this program
                        // is most probably a short program, like a short news or weather program. If this program
                        // is a longer one, this is most probably not a doppelpack.
                        // For example a movie surrounded by short news program, which triggered the doppelpack detection
                        if (next.getLength() > 15) {
                            return false;
                        }
                    }
                    i++;
                }
            }

            // mark all doppelpack programs
            if (doppelpackTitle != null) {
                for (Program prog : doppelpackCandidates) {
                    if (prog.getTitle().equals(doppelpackTitle)) {
                        prog.mark(LazyBones.getInstance());
                        timer.addTvBrowserProgID(prog.getUniqueID());
                    }
                }
            }
        }

        return doppelpackFound;
    }

    private List<Program> collectDoppelpackCandidates(LazyBonesTimer timer, Iterator<Program> it) {
        // contains programs, which start and stop between the start and the stop time
        // of the timer and could be part of a Doppelpack
        List<Program> doppelPack = new ArrayList<Program>();

        // iterate over all programs and
        // compare start and end time to collect doppelpack candidates
        while (it.hasNext()) {
            Program prog = it.next();

            // get prog start and end
            Calendar progStartCal = createStarttimeCalendar(prog);
            Calendar progEndCal = (Calendar) progStartCal.clone();
            progEndCal.add(Calendar.MINUTE, prog.getLength());

            // collect doppelpack candidates
            // use timer with buffers
            if (progStartCal.after(timer.getStartTime()) && progEndCal.before(timer.getEndTime())) {
                doppelPack.add(prog);
            }
        }
        return doppelPack;
    }

    private Calendar createStarttimeCalendar(Program prog) {
        Calendar progStartCal = prog.getDate().getCalendar();
        progStartCal.set(Calendar.HOUR_OF_DAY, prog.getHours());
        progStartCal.set(Calendar.MINUTE, prog.getMinutes());
        progStartCal.set(Calendar.SECOND, 0);
        return progStartCal;
    }
}
