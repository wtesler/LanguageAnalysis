package classifier;

import java.util.ArrayList;

import app.BaseApp;

public abstract class ClassifierNode<T> {

    private final Classifier<T, ?> mClassifier;

    private final ArrayList<ClassifierNode<T>> mPositiveChildren = new ArrayList<>();
    private final ArrayList<ClassifierNode<T>> mNegativeChildren = new ArrayList<>();

    public ClassifierNode(Classifier<T, ?> classifier, final BaseApp app) {
        mClassifier = classifier;
    }

    public abstract void train(boolean interactive);

    public abstract void test(boolean interactive);

    public abstract Score score(boolean interactive);

    public final void learn(boolean interactive) {
        train(interactive);
        test(interactive);
        mPositiveChildren.stream().forEach(node -> node.learn(interactive));
        mNegativeChildren.stream().forEach(node -> node.learn(interactive));
    }

    public final void classify(T model, boolean interactive) {
        double classification = mClassifier.classify(model, interactive);
        if (classification > 0) {
            mPositiveChildren.stream().forEach(node -> node.classify(model, interactive));

        } else {
            mNegativeChildren.stream().forEach(node -> node.classify(model, interactive));
        }
    }

    public Score totalScore(final boolean interactive) {
        Score score = score(interactive);
        Score positiveChildrenScore =  mPositiveChildren.stream()
                .map(classifierNode -> classifierNode.score(interactive))
                .collect(Score::new, Score::add, Score::add);
        Score negativeChildrenScore =  mNegativeChildren.stream()
                .map(classifierNode -> classifierNode.score(interactive))
                .collect(Score::new, Score::add, Score::add);
        score.add(positiveChildrenScore);
        score.add(negativeChildrenScore);
        return score;
    }

    public Classifier<T, ?> getClassifier() {
        return mClassifier;
    }

    public void addChild(ClassifierNode<T> classifierNode, boolean positive) {
        if (positive) {
            mPositiveChildren.add(classifierNode);
        } else {
            mNegativeChildren.add(classifierNode);
        }
    }
}
