package lazybones.gui.recordings;

import java.awt.Component;
import java.awt.Graphics;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.hampelratte.svdrp.responses.highlevel.TreeNode;

import lazybones.LazyBones;

public class RecordingTreeRenderer extends JLabel implements TreeCellRenderer {
                  
    private final transient Icon iconNew;
    private final transient Icon iconCut;
    private final transient Icon iconError;
    private final transient Icon iconClosed;
    private final transient Icon iconOpened;

    public RecordingTreeRenderer() {
        iconNew = LazyBones.getInstance().getIcon("lazybones/new.png");
        iconCut = LazyBones.getInstance().getIcon("lazybones/edit-cut.png");
        iconError = LazyBones.getInstance().getIcon("lazybones/image-missing.png");
        iconClosed = UIManager.getIcon("Tree.closedIcon");
        iconOpened = UIManager.getIcon("Tree.openIcon");

        setOpaque(true);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        setIcon(value, expanded);
        setColors(selected);
        String title = covertToString(value);
        setText(title);
        setEnabled(tree.isEnabled());
        return this;
    }

    private void setColors(boolean selected) {
        if (selected) {
            setBackground(UIManager.getColor("Tree.selectionBackground"));
            setForeground(UIManager.getColor("Tree.selectionForeground"));
        } else {
            setForeground(UIManager.getColor("Tree.textForeground"));
            setBackground(UIManager.getColor("Tree.textBackground"));
        }
    }

    private String covertToString(Object value) {
        String title = "";
        if (value != null) {
            title = value.toString();
            if (value instanceof TreeNode) {
                title = ((TreeNode) value).getDisplayTitle();
                if (value instanceof Recording) {
                    Recording recording = (Recording) value;
                    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, Locale.getDefault());
                    title = "<html>" + df.format(recording.getStartTime().getTime()) + " - <b>" + title + "</b>";
                    if (recording.getShortText() != null && recording.getShortText().trim().length() > 0) {
                        title += " - " + recording.getShortText();
                    }
                    if(recording.getDuration() != -1) {
                        title += " (" + recording.getDuration() + "min)";
                    }
                    title += "</html>";
                }
            }
        }
        return title;
    }

    private void setIcon(Object value, boolean expanded) {
        if (value instanceof Recording) {
            Recording recording = (Recording) value;
            setHorizontalTextPosition(LEADING);
            setIconTextGap(10);
            
            var combined = new ArrayList<Icon>();
            if (recording.isNew()) {
            	combined.add(iconNew);
            }
            if (recording.isCut()) {
            	combined.add(iconCut);
            }
            if (recording.hasError()) {
            	combined.add(iconError);
            }
            
            if (combined.isEmpty()) {
            	setIcon(null);
            	setDisabledIcon(null);
            } else {
            	var iconAll = new CombinedIcon(combined, combined.size());
            	setIcon(iconAll);
            	setDisabledIcon(iconAll);
            }
        } else {
            setHorizontalTextPosition(TRAILING);
            setIconTextGap(5);
            if (expanded) {
                setIcon(iconOpened);
                setDisabledIcon(iconOpened);
            } else {
                setIcon(iconClosed);
                setDisabledIcon(iconClosed);
            }
        }
    }

    private class CombinedIcon implements Icon {
        private List<Icon> icons = new ArrayList<>();
        private int hgap = 2;

        public CombinedIcon(List<Icon> icons, int hgap) {
            this.icons = icons;
            this.hgap = hgap;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            for (int i = 0; i < icons.size(); i++) {
                Icon icon = icons.get(i);
                icon.paintIcon(c, g, x + (i * 16) + (i * hgap), y);
            }
        }

        @Override
        public int getIconWidth() {
            return icons.size() * 16 + (icons.size() - 1) * hgap;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }
    }
}
