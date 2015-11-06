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
package lazybones.conflicts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import lazybones.ChannelManager;
import lazybones.LazyBones;
import lazybones.LazyBonesTimer;
import lazybones.TimerManager;
import lazybones.gui.TimelinePanel;
import lazybones.utils.Utilities;

import org.hampelratte.svdrp.responses.highlevel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.exc.TvBrowserException;
import devplugin.Date;
import devplugin.PluginManager;
import devplugin.Program;
import devplugin.ProgramFieldType;
import devplugin.ProgramSearcher;

/**
 * The ConflictResolver tries to resolve a conflict by searching for program repetitions.
 * Found repetitions are combined with a backtracking algorithm to find a combination, where no pair of two programs overlap.
 */
public class ConflictResolver {
    private static transient Logger logger = LoggerFactory.getLogger(ConflictResolver.class);

    private static final int DAYS_TO_LOOK_FOR_REPS = 6;

    private ConflictingTimersSet<LazyBonesTimer> conflict;

    /**
     * For each program of a conflict set this list contains an array with all the repetitions of the program.
     */
    private List<Program[]> repetitions;
    private List<LazyBonesTimer> allTimers;

    public ConflictResolver(ConflictingTimersSet<LazyBonesTimer> conflict, List<LazyBonesTimer> allTimers) {
        this.conflict = conflict;
        this.allTimers = allTimers;
    }

    public void handleConflicts() {
        // show timeline
        LazyBones.getInstance().getMainDialog().setVisible(true);
        LazyBones.getInstance().getMainDialog().showTimeline();

        // set timeline date to the date of the conflict
        TimelinePanel tp = LazyBones.getInstance().getMainDialog().getTimelinePanel();
        Calendar startTime = (Calendar) conflict.getConflictStartTime().clone();
        int timelineStartHour = Integer.parseInt(LazyBones.getProperties().getProperty("timelineStartHour"));
        if (startTime.get(Calendar.HOUR_OF_DAY) < timelineStartHour) {
            startTime.add(Calendar.DAY_OF_MONTH, -1);
        }
        tp.setCalendar(startTime);
        LazyBones.getInstance().getMainDialog().getTimelinePanel().repaint();
    }

    public List<Program> solveConflict() {
        List<Program> solution = new ArrayList<>();
        repetitions = findRepetitions(conflict);
        boolean solutionFound = findConflictlessSolution(solution, 0);
        if (solutionFound) {
        	// TODO make sure the found solution does not collide with other existing timers
            StringBuilder sb = new StringBuilder("Found combination without conflicts:");
            for (Program program : solution) {
                sb.append("\n\t").append(program.toString());
            }
            logger.info(sb.toString());
        } else {
            throw new ConflictUnresolvableException("Conflict could not be solved");
        }
        return solution;
    }

    private boolean findConflictlessSolution(List<Program> solution, int programToAdd) {
        logger.trace("Current program {}", programToAdd);
        Program[] programRepetitions = repetitions.get(programToAdd);
        for (int i = 0; i < programRepetitions.length; i++) {
            logger.trace("Current rep {}", i);
            Program candidate = programRepetitions[i];
            if (!conflicts(candidate, solution) && !inThePast(candidate) && !conflictsWithOtherTimers(candidate)) {
                solution.add(candidate);
                logger.trace("Adding {} to solution", candidate);
                programToAdd++;
                if(programToAdd < repetitions.size()) {
                    // we found a solution for all programs up to the current one, let's try
                    // to add another one
                    boolean solutionFound = findConflictlessSolution(solution, programToAdd);
                    if(solutionFound) {
                        return true;
                    } else {
                        logger.trace("Backtracking");
                        // backtracking
                        // none of the current repetitions is conflict free with the current solution
                        // now we have to go one step back and try again with another combination

                        // remove the current repetition of this program from the solution and try the next one
                        programToAdd--;
                        solution.remove(solution.size()-1);
                        continue;
                    }
                } else {
                    // we found a solution for the last program to add, so we can
                    // now stop the recursion and return true
                    return true;
                }
            } else {
                if (inThePast(candidate)) {
                    logger.trace("Program is in the past {}", candidate);
                } else {
                    logger.trace("Conflict with current solution {}", candidate);
                }
            }
        }
        return false;
    }

    private boolean conflictsWithOtherTimers(Program candidate) {
    	ConflictFinder finder = new ConflictFinder();

    	List<LazyBonesTimer> timers = new ArrayList<LazyBonesTimer>();
    	timers.add(createTimerFromProgram(candidate));

        List<LazyBonesTimer> otherTimers = new ArrayList<LazyBonesTimer>(allTimers);
        otherTimers.removeAll(conflict);
        timers.addAll(otherTimers);

        Set<ConflictingTimersSet<LazyBonesTimer>> conflicts = finder.findConflictingTimers(timers, false);
        return !conflicts.isEmpty();
	}

	private LazyBonesTimer createTimerFromProgram(Program candidate) {
		Channel channel = ChannelManager.getChannelMapping().get(candidate.getChannel().getId());
		LazyBonesTimer timer = new LazyBonesTimer();
		timer.setChannelNumber(channel.getChannelNumber());
		timer.setTitle(candidate.getTitle());

		// start and end time with buffers
		timer.setStartTime(Utilities.getStartTime(candidate));
		timer.setEndTime(Utilities.getEndTime(candidate));
		TimerManager.setTimerBuffers(timer);
		return timer;
	}

	private boolean inThePast(Program candidate) {
        Calendar now = Calendar.getInstance();
        Calendar programStart = Utilities.getStartTime(candidate);
        return programStart.before(now);
    }

    private boolean conflicts(Program repetition, List<Program> solution) {
        int bufferBefore = Integer.parseInt(LazyBones.getProperties().getProperty("timer.before"));
        int bufferAfter = Integer.parseInt(LazyBones.getProperties().getProperty("timer.after"));

        for (Program program : solution) {
            Calendar candidateStart = Utilities.getStartTime(repetition);
            candidateStart.add(Calendar.MINUTE, -bufferBefore);
            Calendar candidateEnd = Utilities.getEndTime(repetition);
            candidateEnd.add(Calendar.MINUTE, bufferAfter);

            Calendar programStart = Utilities.getStartTime(program);
            programStart.add(Calendar.MINUTE, -bufferBefore);
            Calendar programEnd = Utilities.getEndTime(program);
            programEnd.add(Calendar.MINUTE, bufferAfter);

            //@formatter:off
            if (    candidateStart.after(programStart) && candidateStart.before(programEnd)
                 || candidateEnd.after(programStart) && candidateEnd.before(programEnd)
                 || programStart.after(candidateStart) && programStart.before(candidateEnd)
                 || programEnd.after(candidateStart) && programEnd.before(candidateEnd)
                 || candidateStart.equals(programStart)
                 || candidateEnd.equals(programEnd))
            {
                return true;
            }
            //@formatter:on
        }

        return false;
    }

    private List<Program[]> findRepetitions(ConflictingTimersSet<LazyBonesTimer> conflictingTimersSet) {
        List<Program[]> repetitions = new ArrayList<>();
        try {
            for (LazyBonesTimer timer : conflictingTimersSet) {
                String title = getProgramTitle(timer);
                ProgramSearcher searcher = LazyBones.getPluginManager().createProgramSearcher(PluginManager.SEARCHER_TYPE_EXACTLY, title, false);
                devplugin.Channel[] allChannels = LazyBones.getPluginManager().getSubscribedChannels();
                ProgramFieldType[] searchFields = new ProgramFieldType[] { ProgramFieldType.TITLE_TYPE };
                Program[] results = searcher.search(searchFields, new Date(), DAYS_TO_LOOK_FOR_REPS, allChannels, true);
                for (Program program : results) {
                    logger.trace("Found repetition {}", program);
                }
                repetitions.add(results);
            }
        } catch (TvBrowserException e) {
            logger.error("Search for repetitions failed. Conflict resolution stopped!", e);
        }
        return repetitions;
    }

    private String getProgramTitle(LazyBonesTimer timer) {
        String title = timer.getTitle();
        if (!timer.getTvBrowserProgIDs().isEmpty()) {
            String programId = timer.getTvBrowserProgIDs().get(0);
            Program program = LazyBones.getPluginManager().getProgram(programId);
            if (program != null) {
                title = program.getTitle();
            }
        }
        return title;
    }
}
