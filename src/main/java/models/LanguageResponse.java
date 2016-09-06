package models;

import java.util.List;

public class LanguageResponse {

    public List<Sentence> sentences;
    public List<Token> tokens;
    public List<Entity> entities;
    public Sentiment documentSentiment;
    public @LanguageDocument.Iso String language;
}
