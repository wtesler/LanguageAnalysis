package app;

import javax.inject.Inject;

import app.AppModule.ForQuestions;
import classifier.Classification;
import questions.EnsembleQuestionClassifier;
import questions.SyntaxReader;
import models.LanguageResponse;
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

        mEnsembleQuestionClassifier.test("parses/general_questions_testing", "parses/general_responses_testing");

        Classification classification = mEnsembleQuestionClassifier.classifyDirectory
                ("parses/general_questions_master");
        System.out.println("Accuracy: " + (double) classification.numPositive / classification.total);

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
