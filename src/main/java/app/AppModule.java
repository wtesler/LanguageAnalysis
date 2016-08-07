package app;

import com.google.gson.Gson;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;
import javax.inject.Singleton;

import bayes.Classifier;
import bayes.SyntaxReader;
import cloud.LanguageClient;
import cloud.LanguageService;
import dagger.Module;
import dagger.Provides;
import parser.CloudParser;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import security.GoogleAuth;

import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Module
public class AppModule {

    App mApp;

    public AppModule(App app) {
        mApp = app;
    }

    @Provides
    @ForQuestions
    @Singleton
    Classifier provideClassifier(SyntaxReader syntaxReader) {
        return new Classifier(syntaxReader);
    }

    @Provides
    @Singleton
    CloudParser provideCloudParser(LanguageClient languageClient) {
        return new CloudParser(mApp, languageClient);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    LanguageClient provideLanguageClient(@ForLanguage Retrofit retrofit) {
        LanguageService languageService = retrofit.create(LanguageService.class);
        return new LanguageClient(languageService);
    }

    @Provides
    @ForLanguage
    @Singleton
    OkHttpClient provideLanguageOkHttpClient() {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.interceptors().add(chain -> {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.httpUrl();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("key", GoogleAuth.BROWSER_KEY)
                    .build();

            Request.Builder requestBuilder = original.newBuilder()
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.interceptors().add(interceptor);

        return httpClient;
    }

    @Provides
    @ForLanguage
    @Singleton
    Retrofit provideLanguageRetrofit(@ForLanguage OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl("https://language.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    SyntaxReader provideSyntaxReader(Gson gson) {
        return new SyntaxReader(gson);
    }

    @Qualifier
    @Documented
    @Retention(RUNTIME)
    public @interface ForLanguage { }

    @Qualifier
    @Documented
    @Retention(RUNTIME)
    public @interface ForQuestions { }
}
