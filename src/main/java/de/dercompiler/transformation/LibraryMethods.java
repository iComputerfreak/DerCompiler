package de.dercompiler.transformation;

import firm.*;

import java.util.HashMap;

public class LibraryMethods {

    private static final CompoundType OWNER;
    public static final Entity print_int;
    public static final Entity print_byte;
    public static final Entity read_int;
    public static final Entity flush_out;
    public static final Entity allocate;

    private static final HashMap<String, Entity> map;

    static {
        OWNER = new firm.ClassType("Internals");

        print_int = new Entity(OWNER, "print_int", new MethodType(new Type[]{FirmTypes.intFirmType}, new Type[]{}));
        print_byte = new Entity(OWNER, "print_byte", new MethodType(new Type[]{FirmTypes.intFirmType}, new Type[]{}));
        read_int = new Entity(OWNER, "read_int", new MethodType(new Type[]{}, new Type[]{FirmTypes.intFirmType}));
        flush_out = new Entity(OWNER, "flush_out", new MethodType(new Type[]{}, new Type[]{}));

        allocate = new Entity(OWNER, "allocate", new MethodType(new Type[]{FirmTypes.longFirmType, FirmTypes.longFirmType}, new Type[]{FirmTypes.pointerFirmType}));

        map = new HashMap<>(5);
        map.put("println", print_int);
        map.put("write",  print_byte);
        map.put("read", read_int);
        map.put("flush", flush_out);
        map.put("allocate", allocate);
    }

    public static final Entity forName(String name) {
        return map.getOrDefault(name, null);
    }
}
