package lazybones;

import java.io.Serializable;

/**
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net</a>
 *
 */
public class VDRChannel implements Serializable {

    private static final long serialVersionUID = -4245656957614650646L;
    private int id = -1;
    private String name = "";
    
    public VDRChannel() {}
    
    public VDRChannel(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String toString() {
        return getName();
    }
    
    public boolean equals(Object o) {
      if(o instanceof VDRChannel) {
        VDRChannel c = (VDRChannel)o;
        if( c.getId() == this.id && c.getName().equals(this.name) ) {
          return true;
        }
      }
      return false;
    }
}
