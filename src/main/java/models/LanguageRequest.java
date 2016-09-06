package models;

public class LanguageRequest {

    public LanguageDocument document;
    public Features features;
    public @EncodingType String encodingType;

    public @interface EncodingType {}
    public static final String UTF8 = "UTF8";
    public static final String UTF16 = "UTF16";
    public static final String UTF32 = "UTF32";
}
