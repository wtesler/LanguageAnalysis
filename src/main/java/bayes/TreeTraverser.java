package bayes;

import java.util.HashMap;
import java.util.stream.IntStream;

import models.DependencyTree;

public class TreeTraverser {

    public static void collectSiblingsInNode(DependencyTree.Node node, final HashMap<String, Double> siblingCountMap) {
        IntStream.range(0, node.children.size() - 1).forEach(i -> {

            collectSiblingsInNode(node.children.get(i), siblingCountMap);

            if (i == node.children.size() - 1) {
                return;
            }

            String siblingLabels = node.children.get(i).token.dependencyEdge.label
                            + ","
                            + node.children.get(i + 1).token.dependencyEdge.label;

            siblingCountMap.put(siblingLabels,
                    siblingCountMap.containsKey(siblingLabels) ?
                            siblingCountMap.get(siblingLabels) + 1
                            : 1);
        });
    }
}
