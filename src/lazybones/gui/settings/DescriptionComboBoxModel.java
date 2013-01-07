package lazybones.gui.settings;

import static lazybones.gui.settings.DescriptionSelectorItem.LONGEST;
import static lazybones.gui.settings.DescriptionSelectorItem.TIMER;
import static lazybones.gui.settings.DescriptionSelectorItem.TVB_DESC;
import static lazybones.gui.settings.DescriptionSelectorItem.TVB_PREFIX;
import static lazybones.gui.settings.DescriptionSelectorItem.VDR;

import javax.swing.DefaultComboBoxModel;

import lazybones.LazyBones;
import util.program.AbstractPluginProgramFormating;

public class DescriptionComboBoxModel extends DefaultComboBoxModel {

    public DescriptionComboBoxModel(boolean addLongestElement, boolean addTimerElement) {
        addElement(new DescriptionSelectorItem(VDR, "VDR"));
        addElement(new DescriptionSelectorItem(TVB_DESC, LazyBones.getTranslation("timer_desc_tvb", "TV-Browser description only")));
        AbstractPluginProgramFormating[] formattings = LazyBones.getPluginManager().getAvailableGlobalPuginProgramFormatings();
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
            DescriptionSelectorItem desc = (DescriptionSelectorItem) getElementAt(i);
            if (desc.getId().equals(id)) {
                setSelectedItem(desc);
            }
        }
    }
}
