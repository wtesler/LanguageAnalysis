package microphone;

import java.util.Base64;
import java.util.Scanner;

import javax.inject.Inject;
import javax.sound.sampled.AudioFormat;

import app.BaseApp;
import cloud.CloudParser;
import cloud.SpeechClient;
import gnu.trove.list.array.TByteArrayList;
import models.language.LanguageResponse;
import models.speech.Audio;
import models.speech.RecognitionConfig;
import models.speech.SpeechRequest;
import price_discovery.ensemble.PriceDiscoveryEnsembleClassifier;
import price_discovery.node.PriceVisualClassifierNode;
import question.ensemble.QuestionEnsembleClassifier;
import question.node.QuestionClassifierNode;
import question_price.ensemble.PriceEnsembleClassifier;
import question_price.node.PriceClassifierNode;
import rx.Subscription;

public class SpeechApp extends BaseApp {

    @Inject CloudParser mCloudParser;
    @Inject SpeechClient mSpeechClient;
    @Inject PriceEnsembleClassifier mPriceEnsembleClassifier;
    @Inject PriceDiscoveryEnsembleClassifier mPriceDiscoveryEnsembleClassifier;
    @Inject QuestionEnsembleClassifier mQuestionEnsembleClassifier;

    public static void main(String[] args) {
        new SpeechApp();
    }

    public SpeechApp() {
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

        mCloudParser.getParsedSentenceObservable().subscribe(parsedSentence -> {
            LanguageResponse response = mCloudParser.convertParsedSentence(parsedSentence);
            questionClassifierNode.classify(response, true);
        });

        // Begin microphone procedure.
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);

        Scanner in = new Scanner(System.in);

        while (true) {
            Microphone mic = new Microphone(format);
            TByteArrayList recordedData = new TByteArrayList();
            Subscription micSubscription =  mic.getRecordObservable().subscribe(recordedData::add);

            boolean isRecorded = false;
            boolean isRecording = false;
            while (!isRecorded) {
                String line = in.nextLine();
                if (line.isEmpty()) {
                    if (isRecording) {
                        mic.interrupt();
                        isRecorded = true;
                    } else {
                        mic.start();
                        isRecording = true;
                    }
                }
            }

            isRecorded = false;
            isRecording = false;
            micSubscription.unsubscribe();

            RecognitionConfig config = new RecognitionConfig();
            config.encoding = RecognitionConfig.LINEAR16;
            config.sampleRate = (int) mic.getAudioFormat().getSampleRate();
            config.maxAlternatives = 2;

            Audio audio = new Audio();
            audio.content = Base64.getEncoder().encodeToString(recordedData.toArray());

            SpeechRequest request = new SpeechRequest();
            request.config = config;
            request.audio = audio;

            mSpeechClient.analyze(request)
                    .subscribe(speechResponse -> {
                        String transcript = speechResponse.results.get(0).alternatives.get(0).transcript;
                        mCloudParser.parseSentence(transcript);
                    });
        }
    }
}