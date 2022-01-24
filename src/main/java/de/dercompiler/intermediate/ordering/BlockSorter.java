package de.dercompiler.intermediate.ordering;

import de.dercompiler.intermediate.selection.BasicBlockGraph;
import de.dercompiler.intermediate.selection.FirmBlock;
import firm.Graph;

import java.util.List;

public interface BlockSorter {
    List<FirmBlock> sortBlocks(BasicBlockGraph graph);
}
