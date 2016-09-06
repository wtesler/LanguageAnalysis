package models;

import java.util.List;
import java.util.Map;

public class Entity {

    public String name;
    public @EntityType String type;
    public Map<String, String> metadata;
    public int salience;
    public List<EntityMention> mentions;

    public @interface EntityType {}
    public static final String UNKNOWN = "UNKNOWN";
    public static final String PERSON = "PERSON";
    public static final String LOCATION = "LOCATION";
    public static final String ORGANIZATION = "ORGANIZATION";
    public static final String EVENT = "EVENT";
    public static final String WORK_OF_ART = "WORK_OF_ART";
    public static final String CONSUMER_GOOD = "CONSUMER_GOOD";
    public static final String OTHER = "OTHER";
}
