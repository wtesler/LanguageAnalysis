package app;

import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;

import app.AppModule.ForQuestions;
import classifier.Score;
import models.LanguageResponse;
import cloud.CloudParser;
import questions.EnsembleQuestionClassifier;

public class App {

    @Inject @ForQuestions EnsembleQuestionClassifier mEnsembleQuestionClassifier;
    @Inject CloudParser mCloudParser;

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
                mCloudParser.parseDataFromFiles("parses/general_questions_training");
        List<LanguageResponse> negativeTrainingResponses =
                mCloudParser.parseDataFromFiles("parses/general_responses_training");

        // Train
        mEnsembleQuestionClassifier
                .train(positiveTrainingResponses, negativeTrainingResponses);

        List<LanguageResponse> positiveTestingResponses
                = mCloudParser.parseDataFromFiles("parses/general_questions_testing");
        List<LanguageResponse> negativeTestingResponses
                = mCloudParser.parseDataFromFiles("parses/general_responses_testing");

        // Test
        mEnsembleQuestionClassifier.test(positiveTestingResponses, negativeTestingResponses);

        List<LanguageResponse> responses = mCloudParser.parseDataFromFiles("parses/general_questions_master");
        Score score = mEnsembleQuestionClassifier.scoreObjects(responses);

        System.out.println("Accuracy: " + (double) score.correct / score.total);

        mCloudParser.getParsedSentenceObservable()
                .subscribe(parsedSentence -> {
                    LanguageResponse response = mCloudParser.convertParsedSentence(parsedSentence);
                    boolean decision = mEnsembleQuestionClassifier.classify(response, true);
                    System.out.println(decision ? "This is a question" : "This is a statement");
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
