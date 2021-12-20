package de.dercompiler.intermediate.selection;

import firm.nodes.Node;

/**
 * Represents an annotation of a {@link Node}
 */
public record NodeAnnotation(int cost, Node rootNode, SubstitutionRule rule, boolean visited) {}
