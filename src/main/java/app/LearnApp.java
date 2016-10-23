package app;

import com.google.gson.Gson;

import java.util.Scanner;

import javax.inject.Inject;

import classifier.Score;
import cloud.CloudParser;
import models.LanguageResponse;
import price.ensemble.PriceEnsembleClassifier;
import price.node.PriceClassifierNode;
import questions.ensemble.QuestionEnsembleClassifier;
import questions.node.QuestionClassifierNode;

public class LearnApp extends BaseApp {

    @Inject CloudParser mCloudParser;
    @Inject Gson mGson;
    @Inject PriceEnsembleClassifier mPriceEnsembleClassifier;
    @Inject QuestionEnsembleClassifier mQuestionEnsembleClassifier;

    public static void main(String[] args) {
        new LearnApp();
    }

    public LearnApp() {
        super();
        getAppComponent().inject(this);

        // Create Classifier Tree.
        QuestionClassifierNode questionClassifierNode = new QuestionClassifierNode(mQuestionEnsembleClassifier, this);

        PriceClassifierNode priceClassifierNode = new PriceClassifierNode(mPriceEnsembleClassifier, this);
        questionClassifierNode.addChild(priceClassifierNode);

        // Tell the classifiers to learn.
        questionClassifierNode.learn(true);

        // Check how well the classifiers have done.
        Score score = questionClassifierNode.totalScore(true);
        System.out.println("Accuracy: " + (double) score.correct / score.total);

        mCloudParser.getParsedSentenceObservable()
                .subscribe(parsedSentence -> {
                    LanguageResponse response = mCloudParser.convertParsedSentence(parsedSentence);
                    questionClassifierNode.classify(response, true);
                });

        while (true) {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (!line.isEmpty()) {
                mCloudParser.parseSentence(line);
            }
        }
    }
}
