package price.node;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.BaseApp;
import classifier.Classifier;
import classifier.ClassifierNode;
import classifier.Score;
import models.LanguageResponse;
import utils.FileUtils;

public class PriceClassifierNode extends ClassifierNode<LanguageResponse> {

    @Inject Gson mGson;

    public PriceClassifierNode(Classifier<LanguageResponse> classifier, BaseApp app) {
        super(classifier, app);
        app.getAppComponent().inject(this);
    }

    @Override
    public void train(boolean interactive) {
        List<LanguageResponse> positiveTrainingResponses =
                FileUtils.parseLanguageResponsesFromFiles("parses/price_questions_training", mGson);
        List<LanguageResponse> negativeTrainingResponses = new ArrayList<>();

        getClassifier().train(positiveTrainingResponses, negativeTrainingResponses, true);
    }

    @Override
    public void test(boolean interactive) {

    }

    @Override
    public Score score(boolean interactive) {
        return new Score(0, 0);
    }
}
