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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lazybones.ChannelManager;
import lazybones.LazyBones;
import lazybones.LazyBonesTimer;
import lazybones.utils.StartStopEvent;
import lazybones.utils.Utilities;

import org.hampelratte.svdrp.responses.highlevel.BroadcastChannel;
import org.hampelratte.svdrp.responses.highlevel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConflictFinder {

    private static Logger logger = LoggerFactory.getLogger(ConflictFinder.class);

    private final Set<Conflict> conflicts = new HashSet<>();
    private final HashMap<Integer, Integer> transponderUse = new HashMap<>();
    private final HashSet<LazyBonesTimer> runningEvents = new HashSet<>();

    private void reset() {
        conflicts.clear();
        transponderUse.clear();
        runningEvents.clear();

    }

    public Set<Conflict> findConflictingTimers(List<LazyBonesTimer> timers) {
        logger.debug("Looking for conflicts");

        // clear old status data
        reset();

        int numberOfCards = Integer.parseInt(LazyBones.getProperties().getProperty("numberOfCards"));
        logger.debug("Number of cards: {}", numberOfCards);

        // the following is a scan line algorithm, which runs over the start and stop events of
        // the timers and increases / decreases the transponder usage accordingly
        List<StartStopEvent> startStopEvents = Utilities.createStartStopEventList(timers);
        boolean conflictFound = false;
        Conflict conflict = new Conflict();
        for (Iterator<StartStopEvent> iter = startStopEvents.iterator(); iter.hasNext();) {
            StartStopEvent event = iter.next();
            LazyBonesTimer timer = event.getTimer();
            if (event.isStartEvent()) {
                increaseTransponderUse(timer);
                runningEvents.add(timer);
                if (transponderUse.size() > numberOfCards) {
                    conflictFound = true;
                    conflict.getInvolvedTimers().addAll(runningEvents);
                    if (conflict.getPeriod().getStartTime() == null) {
                        conflict.getPeriod().setStartTime(event.getEventTime());
                    }
                }
            } else {
                decreaseTransponderUse(timer);
                if (conflictFound && transponderUse.size() <= numberOfCards) {
                    conflictFound = false;
                    conflict.getPeriod().setEndTime(event.getEventTime());
                    conflicts.add(conflict);
                    conflict = new Conflict();
                }
                runningEvents.remove(timer);
            }
        }
        return conflicts;
    }

    public int getConflictCount() {
        return conflicts.size();
    }

    public Set<Conflict> getConflicts() {
        return conflicts;
    }

    private void increaseTransponderUse(LazyBonesTimer timer) {
        Channel tempChan = ChannelManager.getInstance().getChannelByNumber(timer.getChannelNumber());
        if (tempChan instanceof BroadcastChannel) {
            BroadcastChannel chan = (BroadcastChannel) tempChan;
            if (transponderUse.containsKey(chan.getFrequency())) {
                int count = transponderUse.get(chan.getFrequency());
                count++;
                transponderUse.put(chan.getFrequency(), count);
            } else {
                transponderUse.put(chan.getFrequency(), 1);
            }
        }
    }

    private void decreaseTransponderUse(LazyBonesTimer timer) {
        Channel tempChan = ChannelManager.getInstance().getChannelByNumber(timer.getChannelNumber());
        if (tempChan instanceof BroadcastChannel) {
            BroadcastChannel chan = (BroadcastChannel) tempChan;
            if (transponderUse.containsKey(chan.getFrequency())) {
                int count = transponderUse.get(chan.getFrequency());
                if (count == 1) {
                    transponderUse.remove(chan.getFrequency());
                } else {
                    count--;
                    transponderUse.put(chan.getFrequency(), count);
                }
            }
        }
    }
}