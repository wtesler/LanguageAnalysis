package app;

import java.util.Scanner;

import javax.inject.Inject;

import classifier.Score;
import cloud.CloudParser;
import models.language.LanguageResponse;
import price_discovery.ensemble.PriceDiscoveryEnsembleClassifier;
import price_discovery.node.PriceVisualClassifierNode;
import question.ensemble.QuestionEnsembleClassifier;
import question.node.QuestionClassifierNode;
import question_price.ensemble.PriceEnsembleClassifier;
import question_price.node.PriceClassifierNode;

public class LearnApp extends BaseApp {

    @Inject CloudParser mCloudParser;
    @Inject PriceEnsembleClassifier mPriceEnsembleClassifier;
    @Inject PriceDiscoveryEnsembleClassifier mPriceDiscoveryEnsembleClassifier;
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
        questionClassifierNode.addChild(priceClassifierNode, true);

        PriceVisualClassifierNode priceVisualClassifierNode
                = new PriceVisualClassifierNode(mPriceDiscoveryEnsembleClassifier, this);
        priceClassifierNode.addChild(priceVisualClassifierNode, true);

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
