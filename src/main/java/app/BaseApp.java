package app;

public abstract class BaseApp {

    private final AppComponent mAppComponent;

    public BaseApp() {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public final AppComponent getAppComponent() {
        return mAppComponent;
    }
}
