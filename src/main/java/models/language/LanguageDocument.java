package models.language;

public class LanguageDocument {

    public @TextType String type;
    public @Iso String language;
    public String content;

    public @interface TextType {}
    public static final String TYPE_UNSPECIFIED = "TYPE_UNSPECIFIED";
    public static final String PLAIN_TEXT = "PLAIN_TEXT";
    public static final String HTML = "HTML";

    public @interface Iso {}
    public static final String ENGLISH = "en";
    public static final String SPANISH = "es";
    public static final String JAPANESE = "ja";
}
