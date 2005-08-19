package lazybones;

public class Time {
  int hour = 0;
  int minute = 0;
  
  public Time() {}
  
  public Time (int hour, int minute) {
    this.hour = hour;
    this.minute = minute;
  }
  
  public void increase() {
    minute++;
    if(minute == 60) {
      minute = 0;
      hour++;
      if(hour == 24) {
        hour = 0;
      }
    }
  }
  
  public void decrease() {
    minute--;
    if(minute == -1) {
      minute = 59;
      hour--;
      if(hour == -1) {
        hour = 23;
      }
    }
  }
  
  public String toString() {
    String h = (hour<10) ? "0"+hour : Integer.toString(hour);
    String m = (minute<10) ? "0"+minute : Integer.toString(minute);
    return h+":"+m;
  }
  
  public int getHour() {
    return hour;
  }
  
  public void setHour(int hour) {
    this.hour = hour;
  }
  
  public int getMinute() {
    return minute;
  }
  
  public void setMinute(int minute) {
    this.minute = minute;
  }
}