package lazybones;

import javax.swing.JPanel;
import javax.swing.JTextField;


public abstract class BrowsePanel extends JPanel {
  
  JTextField textfield;
  
  public void setTextField(JTextField textfield) {
    this.textfield = textfield;
  }
}
