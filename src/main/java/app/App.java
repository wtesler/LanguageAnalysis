package app;

import com.google.gson.Gson;

import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;

import app.AppModule.ForQuestions;
import classifier.Score;
import models.LanguageResponse;
import cloud.CloudParser;
import questions.EnsembleQuestionClassifier;
import utils.FileUtils;

public class App {

    @Inject CloudParser mCloudParser;
    @Inject @ForQuestions EnsembleQuestionClassifier mEnsembleQuestionClassifier;
    @Inject Gson mGson;

    private final AppComponent mAppComponent;

    public static void main(String[] args) {
        new App();
    }

    public App() {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        mAppComponent.inject(this);

        List<LanguageResponse> positiveTrainingResponses =
                FileUtils.parseLanguageResponsesFromFiles("parses/general_questions_training", mGson);
        List<LanguageResponse> negativeTrainingResponses =
                FileUtils.parseLanguageResponsesFromFiles("parses/general_responses_training", mGson);

        // Train
        mEnsembleQuestionClassifier
                .train(positiveTrainingResponses, negativeTrainingResponses);

        List<LanguageResponse> positiveTestingResponses
                = FileUtils.parseLanguageResponsesFromFiles("parses/general_questions_testing", mGson);
        List<LanguageResponse> negativeTestingResponses
                = FileUtils.parseLanguageResponsesFromFiles("parses/general_responses_testing", mGson);

        // Test
        mEnsembleQuestionClassifier.test(positiveTestingResponses, negativeTestingResponses);

        List<LanguageResponse> questions
                = FileUtils.parseLanguageResponsesFromFiles("parses/general_questions_master", mGson);

        List<LanguageResponse> answers
                = FileUtils.parseLanguageResponsesFromFiles("parses/general_responses_master", mGson);

        Score score1 = mEnsembleQuestionClassifier.scoreObjects(questions);
        Score score2 = mEnsembleQuestionClassifier.scoreObjects(answers);

        System.out.println("Accuracy: "
                + (double) (score1.correct + (score2.total - score2.correct))
                / (score1.total + score2.total));

        mCloudParser.getParsedSentenceObservable()
                .subscribe(parsedSentence -> {
                    LanguageResponse response = mCloudParser.convertParsedSentence(parsedSentence);
                    double decision = mEnsembleQuestionClassifier.classify(response, true);
                    System.out.println(decision > 0 ? "This is a question" : "This is a statement");
                });

        while (true) {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (!line.isEmpty()) {
                mCloudParser.parseSentence(line);
            }
        }
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
