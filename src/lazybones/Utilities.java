package lazybones;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.JViewport;

/**
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 * 
 * Utility class with different functions
 */
public class Utilities {

  /*
  public static int percentageOfCommonWords(String s, String t) {
    if (s == null || t == null) {
      return 0;
    }

    s = s.toLowerCase();
    s = s.replaceAll("-", " ");
    s = s.replaceAll(":", " ");
    s = s.replaceAll(";", " ");
    s = s.replaceAll("\\|", " ");
    s = s.replaceAll("_", " ");
    s = s.replaceAll("\\.", "\\. ");
    s = s.trim();
    t = t.toLowerCase();
    t = t.replaceAll("-", " ");
    t = t.replaceAll(":", " ");
    t = t.replaceAll(";", " ");
    t = t.replaceAll("\\|", " ");
    t = t.replaceAll("_", " ");
    t = t.replaceAll("\\.", "\\. ");
    t = t.trim();
    String[] s1Words = s.split(" ");
    String[] s2Words = t.split(" ");

    String[] searchWords;
    String[] title;
    if (s1Words.length > s2Words.length) {
      title = s1Words;
      searchWords = s2Words;
    } else {
      title = s2Words;
      searchWords = s1Words;
    }

    // count the words, which both titles have
    // in common
    int matchCount = 0;
    for (int i = 0; i < searchWords.length; i++) {
      if (contains(title, searchWords[i])) {
        matchCount++;
      }
    }

    // calculate the percentage of common words
    int percent = (int) ((double) matchCount * 100 / (double) searchWords.length);
    // System.out.println(s1+"="+s2+" "+percent+"%");
    return percent;
  }
  

  private static boolean contains(String[] array, String query) {
    for (int i = 0; i < array.length; i++) {
      if (array[i].equals(query)) {
        return true;
      }
    }
    return false;
  }
  
  */

  
  public static int percentageOfEquality(String s, String t) {
    if (s == null || t == null) {
      return 0;
    }

    s = s.toLowerCase();
    s = s.replaceAll("-", " ");
    s = s.replaceAll(":", " ");
    s = s.replaceAll(";", " ");
    s = s.replaceAll("\\|", " ");
    s = s.replaceAll("_", " ");
    s = s.replaceAll("\\.", "\\. ");
    s = s.trim();
    t = t.toLowerCase();
    t = t.replaceAll("-", " ");
    t = t.replaceAll(":", " ");
    t = t.replaceAll(";", " ");
    t = t.replaceAll("\\|", " ");
    t = t.replaceAll("_", " ");
    t = t.replaceAll("\\.", "\\. ");
    t = t.trim();
    
    int levenshteinDistance = Utilities.getLevenshteinDistance(s,t);
    int length = Math.max(s.length(), t.length());
    
    // calculate the percentage of equality
    int percent = 100 - (int)((double) levenshteinDistance * 100 / (double) length);
    //System.out.println(s+"="+t+" "+percent+"%");
    return percent;
  }

  public static int getLevenshteinDistance(String s, String t) {
    int n = s.length();
    int m = t.length();
    int d[][] = new int[n + 1][m + 1];
    int i;
    int j;
    int cost;

    if (n == 0) {
      return m;
    }
    if (m == 0) {
      return n;
    }

    for (i = 0; i <= n; i++) {
      d[i][0] = i;
    }
    for (j = 0; j <= m; j++) {
      d[0][j] = j;
    }

    for (i = 1; i <= n; i++) {
      for (j = 1; j <= m; j++) {
        if (s.charAt(i - 1) == t.charAt(j - 1)) {
          cost = 0;
        } else {
          cost = 1;
        }

        d[i][j] = min(d[i - 1][j] + 1,         // insertion
                      d[i][j - 1] + 1,         // deletion
                      d[i - 1][j - 1] + cost); // substitution
      }
    }
    return d[n][m];
  }


  private static int min(int a, int b, int c) {
    if (b < a) {
      a = b;
    }
    if (c < a) {
      a = c;
    }
    return a;
  }


  public static boolean isCellVisible(JTable table, int rowIndex, int vColIndex) {
    if (!(table.getParent() instanceof JViewport)) {
      return false;
    }
    JViewport viewport = (JViewport) table.getParent();

    // This rectangle is relative to the table where the
    // northwest corner of cell (0,0) is always (0,0)
    Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);

    // The location of the viewport relative to the table
    Point pt = viewport.getViewPosition();

    // Translate the cell location so that it is relative
    // to the view, assuming the northwest corner of the
    // view is (0,0)
    rect.setLocation(rect.x - pt.x, rect.y - pt.y);

    // Check if view completely contains cell
    return new Rectangle(viewport.getExtentSize()).contains(rect);
  }


  public static void scrollToVisible(JTable table, int rowIndex, int vColIndex) {
    if (!(table.getParent() instanceof JViewport)) {
      return;
    }
    JViewport viewport = (JViewport) table.getParent();

    // This rectangle is relative to the table where the
    // northwest corner of cell (0,0) is always (0,0).
    Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);

    // The location of the viewport relative to the table
    Point pt = viewport.getViewPosition();

    // Translate the cell location so that it is relative
    // to the view, assuming the northwest corner of the
    // view is (0,0)
    rect.setLocation(rect.x - pt.x, rect.y - pt.y);

    // Scroll the area into view
    viewport.scrollRectToVisible(rect);
  }
}