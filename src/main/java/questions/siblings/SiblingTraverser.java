package questions.siblings;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import models.DependencyEdge;
import models.Node;

public class SiblingTraverser {

    public static void collectSiblingSet(Node node, final HashSet<String> siblingSet) {

        IntStream.range(0, node.children.size()).forEach(i -> collectSiblingSet(node.children.get(i), siblingSet));

        getSiblingStream(node)
                .collect(Collectors.toCollection((Supplier<Collection<String>>) () -> siblingSet));
    }

    public static void collectSiblingCountInNode(Node node, final HashMap<String, Double> siblingCountMap) {

        IntStream.range(0, node.children.size()).forEach(i
                -> collectSiblingCountInNode(node.children.get(i), siblingCountMap));

        getSiblingStream(node)
                .forEach(siblings -> {
                    siblingCountMap.put(siblings, siblingCountMap.containsKey(siblings) ?
                            siblingCountMap.get(siblings) + 1 : 1);
                });
    }

    public static Stream<String> getSiblingStream(Node node) {
        return IntStream.range(0, node.children.size())
                .mapToObj(i -> {
                    if (i == node.children.size() - 1) {
                        return null;
                    }

                    String label1 = node.children.get(i).token.dependencyEdge.label;
                    String label2 = node.children.get(i + 1).token.dependencyEdge.label;

                    if (label1.equals(DependencyEdge.P) || label2.equals(DependencyEdge.P)) {
                        // Shouldn't take punctuation into account.
                        return null;
                    }

                    return label1 + "," + label2;
                })
                .filter(s -> s != null);
    }
}
