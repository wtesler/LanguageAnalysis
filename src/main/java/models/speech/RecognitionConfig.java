package models.speech;

public class RecognitionConfig {

    public @AudioEncoding String encoding;
    public int sampleRate;
    public @LanguageCode String languageCode;
    public int maxAlternatives;
    public boolean profanityFilter;
    public SpeechContext speechContext;

    public @interface AudioEncoding {}
    public static final String ENCODING_UNSPECIFIED = "ENCODING_UNSPECIFIED";
    public static final String LINEAR16 = "LINEAR16";
    public static final String FLAC = "FLAC";
    public static final String MULAW = "MULAW";
    public static final String AMR = "AMR";
    public static final String AMR_WB = "AMR_WB";

    public @interface LanguageCode {}
    public static final String ENGLISH = "en-US";
}
