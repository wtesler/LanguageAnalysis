package bayes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;
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

                    if (node.children.get(i).token.dependencyEdge.label.equals(DependencyEdge.P)
                            || node.children.get(i + 1).token.dependencyEdge.label.equals(DependencyEdge.P)) {
                        // QuestionClassifier shouldn't take punctuation into account.
                        return null;
                    }

                    String siblingLabel = node.children.get(i).token.dependencyEdge.label
                            + ","
                            + node.children.get(i + 1).token.dependencyEdge.label;

                    return siblingLabel;
                })
                .filter(s -> s != null);
    }

    /**
     * @param siblingLabel i.e. "AUX,NSUBJ"
     */
    public static boolean doesTreeContainSibling(Node node, String siblingLabel) {
        for (int i = 0; i < node.children.size() - 1; i++) {
            String nodeSiblingLabel = node.children.get(i).token.dependencyEdge.label
                    + ","
                    + node.children.get(i + 1).token.dependencyEdge.label;

            if (nodeSiblingLabel.equals(siblingLabel)) {
                return true;
            }

            if (doesTreeContainSibling(node.children.get(i), siblingLabel)) {
                return true;
            }
        }
        return false;
    }

    public static void findSiblingsInTree(Node node, String siblingA, String siblingB, boolean[] siblingFound) {
        for (int i = 0; i < node.children.size() - 1; i++) {
            String nodeSibling = node.children.get(i).token.dependencyEdge.label
                    + ","
                    + node.children.get(i + 1).token.dependencyEdge.label;

            if (nodeSibling.equals(siblingA)) {
                siblingFound[0] = true;
            } else if (nodeSibling.equals(siblingB)) {
                siblingFound[1] = true;
            }

            findSiblingsInTree(node.children.get(i), siblingA, siblingB, siblingFound);
        }
    }
}
