package net.casim.ml.mm.data;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ModelLayer {
    TEXT_CLASSIFIER,
    VISUAL_CLASSIFIER,
    OPTICAL_RECOGNIZER;

    @JsonCreator
    public static ModelLayer fromString(String value) {
        try {
            return ModelLayer.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
    }


}

