package de.dercompiler.io.message;

import com.diogonunes.jcolor.Attribute;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ColorizationHelper {

    /**
     * removes Attributes that are null
     *
     * @param attributes the attributes
     * @return the non-null attributes
     */
    public static Attribute[] removeInvalid(Attribute... attributes) {
        List<Attribute> valid = new LinkedList<>();
        for (Attribute x : attributes) {
            if (!Objects.isNull(x)) valid.add(x);
        }

        return valid.toArray(new Attribute[0]);
    }
}
