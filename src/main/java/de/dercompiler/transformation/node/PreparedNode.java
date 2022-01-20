package de.dercompiler.transformation.node;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.transformation.TransformationWarrningIds;
import firm.nodes.Node;

public class PreparedNode {

    private Node node;
    private NodeAccess access;

    public PreparedNode() {
        this.node = null;
        this.access = NodeAccess.UNINITALIZED;
    }

    public void prepare(Node node, NodeAccess access) {
        //otherwise we ignore the node
        if (access != NodeAccess.UNINITALIZED) {
            if (this.access != access && this.access != NodeAccess.UNINITALIZED) {
                new OutputMessageHandler(MessageOrigin.TRANSFORM).printWarning(TransformationWarrningIds.OVERRIDE_PREPARED_NODE, "Override prepared Node: " + this.access + " with: " + access);
            }
            this.node = node;
            this.access = access;
        }
    }

    public Node getPrepared(NodeAccess check) {
        if (access == NodeAccess.UNINITALIZED) return null;
        if (access != check) {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("Invalid NodeAccess, expected: " + check + " but we prepared: " + access);
            return null;
        }
        return node;
    }

    public boolean isPrepared(NodeAccess access) {
        return this.access != NodeAccess.UNINITALIZED && this.access == access;
    }
}
