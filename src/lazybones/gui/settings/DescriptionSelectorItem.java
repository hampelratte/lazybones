package lazybones.gui.settings;

public class DescriptionSelectorItem {
    private String id;
    private String name;

    public static final String VDR = "vdr";
    public static final String TVB_PREFIX = "tvb_";
    public static final String TVB_DESC = TVB_PREFIX + "desc";
    public static final String LONGEST = "longest";
    public static final String TIMER = "timer";

    public DescriptionSelectorItem(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
