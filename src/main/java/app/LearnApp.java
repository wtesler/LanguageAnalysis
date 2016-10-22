package app;

import com.google.gson.Gson;

import java.util.Scanner;

import javax.inject.Inject;

import cloud.CloudParser;
import models.LanguageResponse;
import price.ensemble.EnsemblePriceClassifier;
import price.node.PriceClassifierNode;
import questions.ensemble.EnsembleQuestionClassifier;
import questions.node.QuestionClassifierNode;

public class LearnApp extends BaseApp {

    @Inject CloudParser mCloudParser;
    @Inject EnsemblePriceClassifier mEnsemblePriceClassifier;
    @Inject EnsembleQuestionClassifier mEnsembleQuestionClassifier;
    @Inject Gson mGson;

    public static void main(String[] args) {
        new LearnApp();
    }

    public LearnApp() {
        super();

        getAppComponent().inject(this);

        QuestionClassifierNode questionClassifierNode = new QuestionClassifierNode(mEnsembleQuestionClassifier, this);
        questionClassifierNode.learn();

        PriceClassifierNode priceClassifierNode = new PriceClassifierNode(mEnsemblePriceClassifier, this);
        priceClassifierNode.learn();

        mCloudParser.getParsedSentenceObservable()
                .subscribe(parsedSentence -> {
                    LanguageResponse response = mCloudParser.convertParsedSentence(parsedSentence);
                    double decision = mEnsembleQuestionClassifier.classify(response, true);
                    boolean isQuestion = decision > 0;
                    if (isQuestion) {
                        decision = mEnsemblePriceClassifier.classify(response, true);
                        boolean isPrice = decision > 0;
                        if (isPrice) {
                            System.out.println("This is a question about price");
                        } else {
                            System.out.println("This is a question");
                        }
                    } else {
                        System.out.println("This is a statement");
                    }
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
