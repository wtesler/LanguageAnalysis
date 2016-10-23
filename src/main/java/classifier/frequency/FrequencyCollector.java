package classifier.frequency;

import java.util.HashMap;
import java.util.HashSet;

public abstract class FrequencyCollector<T, S> {

    public abstract void collectModelSet(T input, final HashSet<S> modelSet);

    public abstract void collectModelCountMap(T input, final HashMap<S, Double> modelCountMap);
}
