package lazybones;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.Popup;
import javax.swing.PopupFactory;

public class BrowseTextField extends JPanel implements ActionListener {

  private static final long serialVersionUID = 4091440584044481373L;
  private JToggleButton button = new JToggleButton("...");
  private JTextField textfield = new JTextField(10);
  private Popup popup;
  private BrowsePanel panel;
  private boolean dialogVisible = false;

  public BrowseTextField(BrowsePanel panel) {
    this.panel = panel;
    panel.setTextField(textfield);
    panel.setBorder(BorderFactory.createLineBorder(Color.RED));

    /*
    panel.setBackground(UIManager.getColor("ToolTip.background"));
    for (int i = 0; i < panel.getComponents().length; i++) {
      panel.getComponent(i).setBackground(UIManager.getColor("ToolTip.background"));
    }*/
    
    this.setLayout(new BorderLayout());
    this.add(textfield, BorderLayout.CENTER);
    this.add(button, BorderLayout.EAST);
    
    button.addActionListener(this);
  }
  
  public void showDialog() {
    PopupFactory factory = PopupFactory.getSharedInstance();
    int x = (int)button.getLocationOnScreen().getX() + button.getWidth() + 10;
    int y = (int)button.getLocationOnScreen().getY();
    popup = factory.getPopup(this, panel, x, y);
    popup.show();
  }
  
  public void hideDialog() {
    popup.hide();
  }

  public void actionPerformed(ActionEvent e) {
    if(e.getSource() == button) {
      if(dialogVisible) {
        hideDialog();
      } else {
        showDialog();
      }
      dialogVisible = !dialogVisible;
    }
  }
  
  public void setEditable(boolean editable) {
    textfield.setEditable(editable);
  }
  
  public void setEnabled(boolean enabled) {
    textfield.setEnabled(enabled);
    button.setEnabled(enabled);
  }
  
  public void setText(String text) {
    textfield.setText(text);
  }
}