package classifier.frequency;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import models.LanguageResponse;
import models.Node;
import utils.LanguageUtils;

public abstract class NodeFrequencyCollector<S> extends FrequencyCollector<LanguageResponse, S> {

    public abstract Stream<S> getModelStream(Node input);

    @Override
    public void collectModelSet(LanguageResponse response, final HashSet<S> modelSet) {
        Node node = LanguageUtils.toDependencyTree(response).getRoot();
        collectModelSet(node, modelSet);
    }

    private void collectModelSet(Node node, final HashSet<S> modelSet) {
        node.children.stream().forEach(child -> collectModelSet(child, modelSet));
        getModelStream(node).collect(Collectors.toCollection((Supplier<Collection<S>>) () -> modelSet));
    }

    @Override
    public void collectModelCountMap(LanguageResponse response, final HashMap<S, Double> modelCountMap) {
        Node node = LanguageUtils.toDependencyTree(response).getRoot();
        collectModelCountMap(node, modelCountMap);
    }

    private void collectModelCountMap(Node node, final HashMap<S, Double> modelCountMap) {
        node.children.stream().forEach(child -> collectModelCountMap(child, modelCountMap));
        getModelStream(node).forEach(model -> {
            modelCountMap.put(model, modelCountMap.containsKey(model) ? modelCountMap.get(model) + 1 : 1);
        });
    }
}