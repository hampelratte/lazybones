/*
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
package lazybones.gui.settings;

import static devplugin.Plugin.getPluginManager;
import static lazybones.gui.settings.DescriptionSelectorItem.LONGEST;
import static lazybones.gui.settings.DescriptionSelectorItem.TIMER;
import static lazybones.gui.settings.DescriptionSelectorItem.TVB_DESC;
import static lazybones.gui.settings.DescriptionSelectorItem.TVB_PREFIX;
import static lazybones.gui.settings.DescriptionSelectorItem.VDR;

import javax.swing.DefaultComboBoxModel;

import lazybones.LazyBones;
import util.program.AbstractPluginProgramFormating;

public class DescriptionComboBoxModel extends DefaultComboBoxModel<DescriptionSelectorItem> {

    public DescriptionComboBoxModel(boolean addLongestElement, boolean addTimerElement) {
        addElement(new DescriptionSelectorItem(VDR, "VDR"));
        addElement(new DescriptionSelectorItem(TVB_DESC, LazyBones.getTranslation("timer_desc_tvb", "TV-Browser description only")));
        AbstractPluginProgramFormating[] formattings = getPluginManager().getAvailableGlobalPuginProgramFormatings();
        for (AbstractPluginProgramFormating format : formattings) {
            String name = LazyBones.getTranslation("timer_desc_formatted", "TV-Browser - formatted ({0})", format.getName());
            addElement(new DescriptionSelectorItem(TVB_PREFIX + format.getId(), name));
        }

        if (addLongestElement) {
            addElement(new DescriptionSelectorItem(LONGEST, LazyBones.getTranslation("timer_desc_longest", "longest description")));
        }

        if (addTimerElement) {
            addElement(new DescriptionSelectorItem(TIMER, "Timer"));
        }
    }

    public void setSelected(String id) {
        for (int i = 0; i < getSize(); i++) {
            DescriptionSelectorItem desc = getElementAt(i);
            if (desc.getId().equals(id)) {
                setSelectedItem(desc);
            }
        }
    }
}
