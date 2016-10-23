package question.ensemble;

import classifier.ensemble.EnsembleClassifier;
import classifier.frequency.FrequencyClassifer;
import models.LanguageResponse;
import question.keywords.InitialWordClassifier;
import question.keywords.QuestionWordsClassifier;
import question.partofspeech.PosBigramCollector;
import question.siblings.SiblingCollector;

public class QuestionEnsembleClassifier extends EnsembleClassifier<LanguageResponse> {

    public QuestionEnsembleClassifier() {
        addClassifier(new FrequencyClassifer<>(new SiblingCollector()));
        //addClassifier(new FrequencyClassifer<>(new FlatEdgeCollector()));
        addClassifier(new QuestionWordsClassifier());
        addClassifier(new InitialWordClassifier());
        addClassifier(new FrequencyClassifer<>(new PosBigramCollector()));
        //addClassifier(new FrequencyClassifer<>(new RootChildCollector()));
    }

    @Override
    public double classify(LanguageResponse model, boolean interactive) {
        double decision = super.classify(model, false);
        if (interactive) {
            if (decision > 0) {
                System.out.println("This is a question.");
            } else {
                System.out.println("This is a statement.");
            }
        }
        return decision;
    }
}
