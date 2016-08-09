package bayes;

import java.util.HashMap;

public abstract class Classifier {

    private final HashMap<String, Double> mValueMap = new HashMap<>();

    public Classifier() { }

    public abstract void train(String dirClassA, String dirClassB);

    public abstract void classifyDirectory(String dir);

    protected Double getValue(String key) {
        return mValueMap.get(key);
    }

    protected HashMap<String, Double> getValues() {
        return mValueMap;
    }

    protected void setValue(String key, Double value) {
        mValueMap.put(key, value);
    }

    protected void addValues(HashMap<String, Double> values) {
        mValueMap.putAll(values);
    }
}
