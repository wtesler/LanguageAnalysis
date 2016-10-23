package question.flatedge;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import classifier.frequency.FrequencyCollector;
import models.LanguageResponse;

public class FlatEdgeCollector extends FrequencyCollector<LanguageResponse, String> {

    @Override
    public void collectModelSet(LanguageResponse response, final HashSet<String> modelSet) {
        getModelStream(response).collect(Collectors.toCollection((Supplier<Collection<String>>) () -> modelSet));
    }

    @Override
    public void collectModelCountMap(LanguageResponse response, final ConcurrentHashMap<String, Double> modelCountMap) {
        getModelStream(response).forEach(model
                -> modelCountMap.put(model, modelCountMap.containsKey(model) ? modelCountMap.get(model) + 1 : 1));
    }

    public Stream<String> getModelStream(LanguageResponse response) {
        return IntStream.range(0, response.tokens.size())
                .mapToObj(i -> {
                    if (i == response.tokens.size() - 1) {
                        return null;
                    }

                    String label1 = response.tokens.get(i).lemma;
                    String label2 = response.tokens.get(i + 1).lemma;
                    return label1 + "," + label2;
                })
                .filter(s -> s != null);
    }
}
