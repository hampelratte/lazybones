/* $Id: DurationCriterion.java,v 1.3 2011-04-20 12:09:14 hampelratte Exp $
 * 
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
package lazybones.programmanager.evaluation;

import java.util.concurrent.TimeUnit;

import lazybones.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devplugin.Program;

/**
 * Compares the duration of a {@link Program} and {@link Timer} and returns the percentage of equality
 * 
 * @author <a href="hampelratte@users.berlios.de">hampelratte@users.berlios.de</a>
 */
public class DurationCriterion extends AbstractCriterion {

    private static transient Logger logger = LoggerFactory.getLogger(DurationCriterion.class);

    public int evaluate(Program prog, Timer timer) {
        // program duration in minutes
        int durationProg = prog.getLength();

        // timer duration in minutes
        Timer bufferless = timer.getTimerWithoutBuffers();
        long start = bufferless.getStartTime().getTimeInMillis();
        long end = bufferless.getEndTime().getTimeInMillis();
        long durationInMillis = end - start;
        int durationTimer = (int) TimeUnit.MILLISECONDS.toMinutes(durationInMillis);

        // return 100% - the difference in minutes
        int percentage = 100 - Math.abs(durationTimer - durationProg);
        logger.trace("DurationCriterion for timer {} and prog {}: {}", new Object[] { timer.getTitle(), prog.getTitle(), percentage });
        return percentage;
    }

}
