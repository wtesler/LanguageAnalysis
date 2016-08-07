package app;

import javax.inject.Inject;

import bayes.Classifier;

public class App {

    @Inject @AppModule.ForQuestions Classifier mClassifier;

    private final AppComponent mAppComponent;

    public static void main(String[] args) {
        new App();
    }

    public App() {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        mAppComponent.inject(this);

        mClassifier.classify();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
