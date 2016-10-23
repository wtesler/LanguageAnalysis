package questions.siblings;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import classifier.frequency.NodeFrequencyCollector;
import models.DependencyEdge;
import models.Node;

public class SiblingCollector extends NodeFrequencyCollector<String> {

    @Override
    public Stream<String> getModelStream(Node node) {
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
