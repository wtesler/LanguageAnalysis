package questions.ensemble;

import classifier.EnsembleClassifier;
import models.LanguageResponse;
import questions.flatedge.FlatEdgeClassifier;
import questions.keywords.InitialWordClassifier;
import questions.keywords.QuestionWordsClassifier;
import questions.partofspeech.PosBigramClassifier;
import questions.siblings.SiblingClassifier;

public class EnsembleQuestionClassifier extends EnsembleClassifier<LanguageResponse> {

    public EnsembleQuestionClassifier() {
        addClassifier(new SiblingClassifier());
        addClassifier(new FlatEdgeClassifier());
        addClassifier(new QuestionWordsClassifier());
        addClassifier(new InitialWordClassifier());
        addClassifier(new PosBigramClassifier());
    }
}
