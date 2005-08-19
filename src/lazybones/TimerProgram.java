package lazybones;

import tvdataservice.MutableProgram;
import de.hampelratte.svdrp.responses.highlevel.VDRTimer;
import devplugin.Channel;
import devplugin.Date;

/**
 * A Program, which contains its' according timer 
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net</a>
 *
 */
public class TimerProgram extends MutableProgram {

  private VDRTimer timer;
  
  public TimerProgram(Channel arg0, Date arg1) {
    super(arg0, arg1);
  }
  
  public TimerProgram(Channel arg0, Date arg1, int arg2, int arg3) {
    super(arg0, arg1, arg2, arg3);
  }
  
  public VDRTimer getTimer() {
    return timer;
  }
  
  public void setTimer(VDRTimer timer) {
    this.timer = timer;
  }
}
