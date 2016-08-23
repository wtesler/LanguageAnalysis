package questions.partofspeech;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import models.Node;
import models.PartOfSpeech;

public class PosTraverser {

    public static void collectPosBigrams(Node node, final HashSet<String> siblingSet) {

        IntStream.range(0, node.children.size()).forEach(i -> collectPosBigrams(node.children.get(i), siblingSet));

        getPosBigramStream(node)
                .collect(Collectors.toCollection((Supplier<Collection<String>>) () -> siblingSet));
    }

    public static void collectPosBigramCount(Node node, final HashMap<String, Double> siblingCountMap) {

        IntStream.range(0, node.children.size()).forEach(i
                -> collectPosBigramCount(node.children.get(i), siblingCountMap));

        getPosBigramStream(node)
                .forEach(siblings -> {
                    siblingCountMap.put(siblings, siblingCountMap.containsKey(siblings) ?
                            siblingCountMap.get(siblings) + 1 : 1);
                });
    }

    public static Stream<String> getPosBigramStream(Node node) {
        return IntStream.range(0, node.children.size())
                .mapToObj(i -> {
                    if (i == node.children.size() - 1) {
                        return null;
                    }

                    String label1 = node.children.get(i).token.partOfSpeech.tag;
                    String label2 = node.children.get(i + 1).token.partOfSpeech.tag;

                    if (label1.equals(PartOfSpeech.PUNCTUATION) || label2.equals(PartOfSpeech.PUNCTUATION)) {
                        // Shouldn't take punctuation into account.
                        return null;
                    }

                    return label1 + "," + label2;
                })
                .filter(s -> s != null);
    }
}
