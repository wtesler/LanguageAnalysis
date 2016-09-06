package cloud;

import com.squareup.okhttp.ResponseBody;

import models.GcsLanguageRequest;
import models.LanguageRequest;
import models.LanguageResponse;
import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

public interface LanguageService {

    @POST("v1beta1/documents:annotateText")
    Observable<LanguageResponse> parse(@Body LanguageRequest request);

    @POST("v1beta1/documents:annotateText")
    Observable<ResponseBody> rawParse(@Body LanguageRequest request);

    @POST("v1beta1/documents:annotateText")
    Observable<LanguageResponse> parse(@Body GcsLanguageRequest request);
}
