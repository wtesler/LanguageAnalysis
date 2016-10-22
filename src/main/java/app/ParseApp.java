package app;

import javax.inject.Inject;

import cloud.CloudParser;

public class ParseApp extends BaseApp {

    @Inject CloudParser mCloudParser;

    public static void main(String[] args) {
        new ParseApp();
    }

    public ParseApp() {
        super();

        getAppComponent().inject(this);

        mCloudParser.parseSentencesResource("price_questions_training.txt", "parses/price_questions_training");
    }
}
