package app;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import cloud.CloudParser;
import cloud.LanguageClient;
import dagger.Component;
import price.ensemble.PriceEnsembleClassifier;
import price.node.PriceClassifierNode;
import questions.ensemble.QuestionEnsembleClassifier;
import questions.node.QuestionClassifierNode;
import retrofit.Retrofit;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(LearnApp app);
    void inject(ParseApp app);
    void inject(PriceClassifierNode priceClassifierNode);
    void inject(QuestionClassifierNode questionClassifierNode);

    PriceEnsembleClassifier priceClassifier();

    QuestionEnsembleClassifier questionClassifier();

    CloudParser cloudParser();

    Gson gson();

    LanguageClient languageClient();

    @AppModule.ForLanguage
    OkHttpClient okHttpClient();

    @AppModule.ForLanguage
    Retrofit languageRetrofit();
}

