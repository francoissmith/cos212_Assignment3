public class delete {
    BPTreeLeafNode target = (BPTreeLeafNode) getNode(key);
    if (target == null) { // if key not in sequence set
        return this;
    }

    // remove key from leafnode
    removeFromLeaf(key, target);


    int minRequirement = m / 2; 

    // if node has underflow
    if (target.getKeyCount() < minRequirement) {
        BPTreeLeafNode left = (BPTreeLeafNode) target.leftSibling;
        BPTreeInnerNode parent = (BPTreeInnerNode) target.parentNode;
        BPTreeLeafNode right = (BPTreeLeafNode) target.rightSibling;

        if (target.leftSibling != null) {
            if (target.leftSibling.getKeyCount() >= minRequirement) { // if left has extra
                stealFromLeft(target, left, parent, key);
                return this;
            }
        }  
        
        if (target.rightSibling != null) {
            if (target.rightSibling.getKeyCount() >= minRequirement) {
                stealFromRight(target, right, parent, key);
                return this;
            }
        }

        if (target.parentNode != null) {
            if (target.parentNode.getKeyCount() > 1) {
               // removeFromInner(key, (BPTreeInnerNode)target.parentNode);
                return this;
            }
        }

        if (target.leftSibling != null) {
            merge(target, left, right, parent, key, true);
            return parent;
        } else if (target.rightSibling != null){
            merge(target, left, right, parent, key, false);
            return parent;
        }
    }
    return this;
}
