/*
 * Created on 25.03.2005
 *
 */
package lazybones;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import de.hampelratte.svdrp.Response;
import de.hampelratte.svdrp.commands.GRAB;

/**
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 *  
 */
public class PreviewPanel extends JLabel {

  private static final long serialVersionUID = -6728708348263401257L;

  private static final util.ui.Localizer mLocalizer = util.ui.Localizer
  .getLocalizerFor(PreviewPanel.class);
  
  private ImageIcon image = new ImageIcon();

  private PreviewGrabber pg;
  
  private LazyBones control;

  public PreviewPanel(LazyBones control) {
    this.control = control;
    initGUI();
  }

  private void initGUI() {
    setBorder(BorderFactory.createLineBorder(Color.WHITE));
  }

  public void startGrabbing() {
    pg = new PreviewGrabber();
    pg.start();
  }

  public void stopGrabbing() {
    if (pg != null) {
      pg.stopThread();
    }
  }

  private class PreviewGrabber extends Thread {

    private boolean running = true;

    public void run() {
      GRAB grab = new GRAB(control.getProperties().getProperty("preview.path"));
      grab.setQuality("80");
      grab.setFormat("jpeg");

      while (running) {
        try {
          Thread.sleep(1000);
          int width = getWidth();
          int height = getHeight();
          grab.setResolution(width + " " + height);
          Response res = VDRConnection.send(grab);
          if(res.getCode() == 250) {
            URL url = new URL(control.getProperties().getProperty("preview.url"));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            InputStream in = con.getInputStream();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int length = -1;
            while ((length = in.read(buffer)) >= 0) {
              bos.write(buffer, 0, length);
            }
            in.close();
            con.disconnect();
            image = new ImageIcon(bos.toByteArray());
            setIcon(image);
            repaint();
          }
        } catch (Exception e) {
          running = false;
          String mesg = mLocalizer.msg("couldnt_grab","Coulnd't grab picture:\n");
          JOptionPane.showMessageDialog(null,mesg + e);
        }
      }
    }

    public void stopThread() {
      running = false;
    }
  }
}