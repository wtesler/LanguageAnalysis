package question_price.node;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.BaseApp;
import classifier.ClassifierNode;
import classifier.Score;
import models.language.LanguageResponse;
import question_price.ensemble.PriceEnsembleClassifier;
import utils.FileUtils;

public class PriceClassifierNode extends ClassifierNode<LanguageResponse> {

    @Inject Gson mGson;

    public PriceClassifierNode(PriceEnsembleClassifier classifier, BaseApp app) {
        super(classifier, app);
        app.getAppComponent().inject(this);
    }

    @Override
    public void train(boolean interactive) {
        List<LanguageResponse> positiveTrainingResponses =
                FileUtils.parseLanguageResponsesFromFiles("parses/price_questions_training", mGson);
        List<LanguageResponse> negativeTrainingResponses = new ArrayList<>();

        getClassifier().train(positiveTrainingResponses, negativeTrainingResponses, interactive);
    }

    @Override
    public void test(boolean interactive) {
        List<LanguageResponse> positiveTestingResponses
                = FileUtils.parseLanguageResponsesFromFiles("parses/price_questions_training", mGson);
        List<LanguageResponse> negativeTestingResponses = new ArrayList<>();

        getClassifier().test(positiveTestingResponses, negativeTestingResponses, interactive);
    }

    @Override
    public Score score(boolean interactive) {
        return new Score(0, 0);
    }
}
