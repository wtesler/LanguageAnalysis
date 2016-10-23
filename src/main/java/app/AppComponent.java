package app;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import cloud.CloudParser;
import cloud.LanguageClient;
import dagger.Component;
import price_discovery.ensemble.PriceDiscoveryEnsembleClassifier;
import question_price.ensemble.PriceEnsembleClassifier;
import question_price.node.PriceClassifierNode;
import price_discovery.node.PriceVisualClassifierNode;
import question.ensemble.QuestionEnsembleClassifier;
import question.node.QuestionClassifierNode;
import retrofit.Retrofit;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(LearnApp app);
    void inject(ParseApp app);
    void inject(PriceClassifierNode priceClassifierNode);
    void inject(PriceVisualClassifierNode priceClassifierNode);
    void inject(QuestionClassifierNode questionClassifierNode);

    PriceEnsembleClassifier priceClassifier();

    PriceDiscoveryEnsembleClassifier priceVisualClassifier();

    QuestionEnsembleClassifier questionClassifier();

    CloudParser cloudParser();

    Gson gson();

    LanguageClient languageClient();

    @AppModule.ForLanguage
    OkHttpClient okHttpClient();

    @AppModule.ForLanguage
    Retrofit languageRetrofit();
}

