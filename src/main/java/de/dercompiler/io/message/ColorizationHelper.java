package de.dercompiler.io.message;

import com.diogonunes.jcolor.Attribute;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ColorizationHelper {

    public static Attribute[] removeInvalid(Attribute... attributes) {
        List<Attribute> valid = new LinkedList<>();
        for (Attribute x : attributes) {
            if (!Objects.isNull(x)) valid.add(x);
        }

        return valid.toArray(new Attribute[0]);
    }
}
