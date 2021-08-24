package lazybones.conflicts;

import java.util.HashSet;
import java.util.Set;

import lazybones.LazyBonesTimer;
import lazybones.utils.Period;

public class Conflict {
    private Period period = new Period();
    private Set<LazyBonesTimer> involvedTimers = new HashSet<>();

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Set<LazyBonesTimer> getInvolvedTimers() {
        return involvedTimers;
    }

    public void setInvolvedTimers(Set<LazyBonesTimer> involvedTimers) {
        this.involvedTimers = involvedTimers;
    }
}
