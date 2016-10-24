package models.language;

public class GcsLanguageRequest {

    public GcsLanguageDocument document;
    public Features features;
    public @LanguageRequest.EncodingType String encodingType;
}
