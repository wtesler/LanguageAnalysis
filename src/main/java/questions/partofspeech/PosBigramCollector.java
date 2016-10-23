package questions.partofspeech;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import classifier.frequency.NodeFrequencyCollector;
import models.Node;
import models.PartOfSpeech;

public class PosBigramCollector extends NodeFrequencyCollector<String> {

    @Override
    public Stream<String> getModelStream(Node node) {
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
