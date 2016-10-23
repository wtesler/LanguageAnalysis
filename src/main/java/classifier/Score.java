package classifier;

public class Score {
    public int correct;
    public int total;

    public Score() {
        this(0, 0);
    }

    public Score(int correct, int total) {
        this.correct = correct;
        this.total = total;
    }

    public void add(Score other) {
        this.correct += other.correct;
        this.total += other.total;
    }
}