/* $Id: TitleCriterion.java,v 1.2 2008-10-21 19:42:57 hampelratte Exp $
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
package lazybones.programmanager.evaluation;

import lazybones.Timer;
import lazybones.utils.Utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devplugin.Program;

public class TitleCriterion extends AbstractCriterion {

    private static transient Logger logger = LoggerFactory.getLogger(TitleCriterion.class);
    
    public int evaluate(Program prog, Timer timer) {
        // calculate the precentage of common words
        int percentage = 0;
        int percentagePath = Utilities.percentageOfEquality(timer.getPath(), prog.getTitle());
        int percentageTitle = Utilities.percentageOfEquality(timer.getTitle(), prog.getTitle());
        int percentageBoth = Utilities.percentageOfEquality(timer.getPath() + timer.getTitle(), prog.getTitle());
        percentage = Math.max(percentagePath, percentageTitle);
        percentage = Math.max(percentage, percentageBoth);

        logger.trace("TitleCriterion for timer {} and prog {}: {}", new Object[] {timer.getTitle(), prog.getTitle(), percentage});
        return percentage;
    }

}