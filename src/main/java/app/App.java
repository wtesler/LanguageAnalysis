package app;

import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;

import app.AppModule.ForQuestions;
import classifier.Score;
import models.LanguageResponse;
import parser.CloudParser;
import questions.EnsembleQuestionClassifier;
import questions.SyntaxStructurer;

public class App {

    @Inject @ForQuestions EnsembleQuestionClassifier mEnsembleQuestionClassifier;
    @Inject CloudParser mCloudParser;
    @Inject
    SyntaxStructurer mSyntaxStructurer;

    private final AppComponent mAppComponent;

    public static void main(String[] args) {
        new App();
    }

    public App() {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        mAppComponent.inject(this);

        mEnsembleQuestionClassifier
                .train("parses/general_questions_training", "parses/general_responses_training");

        List<LanguageResponse> positiveResponses
                = mSyntaxStructurer.readParsedDataFromFiles("parses/general_questions_testing");
        List<LanguageResponse> negativeResponses
                = mSyntaxStructurer.readParsedDataFromFiles("parses/general_responses_testing");

        mEnsembleQuestionClassifier.test(positiveResponses, negativeResponses);

        List<LanguageResponse> responses = mSyntaxStructurer.readParsedDataFromFiles("parses/general_questions_master");
        Score score = mEnsembleQuestionClassifier.scoreObjects(responses);

        System.out.println("Accuracy: " + (double) score.correct / score.total);

        mCloudParser.getParsedSentenceObservable()
                .subscribe(parsedSentence -> {
                    LanguageResponse response = mSyntaxStructurer.convertParsedSentence(parsedSentence);
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
