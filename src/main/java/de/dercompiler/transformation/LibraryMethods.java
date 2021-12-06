package de.dercompiler.transformation;

import firm.Entity;
import firm.MethodType;
import firm.PointerType;
import firm.Type;

import java.util.HashMap;
import java.util.List;

public class LibraryMethods {

    public static final Entity print_int;
    public static final Entity print_byte;
    public static final Entity read_byte;
    public static final Entity flush_out;
    public static final Entity allocate;

    private static final HashMap<String, Entity> map;

    static {
        print_int = new Entity(null, "print_int", new MethodType(new Type[]{FirmTypes.intFirmType}, new Type[0]));
        print_byte = new Entity(null, "print_byte", new MethodType(new Type[]{FirmTypes.intFirmType}, new Type[]{}));
        read_byte = new Entity(null, "read_byte", new MethodType(new Type[]{}, new Type[]{FirmTypes.intFirmType}));
        flush_out = new Entity(null, "flush_out", new MethodType(new Type[]{}, new Type[]{}, false));

        allocate = new Entity(null, "allocate", new MethodType(new Type[]{new PointerType(FirmTypes.voidFirmType)}, new Type[]{FirmTypes.intFirmType}));

        map = new HashMap<>(5);
        List.of(print_int, print_byte, read_byte, flush_out, allocate).stream().forEach(entity -> map.put(entity.getName(), entity));
    }

    public static final Entity forName(String name) {
        return map.getOrDefault(name, null);
    }
}
