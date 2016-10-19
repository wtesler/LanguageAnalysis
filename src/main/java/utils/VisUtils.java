package utils;

public class VisUtils {

    private VisUtils() { }

    public static String toGauge(double value) {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 20; i > 0; i--) {
            if (Math.abs((-(i / 20.0)) - value) < .025) {
                builder.append('|');
            } else {
                builder.append(' ');
            }
        }

        if (Math.abs(value) < .025) {
            builder.append('|');
        } else {
            builder.append(':');
        }

        for (int i = 1; i <= 20; i++) {
            if (Math.abs((i / 20.0) - value) < .025) {
                builder.append('|');
            } else {
                builder.append(' ');
            }
        }
        builder.append(']');
        return builder.toString();
    }
}
