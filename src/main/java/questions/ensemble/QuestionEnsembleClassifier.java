package questions.ensemble;

import classifier.EnsembleClassifier;
import models.LanguageResponse;
import questions.flatedge.FlatEdgeClassifier;
import questions.keywords.InitialWordClassifier;
import questions.keywords.QuestionWordsClassifier;
import questions.partofspeech.PosBigramClassifier;
import questions.siblings.SiblingClassifier;

public class QuestionEnsembleClassifier extends EnsembleClassifier<LanguageResponse> {

    public QuestionEnsembleClassifier() {
        addClassifier(new SiblingClassifier());
        //addClassifier(new FlatEdgeClassifier());
        addClassifier(new QuestionWordsClassifier());
        addClassifier(new InitialWordClassifier());
        addClassifier(new PosBigramClassifier());
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
