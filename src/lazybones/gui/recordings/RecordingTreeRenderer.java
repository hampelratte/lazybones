package lazybones.gui.recordings;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import lazybones.LazyBones;

import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.hampelratte.svdrp.responses.highlevel.TreeNode;

public class RecordingTreeRenderer extends JLabel implements TreeCellRenderer {

    private final Icon iconNew;
    private final Icon iconCut;
    private final Icon iconBoth;
    private final Icon iconClosed;
    private final Icon iconOpened;

    public RecordingTreeRenderer() {
        iconNew = LazyBones.getInstance().getIcon("lazybones/new.png");
        iconCut = LazyBones.getInstance().getIcon("lazybones/edit-cut.png");
        List<Icon> combined = Arrays.asList(new Icon[] { iconNew, iconCut });
        iconBoth = new CombinedIcon(combined, 2);
        iconClosed = UIManager.getIcon("Tree.closedIcon");
        iconOpened = UIManager.getIcon("Tree.openIcon");

        setOpaque(true);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        setIcon(value, expanded);
        setColors(selected, row);
        String title = covertToString(value);
        setText(title);
        setEnabled(tree.isEnabled());
        return this;
    }

    private void setColors(boolean selected, int row) {
        if (selected) {
            setBackground(UIManager.getColor("Tree.selectionBackground"));
            setForeground(UIManager.getColor("Tree.selectionForeground"));
        } else {
            setForeground(UIManager.getColor("Tree.textForeground"));
            if (row % 2 != 0) {
                setBackground(new Color(250, 250, 220));
            } else {
                setBackground(UIManager.getColor("Tree.textBackground"));
            }
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
                    title += "</html>";
                }
            }
        }
        return title;
    }

    private void setIcon(Object value, boolean expanded) {
        if (value instanceof Recording) {
            Recording recording = (Recording) value;
            setHorizontalTextPosition(JLabel.LEADING);
            setIconTextGap(10);
            if (recording.isNew()) {
                if (recording.isCut()) {
                    setIcon(iconBoth);
                    setDisabledIcon(iconBoth);
                } else {
                    setIcon(iconNew);
                    setDisabledIcon(iconNew);
                }
            } else if (recording.isCut()) {
                setIcon(iconCut);
                setDisabledIcon(iconCut);
            } else {
                setIcon(null);
                setDisabledIcon(null);
            }
        } else {
            setHorizontalTextPosition(JLabel.TRAILING);
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
        private List<Icon> icons = new ArrayList<Icon>();
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
