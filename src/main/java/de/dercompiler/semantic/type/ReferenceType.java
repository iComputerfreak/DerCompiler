package de.dercompiler.semantic.type;

public sealed interface ReferenceType extends Type permits ArrayType, ClassType {
}
