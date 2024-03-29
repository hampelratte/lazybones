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
package lazybones.gui.components.timeroptions;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import lazybones.ChannelManager;
import lazybones.LazyBones;
import lazybones.LazyBonesTimer;
import lazybones.conflicts.Conflict;
import lazybones.gui.components.AbstractDetailsPanel;

import org.hampelratte.svdrp.responses.highlevel.Channel;

import devplugin.Program;

public class TimerOptionsView extends AbstractDetailsPanel {
    /**
     * The timer to show in the view
     */
    private LazyBonesTimer timer = null;

    /**
     * The TVB program, which corresponds to the timer
     */
    private transient Program prog;

    public TimerOptionsView() {
        super("timer_details.html");
    }

    @Override
    public String replaceTags(String template) {
        template = template.replace("{title}", timer.getDisplayTitle());

        String channel = "";
        if (prog != null) {
            channel = prog.getChannel().getName();
        } else {
            Channel chan = ChannelManager.getInstance().getChannelByNumber(timer.getChannelNumber());
            channel = chan.getName();
        }
        template = template.replace("{channel}", channel);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        template = template.replace("{startDate}", createDateString());
        template = template.replace("{startTime}", timeFormat.format(timer.getStartTime().getTime()));
        template = template.replace("{endTime}", timeFormat.format(timer.getEndTime().getTime()));
        template = template.replace("{description}", timer.getDescription().replace("\n", "<br>"));
        template = template.replace("{directory}", timer.getPath());
        template = template.replace("{lifetime}", Integer.toString(timer.getLifetime()));
        template = template.replace("{priority}", Integer.toString(timer.getPriority()));
        template = template.replace("{conflicts}", createListOfConflictingTimers(timer.getConflicts()));
        return template;
    }

    private String createListOfConflictingTimers(List<Conflict> conflicts) {
        if (conflicts.isEmpty()) {
            return "";
        } else {
            DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
            StringBuilder sb = new StringBuilder();
            for (Conflict conflict : conflicts) {
                Date startTime = conflict.getPeriod().getStartTime().getTime();
                Date endTime = conflict.getPeriod().getEndTime().getTime();
                sb.append(dateFormat.format(startTime));
                sb.append(" &ndash; ");
                sb.append(dateFormat.format(endTime));
                sb.append("<ul>");
                for (LazyBonesTimer involvedTimer : conflict.getInvolvedTimers()) {
                    if (!involvedTimer.equals(this.timer)) {
                        sb.append("<li>").append(involvedTimer.getTitle()).append("</li>");
                    }
                }
                sb.append("</ul><br/>");
            }
            return sb.toString();
        }
    }

    private String createDateString() {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        StringBuilder dateString = new StringBuilder();
        if (timer.isRepeating()) {
            createRepeatingDateString(dateString, dateFormat);
        } else {
            dateString.append(dateFormat.format(timer.getStartTime().getTime()));
        }
        return dateString.toString();
    }

    private void createRepeatingDateString(StringBuilder dateString, DateFormat dateFormat) { // NOSONAR
    	if (timer.isSetForWeekdays(0, 1, 2, 3, 4, 5, 6)) {
            dateString.append(LazyBones.getTranslation("daily", "daily")).append(' ');
        } else if (timer.isSetForWeekdays(0, 1, 2, 3, 4)) {
            dateString.append(LazyBones.getTranslation("onWorkdays", "on workdays")).append(' ');
        } else if (timer.isSetForWeekdays(5, 6)) {
            dateString.append(LazyBones.getTranslation("atTheWeekend", "at the weekend")).append(' ');
        } else {
            dateString.append(timer.getRepeatingDays()[0] ? LazyBones.getTranslation("onMondays", "on Mondays") : "").append(' ');
            dateString.append(timer.getRepeatingDays()[1] ? LazyBones.getTranslation("onTuesdays", "on Tuesdays") : "").append(' ');
            dateString.append(timer.getRepeatingDays()[2] ? LazyBones.getTranslation("onWednesdays", "on Wednesdays") : "").append(' ');
            dateString.append(timer.getRepeatingDays()[3] ? LazyBones.getTranslation("onThursdays", "on Thursdays") : "").append(' ');
            dateString.append(timer.getRepeatingDays()[4] ? LazyBones.getTranslation("onFridays", "on Fridays") : "").append(' ');
            dateString.append(timer.getRepeatingDays()[5] ? LazyBones.getTranslation("onSaturdays", "on Saturdays") : "").append(' ');
            dateString.append(timer.getRepeatingDays()[6] ? LazyBones.getTranslation("onSundays", "on Sundays") : "").append(' ');
        }

        if (timer.hasFirstTime()) {
            dateString.append("ab dem ");
            dateString.append(dateFormat.format(timer.getFirstTime().getTime()));
        }
	}

	public void setTimer(LazyBonesTimer timer) {
        this.timer = timer;
        updateHtmlPane();
    }

    public void setProgram(Program prog) {
        this.prog = prog;
        updateHtmlPane();
    }
}
