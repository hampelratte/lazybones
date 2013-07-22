/*
 * Copyright (c), Henrik Niehaus & Lazy Bones development team
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

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JSpinner;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

public class SpinnerCalendarEditor extends JSpinner.DefaultEditor {

    private SpinnerCalendarModel model;

    public SpinnerCalendarEditor(JSpinner spinner, final SpinnerCalendarModel model) {
        super(spinner);
        this.model = model;
        getTextField().setEditable(true);
        getTextField().setFormatterFactory(new DefaultFormatterFactory(new CalendarFormatter(new SimpleDateFormat("HH:mm"))));
        getTextField().addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int amount = e.getWheelRotation();
                Object value = null;
                for (int i = 0; i < Math.abs(amount); i++) {
                    if (amount > 0) {
                        value = model.getPreviousValue();
                    } else {
                        value = model.getNextValue();
                    }
                }
                model.setValue(value);
            }
        });
    }

    private class CalendarFormatter extends DefaultFormatter {
        private DateFormat dateFormat;

        public CalendarFormatter(DateFormat dateFormat) {
            this.dateFormat = dateFormat;
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (!(value instanceof Calendar)) {
                throw new IllegalArgumentException("Not a Calendar");
            }
            return dateFormat.format(((Calendar) value).getTime());
        }

        @Override
        public Object stringToValue(String string) throws ParseException {
            Calendar value = (Calendar) model.getValue();
            value = (Calendar) value.clone();
            Calendar time = Calendar.getInstance();
            time.setTime(dateFormat.parse(string));
            value.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
            value.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
            return value;
        }
    }
}
