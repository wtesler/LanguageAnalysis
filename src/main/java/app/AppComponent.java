package app;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import bayes.Classifier;
import bayes.SyntaxReader;
import cloud.LanguageClient;
import dagger.Component;
import parser.CloudParser;
import retrofit.Retrofit;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(App app);

    void inject(CloudParser cloudParser);

    @AppModule.ForQuestions
    Classifier classifier();

    CloudParser cloudParser();

    Gson gson();

    LanguageClient languageClient();

    @AppModule.ForLanguage
    OkHttpClient okHttpClient();

    @AppModule.ForLanguage
    Retrofit languageRetrofit();

    SyntaxReader syntaxReader();
}

