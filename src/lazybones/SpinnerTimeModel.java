package lazybones;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class SpinnerTimeModel implements SpinnerModel {

  private Time time = new Time();

  private Vector changeListener = new Vector();
  
  public Object getNextValue() {
    time.increase();
    fireStateChanged();
    return time;
  }

  public Object getPreviousValue() {
    time.decrease();
    fireStateChanged();
    return time;
  }

  public Object getValue() {
    return time;
  }

  public void setValue(Object o) {
    if(o instanceof Time) {
      time = (Time) o;
      fireStateChanged();
    }
  }


  public void addChangeListener(ChangeListener arg0) {
    changeListener.add(arg0);
  }


  public void removeChangeListener(ChangeListener arg0) {
    changeListener.remove(arg0);
  }

  private void fireStateChanged() {
    Iterator it = changeListener.iterator();
    while (it.hasNext()) {
      ChangeListener cl = (ChangeListener) it.next();
      cl.stateChanged(new ChangeEvent(this));
    }
  }
}