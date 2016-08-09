package bayes;

public class QuestionClassifier extends Classifier {

    private final SyntaxReader mSyntaxReader;
    private final SiblingClassifier mSiblingClassifier;

    public QuestionClassifier(SyntaxReader syntaxReader) {
        mSyntaxReader = syntaxReader;
        mSiblingClassifier = new SiblingClassifier(mSyntaxReader);
    }

    @Override
    public void train(String dirClassA, String dirClassB) {
        mSiblingClassifier.train(dirClassA, dirClassB);
        addValues(mSiblingClassifier.getValues());
    }

    @Override
    public void classifyDirectory(String dir) {
        mSiblingClassifier.classifyDirectory(dir);
    }
}
