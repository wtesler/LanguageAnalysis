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

    public QuestionClassifierNode(Classifier<LanguageResponse> classifier, BaseApp app) {
        super(classifier, app);
        app.getAppComponent().inject(this);
    }

    @Override
    public void train() {
        List<LanguageResponse> positiveTrainingResponses =
                FileUtils.parseLanguageResponsesFromFiles("parses/general_questions_training", mGson);
        List<LanguageResponse> negativeTrainingResponses =
                FileUtils.parseLanguageResponsesFromFiles("parses/general_responses_training", mGson);

        getClassifier().train(positiveTrainingResponses, negativeTrainingResponses);
    }

    @Override
    public void test() {
        List<LanguageResponse> positiveTestingResponses
                = FileUtils.parseLanguageResponsesFromFiles("parses/general_questions_testing", mGson);
        List<LanguageResponse> negativeTestingResponses
                = FileUtils.parseLanguageResponsesFromFiles("parses/general_responses_testing", mGson);

        getClassifier().test(positiveTestingResponses, negativeTestingResponses);
    }

    @Override
    public void score() {
        List<LanguageResponse> questions
                = FileUtils.parseLanguageResponsesFromFiles("parses/general_questions_master", mGson);

        List<LanguageResponse> answers
                = FileUtils.parseLanguageResponsesFromFiles("parses/general_responses_master", mGson);

        Score score1 = getClassifier().score(questions);
        Score score2 = getClassifier().score(answers);
        score2.correct = score2.total - score2.correct;
        System.out.println("Accuracy: " + (double) (score1.correct + score2.correct) / (score1.total + score2.total));
    }
}
