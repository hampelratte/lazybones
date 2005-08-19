package lazybones;

import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;

import devplugin.Program;

/**
 * Compares two objects, which implement the interface Program
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 * 
 */
public class ProgramComparator implements Comparator {

  public int compare(Object o1, Object o2) {
    Program prog1 = (Program) o1;
    Program prog2 = (Program) o2;
    if (!prog1.getChannel().getId().equals(prog2.getChannel().getId()))
      return -1;

    Calendar cal = GregorianCalendar.getInstance();
    cal.set(Calendar.YEAR, prog1.getDate().getYear());
    cal.set(Calendar.MONTH, prog1.getDate().getMonth());
    cal.set(Calendar.DAY_OF_MONTH, prog1.getDate().getDayOfMonth());
    cal.set(Calendar.HOUR_OF_DAY, prog1.getHours());
    cal.set(Calendar.MINUTE, prog1.getMinutes());

    Calendar thisCal = GregorianCalendar.getInstance();
    thisCal.set(Calendar.YEAR, prog2.getDate().getYear());
    thisCal.set(Calendar.MONTH, prog2.getDate().getMonth());
    thisCal.set(Calendar.DAY_OF_MONTH, prog2.getDate().getDayOfMonth());
    thisCal.set(Calendar.HOUR_OF_DAY, prog2.getHours());
    thisCal.set(Calendar.MINUTE, prog2.getMinutes());

    if (cal.get(Calendar.YEAR) < thisCal.get(Calendar.YEAR)) {
      return -1;
    } else if (cal.get(Calendar.YEAR) > thisCal.get(Calendar.YEAR)) {
      return 1;
    }
    // at this point: year is equal

    if (cal.get(Calendar.MONTH) < thisCal.get(Calendar.MONTH)) {
      return -1;
    } else if (cal.get(Calendar.MONTH) > thisCal.get(Calendar.MONTH)) {
      return 1;
    }
    // at this point: month is equal

    if (cal.get(Calendar.DAY_OF_MONTH) < thisCal.get(Calendar.DAY_OF_MONTH)) {
      return -1;
    } else if (cal.get(Calendar.DAY_OF_MONTH) > thisCal
        .get(Calendar.DAY_OF_MONTH)) {
      return 1;
    }
    // at this point: day is equal

    if (cal.get(Calendar.HOUR_OF_DAY) < thisCal.get(Calendar.HOUR_OF_DAY)) {
      return -1;
    } else if (cal.get(Calendar.HOUR_OF_DAY) > thisCal
        .get(Calendar.HOUR_OF_DAY)) {
      return 1;
    }
    // at this point: hour is equal

    if (cal.get(Calendar.MINUTE) < thisCal.get(Calendar.MINUTE)) {
      return -1;
    } else if (cal.get(Calendar.MINUTE) > thisCal.get(Calendar.MINUTE)) {
      return 1;
    }
    // at this point: minute is equal

    return 0;
  }
}