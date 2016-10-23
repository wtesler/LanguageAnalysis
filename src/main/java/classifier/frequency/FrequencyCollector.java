package classifier.frequency;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public abstract class FrequencyCollector<T, S> {

    public abstract void collectModelSet(T input, final HashSet<S> modelSet);

    public abstract void collectModelCountMap(T input, final ConcurrentHashMap<S, Double> modelCountMap);
}
