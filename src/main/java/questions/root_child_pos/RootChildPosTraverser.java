package questions.root_child_pos;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import models.DependencyEdge;
import models.Node;
import models.PartOfSpeech;

public class RootChildPosTraverser {

    public static void collectRootChildSet(Node node, final HashSet<String> rootChildSet) {

        IntStream.range(0, node.children.size()).forEach(i -> collectRootChildSet(node.children.get(i), rootChildSet));

        getRootChildStream(node)
                .collect(Collectors.toCollection((Supplier<Collection<String>>) () -> rootChildSet));
    }

    public static void collectRootChildCountInNode(Node node, final HashMap<String, Double> rootChildCountMap) {

        IntStream.range(0, node.children.size()).forEach(i
                -> collectRootChildCountInNode(node.children.get(i), rootChildCountMap));

        getRootChildStream(node)
                .forEach(siblings -> rootChildCountMap.put(siblings, rootChildCountMap.containsKey(siblings) ?
                        rootChildCountMap.get(siblings) + 1 : 1));
    }

    public static Stream<String> getRootChildStream(Node node) {
        return IntStream.range(0, node.children.size())
                .mapToObj(i -> {
                    String label1 = node.token.partOfSpeech.tag;
                    String label2 = node.children.get(i).token.partOfSpeech.tag;

                    if (label1.equals(PartOfSpeech.PUNCTUATION) || label2.equals(PartOfSpeech.PUNCTUATION)) {
                        // Shouldn't take punctuation into account.
                        return null;
                    }

//                    if (i == node.children.size() - 1) {
//                        return null;
//                    }
//
//                    String label1 = node.children.get(i).token.dependencyEdge.label;
//                    String label2 = node.children.get(i + 1).token.dependencyEdge.label;
//
//                    if (label1.equals(DependencyEdge.P) || label2.equals(DependencyEdge.P)) {
//                        // Shouldn't take punctuation into account.
//                        return null;
//                    }

                    return label1 + "," + label2;
                })
                .filter(s -> s != null);
    }
}
