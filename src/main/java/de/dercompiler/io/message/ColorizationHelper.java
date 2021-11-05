package de.dercompiler.io.message;

import com.diogonunes.jcolor.Attribute;

import java.util.Arrays;
import java.util.Objects;

public class ColorizationHelper {

    /**
     * Removes invalid attributes from the given list
     *
     * @param attributes The given attributes
     * @return All given attributes that are not null
     */
    public static Attribute[] removeInvalid(Attribute... attributes) {
        // Delete all null attributes
        return Arrays.stream(attributes).filter(Objects::nonNull).toArray(Attribute[]::new);
    }
}
