package models.language;

public class PartOfSpeech {

    public @Tag String tag;

    public @interface Tag {}
    public static final String UNKNOWN = "UNKNOWN";
    public static final String ADJECTIVE = "ADJ";
    public static final String ADPOSITION = "ADP";
    public static final String ADVERB = "ADV";
    public static final String CONJUNCTION = "CONJ";
    public static final String DETERMINER = "DET";
    public static final String NOUN = "NOUN";
    public static final String NUM = "NUM";
    public static final String PRONOUN = "PRON";
    public static final String PARTICLE = "PRT";
    public static final String PUNCTUATION = "PUNCT";
    public static final String VERB = "VERB";
    public static final String OTHER = "X";
    public static final String AFFIX = "AFFIX";
}


