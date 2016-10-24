package cloud;

import models.speech.GcsSpeechRequest;
import models.speech.SpeechRequest;
import models.speech.SpeechResponse;
import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

public interface SpeechService {

    @POST("v1beta1/speech:syncrecognize")
    Observable<SpeechResponse> getSpeechResults(@Body GcsSpeechRequest request);

    @POST("v1beta1/speech:syncrecognize")
    Observable<SpeechResponse> getSpeechResults(@Body SpeechRequest request);
}