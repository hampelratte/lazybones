package lazybones;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.sf.nachocalendar.CalendarFactory;
import net.sf.nachocalendar.components.DatePanel;
import net.sf.nachocalendar.event.DateSelectionEvent;
import net.sf.nachocalendar.event.DateSelectionListener;
import net.sf.nachocalendar.model.DateSelectionModel;
import de.hampelratte.svdrp.responses.highlevel.VDRTimer;

// TODO beautify this panel
public class DayChooser extends BrowsePanel implements ActionListener,
    DateSelectionListener {

  private static final long serialVersionUID = -2936338063641916673L;

  private static final util.ui.Localizer mLocalizer = util.ui.Localizer
  .getLocalizerFor(DayChooser.class);
  
  private VDRTimer timer;

  //private Calendar startDate;

  private JCheckBox monday = new JCheckBox(mLocalizer.msg("monday","Monday"));

  private JCheckBox tuesday = new JCheckBox(mLocalizer.msg("tuesday","Tuesday"));

  private JCheckBox wednesday = new JCheckBox(mLocalizer.msg("wednesday","Wednesday"));

  private JCheckBox thursday = new JCheckBox(mLocalizer.msg("thursday","Thursday"));

  private JCheckBox friday = new JCheckBox(mLocalizer.msg("friday","Friday"));

  private JCheckBox saturday = new JCheckBox(mLocalizer.msg("saturday","Saturday"));

  private JCheckBox sunday = new JCheckBox(mLocalizer.msg("sunday","Sunday"));

  private DatePanel cal = CalendarFactory.createDatePanel();


  public DayChooser(VDRTimer timer) {
    this.timer = timer;

    initGUI();
  }


  private void initGUI() {
    JPanel days = new JPanel();
    days.add(monday);
    days.add(tuesday);
    days.add(wednesday);
    days.add(thursday);
    days.add(friday);
    days.add(saturday);
    days.add(sunday);

    this.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor = GridBagConstraints.WEST;
    add(days, gbc);
    
    gbc.gridx = 0;
    gbc.gridy = 1;
    add(cal, gbc);

    monday.addActionListener(this);
    tuesday.addActionListener(this);
    wednesday.addActionListener(this);
    thursday.addActionListener(this);
    friday.addActionListener(this);
    saturday.addActionListener(this);
    sunday.addActionListener(this);

    cal.setSelectionMode(DateSelectionModel.SINGLE_SELECTION);
    cal.getDateSelectionModel().addDateSelectionListener(this);

    this.setSize(300, 200);
  }


  public void actionPerformed(ActionEvent e) {
    userInputHappened();
  }


  private void updateTimer() {
    boolean[] repeatingDays = new boolean[7];

    repeatingDays[0] = monday.isSelected();
    repeatingDays[1] = tuesday.isSelected();
    repeatingDays[2] = wednesday.isSelected();
    repeatingDays[3] = thursday.isSelected();
    repeatingDays[4] = friday.isSelected();
    repeatingDays[5] = saturday.isSelected();
    repeatingDays[6] = sunday.isSelected();

    timer.setRepeatingDays(repeatingDays);
  }

  public void valueChanged(DateSelectionEvent e) {
    Object o = cal.getDateSelectionModel().getSelectedDate();
    if (o != null) {
      Calendar startDate = GregorianCalendar.getInstance();
      startDate.setTimeInMillis(cal.getDate().getTime());

      if(timer.isRepeating()) {
        timer.setFirstTime(startDate);
        timer.setHasFirstTime(true);
      } else {
        Calendar start = timer.getStartTime();
        start.set(Calendar.YEAR, startDate.get(Calendar.YEAR));
        start.set(Calendar.MONTH, startDate.get(Calendar.MONTH));
        start.set(Calendar.DAY_OF_MONTH, startDate.get(Calendar.DAY_OF_MONTH));
        timer.setStartTime(start);
        
        Calendar end = timer.getStartTime();
        end.set(Calendar.YEAR, startDate.get(Calendar.YEAR));
        end.set(Calendar.MONTH, startDate.get(Calendar.MONTH));
        end.set(Calendar.DAY_OF_MONTH, startDate.get(Calendar.DAY_OF_MONTH));
        timer.setStartTime(end);
      }
      
      userInputHappened();
    }
  }
  
  private void userInputHappened() {
    updateTimer();
    String dayString = timer.getDayString();
    super.textfield.setText(dayString);
  }
}