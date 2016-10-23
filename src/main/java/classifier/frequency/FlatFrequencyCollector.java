package classifier.frequency;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class FlatFrequencyCollector<T, S> extends FrequencyCollector<T, S> {

    public abstract Stream<S> getModelStream(T input);

    public void collectModelSet(T input, final HashSet<S> modelSet) {
        getModelStream(input).collect(Collectors.toCollection((Supplier<Collection<S>>) () -> modelSet));
    }

    public void collectModelCountMap(T input, final HashMap<S, Double> modelCountMap) {
        getModelStream(input).forEach(model -> {
            modelCountMap.put(model, modelCountMap.containsKey(model) ? modelCountMap.get(model) + 1 : 1);
        });
    }
}
