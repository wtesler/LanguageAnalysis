package cloud;

import com.squareup.okhttp.ResponseBody;

import models.GcsLanguageRequest;
import models.LanguageRequest;
import models.LanguageResponse;
import rx.Observable;
import utils.RxUtils;

public class LanguageClient {

    private final LanguageService mLanguageService;

    public LanguageClient(LanguageService languageService) {
        mLanguageService = languageService;
    }

    public Observable<LanguageResponse> parse(LanguageRequest request) {
        return mLanguageService.parse(request)
                .compose(RxUtils.<LanguageResponse>retrofitTransformer());
    }

    public Observable<LanguageResponse> parse(GcsLanguageRequest request) {
        return mLanguageService.parse(request)
                .compose(RxUtils.<LanguageResponse>retrofitTransformer());
    }

    public Observable<ResponseBody> rawParse(LanguageRequest request) {
        return mLanguageService.rawParse(request)
                .compose(RxUtils.<ResponseBody>retrofitTransformer());
    }
}
