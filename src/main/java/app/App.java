package app;

import javax.inject.Inject;

import app.AppModule.ForQuestions;
import bayes.QuestionClassifier;
import parser.CloudParser;

public class App {

    @Inject @ForQuestions
    QuestionClassifier mQuestionClassifier;
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

        mQuestionClassifier
                .train("parses/general_questions_training", "parses/general_responses_training");

//        mCloudParser.getParsedSentenceObservable()
//                .subscribe(parsedSentence -> {
//                    double score = mQuestionClassifier.classifySentence(parsedSentence, siblingScoreMap);
//                    System.out.println(score);
//                });
//
//        mCloudParser.parseSentence("What did he take");
//
//        while (true) {
//            continue;
//        }

        mQuestionClassifier.classifyDirectory("parses/general_questions_testing");
        //mQuestionClassifier.classifyDirectory("parses/general_responses_testing");
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
