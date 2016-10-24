package cloud;

import models.speech.GcsSpeechRequest;
import models.speech.SpeechRequest;
import models.speech.SpeechResponse;
import rx.Observable;
import utils.RxUtils;

public class SpeechClient {

    SpeechService mSpeechService;

    public SpeechClient(SpeechService service) {
        mSpeechService = service;
    }

    public Observable<SpeechResponse> analyze(GcsSpeechRequest request) {
        return mSpeechService.getSpeechResults(request)
                .compose(RxUtils.<SpeechResponse>retrofitTransformer());
    }

    public Observable<SpeechResponse> analyze(SpeechRequest request) {
        return mSpeechService.getSpeechResults(request)
                .compose(RxUtils.<SpeechResponse>retrofitTransformer());
    }
}
