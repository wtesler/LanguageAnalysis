package app;

import java.util.List;

import javax.inject.Inject;

import app.AppModule.ForQuestions;
import classifier.Score;
import models.LanguageResponse;
import questions.EnsembleQuestionClassifier;
import questions.SyntaxReader;
import parser.CloudParser;

public class App {

    @Inject @ForQuestions EnsembleQuestionClassifier mEnsembleQuestionClassifier;
    @Inject CloudParser mCloudParser;
    @Inject SyntaxReader mSyntaxReader;

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
                = mSyntaxReader.readParsedDataFromFiles("parses/general_questions_testing");
        List<LanguageResponse> negativeResponses
                = mSyntaxReader.readParsedDataFromFiles("parses/general_responses_testing");

        mEnsembleQuestionClassifier.test(positiveResponses, negativeResponses);

        List<LanguageResponse> responses = mSyntaxReader.readParsedDataFromFiles("parses/general_questions_master");

        Score score = mEnsembleQuestionClassifier.scoreObjects(responses);
        System.out.println("Accuracy: " + (double) score.correct / score.total);

//        mCloudParser.getParsedSentenceObservable()
//                .subscribe(parsedSentence -> {
//                    LanguageResponse response = mSyntaxReader.convertParsedSentence(parsedSentence);
//                    boolean decision = mEnsembleQuestionClassifier.classify(response);
//                    System.out.println(decision ? "This is a question" : "This is a statement");
//                });
//
//        mCloudParser.parseSentence("I want a refund");
//        mCloudParser.parseSentence("Two burgers");
//        mCloudParser.parseSentence("A number 4 with a coke.");
//        mCloudParser.parseSentence("Put mustard on it.");

        while (true) {
            continue;
        }
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
