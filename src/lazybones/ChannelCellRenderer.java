package lazybones;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;


public class ChannelCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = -1450755081437236476L;

    private Color unselectedForeground;

    private Color unselectedBackground;

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (column == 0) {
            super.setForeground((unselectedForeground != null) ? unselectedForeground
                            : table.getForeground());
            super.setBackground((unselectedBackground != null) ? unselectedBackground
                            : table.getBackground());

            setFont(table.getFont());
            setBorder(noFocusBorder);
            setValue(value);
        } else {
            if (isSelected) {
                super.setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            } else {
                super
                        .setForeground((unselectedForeground != null) ? unselectedForeground
                                : table.getForeground());
                super
                        .setBackground((unselectedBackground != null) ? unselectedBackground
                                : table.getBackground());
            }

            setFont(table.getFont());

            if (hasFocus) {
                setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
                if (!isSelected && table.isCellEditable(row, column)) {
                    Color col;
                    col = UIManager.getColor("Table.focusCellForeground");
                    if (col != null) {
                        super.setForeground(col);
                    }
                    col = UIManager.getColor("Table.focusCellBackground");
                    if (col != null) {
                        super.setBackground(col);
                    }
                }
            } else {
                setBorder(noFocusBorder);
            }

            setValue(value);
        }

        return this;
    }
}