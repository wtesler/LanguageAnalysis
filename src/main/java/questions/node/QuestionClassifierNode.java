package questions.node;

import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

import app.BaseApp;
import classifier.Classifier;
import classifier.ClassifierNode;
import classifier.Score;
import models.LanguageResponse;
import utils.FileUtils;

public class QuestionClassifierNode extends ClassifierNode<LanguageResponse> {

    @Inject Gson mGson;

    public QuestionClassifierNode(Classifier<LanguageResponse, ?> classifier, BaseApp app) {
        super(classifier, app);
        app.getAppComponent().inject(this);
    }

    @Override
    public void train(boolean interactive) {
        List<LanguageResponse> positiveTrainingResponses =
                FileUtils.parseLanguageResponsesFromFiles("parses/general_questions_training", mGson);
        List<LanguageResponse> negativeTrainingResponses =
                FileUtils.parseLanguageResponsesFromFiles("parses/general_responses_training", mGson);

        getClassifier().train(positiveTrainingResponses, negativeTrainingResponses, true);
    }

    @Override
    public void test(boolean interactive) {
        List<LanguageResponse> positiveTestingResponses
                = FileUtils.parseLanguageResponsesFromFiles("parses/general_questions_testing", mGson);
        List<LanguageResponse> negativeTestingResponses
                = FileUtils.parseLanguageResponsesFromFiles("parses/general_responses_testing", mGson);

        getClassifier().test(positiveTestingResponses, negativeTestingResponses, true);
    }

    @Override
    public Score score(boolean interactive) {
        List<LanguageResponse> questions
                = FileUtils.parseLanguageResponsesFromFiles("parses/general_questions_master", mGson);

        List<LanguageResponse> answers
                = FileUtils.parseLanguageResponsesFromFiles("parses/general_responses_master", mGson);

        Score score1 = getClassifier().score(questions, true);
        Score score2 = getClassifier().score(answers, true);
        score2.correct = score2.total - score2.correct;

        return new Score(score1.correct + score2.correct, score1.total + score2.total);
    }
}
