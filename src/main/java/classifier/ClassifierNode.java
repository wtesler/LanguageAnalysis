package classifier;

import app.BaseApp;

public abstract class ClassifierNode<T> {

    Classifier<T> mClassifier;

    public ClassifierNode(Classifier<T> classifier, final BaseApp app) {
        mClassifier = classifier;
    }

    public abstract void train();

    public abstract void test();

    public abstract void score();

    public final void learn() {
        train();
        test();
        score();
    }

    public Classifier<T> getClassifier() {
        return mClassifier;
    }
}
