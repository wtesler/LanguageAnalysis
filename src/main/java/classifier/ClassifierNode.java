package classifier;

import java.util.ArrayList;

import app.BaseApp;

public abstract class ClassifierNode<T> {

    private final Classifier<T> mClassifier;

    private final ArrayList<ClassifierNode<T>> mChildren = new ArrayList<>();

    public ClassifierNode(Classifier<T> classifier, final BaseApp app) {
        mClassifier = classifier;
    }

    public abstract void train(boolean interactive);

    public abstract void test(boolean interactive);

    public abstract Score score(boolean interactive);

    public final void learn(boolean interactive) {
        train(interactive);
        mChildren.parallelStream().forEach(node -> node.train(interactive));

        test(interactive);
        mChildren.parallelStream().forEach(node -> node.test(interactive));
    }

    public final void classify(T model, boolean interactive) {
        mClassifier.classify(model, interactive);
        mChildren.stream().forEach(node -> node.classify(model, interactive));
    }

    public Score totalScore(final boolean interactive) {
        Score score = score(interactive);
        Score childrenScore =  mChildren.parallelStream()
                .map(classifierNode -> classifierNode.score(interactive))
                .collect(Score::new, Score::add, Score::add);
        score.add(childrenScore);
        return score;
    }

    public Classifier<T> getClassifier() {
        return mClassifier;
    }

    public void addChild(ClassifierNode classifierNode) {
        mChildren.add(classifierNode);
    }
}
