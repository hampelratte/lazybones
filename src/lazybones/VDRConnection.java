package lazybones;

import javax.swing.JOptionPane;

import de.hampelratte.svdrp.Command;
import de.hampelratte.svdrp.Connection;
import de.hampelratte.svdrp.Response;

/**
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 *  
 */
public class VDRConnection {
  private static final util.ui.Localizer mLocalizer = util.ui.Localizer
      .getLocalizerFor(VDRConnection.class);

  private static Connection connection;

  protected static String host;

  protected static int port;


  public static Response send(Command cmd) {
    Response res = null;
    try {
      connection = new Connection(VDRConnection.host, VDRConnection.port);
      Connection.DEBUG = false;
      res = connection.send(cmd);
      connection.close();
    } catch (Exception e1) {
      JOptionPane.showMessageDialog(null, mLocalizer.msg("couldnt_connect",
          "Couldn't connect to VDR")+ ":\n" + e1.toString());
    } 
    return res;
  }


  public static boolean isAvailable() {
    try {
      connection = new Connection(VDRConnection.host, VDRConnection.port);
      connection.close();
      return true;
    } catch (Exception e1) {
    }
    return false;
  }


  public static Connection getConnection() {
    return connection;
  }
}