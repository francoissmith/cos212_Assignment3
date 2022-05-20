// Name: Francois Smith
// Student Number: 19314486

/**
 * A B+ tree internal node
 * 
 * @param <TKey>   the data type of the key
 * @param <TValue> the data type of the value
 */
class BPTreeInnerNode<TKey extends Comparable<TKey>, TValue> extends BPTreeNode<TKey, TValue> {

	protected Object[] references;

	public BPTreeInnerNode(int order) {
		this.m = order;
		// The strategy used here first inserts and then checks for overflow.
		// Thus an extra space is required i.e. m instead of m-1/m+1 instead of m.
		// You can change this if needed.
		this.keys = new Object[m];
		this.references = new Object[m + 1];
	}

	@SuppressWarnings("unchecked")
	public BPTreeNode<TKey, TValue> getChild(int index) {
		return (BPTreeNode<TKey, TValue>) this.references[index];
	}

	public void setChild(int index, BPTreeNode<TKey, TValue> child) {
		this.references[index] = child;
		if (child != null)
			child.setParent(this);
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	////// You should not change any code above this line //////
	////// Implement functions below this line //////

	/**
	 * Insert a new key and its associated value into the B+ tree.
	 */// ******************************************************************************************************** insert()
	 int insertIndex = 0;
	@SuppressWarnings({"unchecked", "rawtypes"})
	public BPTreeNode<TKey, TValue> insert(TKey key, TValue value) {
		BPTreeInnerNode ptr = traverseTree(this, key);
		BPTreeLeafNode targetLeaf = (BPTreeLeafNode<TKey, TValue>) ptr.getChild(insertIndex);
		BPTreeNode targetNode = targetLeaf.insert(key, value);

		if (targetNode != targetLeaf) {
			if (targetNode.getKeyCount() == m) {
				targetNode = split((BPTreeInnerNode<TKey, TValue>) targetNode);
			}
			while (targetNode.getParent() != null) {
				targetNode = targetNode.getParent();
			}
			return targetNode;
		} else return this;
	}
	/**
	 * insert()'s helper function to traverse the tree and find target node.
	 */// ******************************************************************************************************** traverseTree()
	@SuppressWarnings({"unchecked", "rawtypes"})
	public BPTreeInnerNode<TKey, TValue> traverseTree(BPTreeInnerNode<TKey, TValue> node, TKey key) {
		BPTreeInnerNode ptr = node;
		Boolean found = false;
		while (!found) { // traverse tree 
			for (int i = 0; i < getKeyCount(); i++) { // iterate through keys
				if (getKey(i).compareTo(key) > 0) {
					if (ptr.getChild(i).isLeaf()) { // if ptr's child is a leaf: found insert location
						insertIndex = i;
						found = true;
					} else { // else traverse further down the tree
						ptr = (BPTreeInnerNode) ptr.getChild(i);
					}
				}

				if (i + 1 == getKeyCount()) { // new key is greatest value in list
					if (ptr.getChild(i + 1).isLeaf()) {// if ptr's child is a leaf: found insert location
						insertIndex = i + 1;
						found = true;
					} else { // else traverse further down the tree
						ptr = (BPTreeInnerNode<TKey, TValue>) ptr.getChild(i + 1);
					}
				}
			}
		}

		return ptr;
	}

	/**
	 * If a leaf node is full. Split the ndoe.
	 * Recieves node to be split as param.
	 */// ******************************************************************************************************** split()
	 int pos = 0;
	@SuppressWarnings({"unchecked", "rawtypes"})
	public BPTreeInnerNode<TKey, TValue> split(BPTreeInnerNode<TKey, TValue> node) {
		int splitKeysAt = node.getKeyCount() / 2; // the halfway index of old node's key set

		// create new nodes
		BPTreeInnerNode left = new BPTreeInnerNode(node.m);
		BPTreeInnerNode right = new BPTreeInnerNode(node.m);
		BPTreeInnerNode inner = new BPTreeInnerNode(node.m);

		if (node.parentNode != null) { // if node has parent update parent node
			BPTreeInnerNode parent = (BPTreeInnerNode<TKey, TValue>) node.parentNode;
			pos = parent.moveKeyUp(node.getKey(splitKeysAt));
			inner = parent;
		} else { // else new inner node becomes parent
			inner.setKey(0, node.getKey(splitKeysAt));
			inner.setChild(0, node.getChild(splitKeysAt));
			inner.keyTally = 1;
			pos = 0;
		}

		return setNodes(inner, left, right, node, splitKeysAt);
	}

	/**
	 * Split()'s helper function. Set the new inner, left and right nodes.'
	 *///******************************************************************************************************** setNodes()
	public BPTreeInnerNode<TKey, TValue> setNodes(BPTreeInnerNode<TKey, TValue> inner,
	BPTreeInnerNode<TKey, TValue> left, BPTreeInnerNode<TKey, TValue> right, BPTreeInnerNode<TKey, TValue> old, int splitKeysAt) {

		for (int i = 0; i < splitKeysAt; i++) { // move values to left node
			left.setKey(i, old.getKey(i));
			left.setChild(i, old.getChild(i));
			left.keyTally++;
		}
		left.setChild(left.getKeyCount(), old.getChild(splitKeysAt)); // set new leftNode's child

		for (int i = splitKeysAt + 1; i < old.getKeyCount(); i++) { // move key values to right node
			right.setKey(right.getKeyCount(), old.getKey(i));
			right.setChild(right.getKeyCount(), old.getChild(i));
			right.keyTally++;
		}
		right.setChild(right.getKeyCount(), old.getChild(old.getKeyCount() - 1).rightSibling); // set new rightNode's child

		// complete new nodes
		right.leftSibling = left;
		left.rightSibling = right;

		// set new node parents
		right.setParent(inner);
		left.setParent(inner);
		
		// complete new InnerNode
		if (pos + 2 < inner.getKeyCount()) { // right-part of keys
			inner.getChild(pos + 2).leftSibling = right;
			right.rightSibling = inner.getChild(pos + 2);
		}
		if (pos - 1 >= 0) { // left part of keys
			inner.getChild(pos - 1).rightSibling = left;
			left.leftSibling = inner.getChild(pos - 1);
		}
		if (old.parentNode != null) {
			inner.setChild(pos, left);
			inner.setChild(pos + 1, right);
		} else {
			inner.setChild(0, left);
			inner.setChild(1, right);
		}
		if (inner.getKeyCount() == m) {
			inner = split(inner);
		}

		return inner;
	}

	/**
	 * Delete the passed in key from the tree;
	 */// ******************************************************************************************************** delete()
	@SuppressWarnings({"unchecked", "rawtypes"})
	public BPTreeNode<TKey, TValue> delete(TKey key) {

		BPTreeLeafNode target = (BPTreeLeafNode) getNode(key);
		if (target == null) { // if key not in sequence set
			return this;
		}

		// remove key from leafnode
		removeFromLeaf(key, target);

		int minRequirement = m / 2 - 1; 

		// if node has underflow
		if (target.getKeyCount() < minRequirement) {
			BPTreeLeafNode left = (BPTreeLeafNode) target.leftSibling;
			BPTreeInnerNode parent = (BPTreeInnerNode) target.parentNode;
			BPTreeLeafNode right = (BPTreeLeafNode) target.rightSibling;

			if (target.leftSibling != null) {
				if (target.leftSibling.getKeyCount() > minRequirement) { // if left has extra
					stealFromLeft(target, left, parent, key);
					return this;
				}
			}  
			
			if (target.rightSibling != null) {
				if (target.rightSibling.getKeyCount() > minRequirement) {
					stealFromRight(target, right, parent, key);
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

	/**
	 * Remove passed in key from the sequence set.
	 */// ******************************************************************************************************** removeFromLeaf()
	public void removeFromLeaf(TKey key, BPTreeLeafNode<TKey, TValue> target) {
		for (int i = 0; i < target.getKeyCount(); i++) {
			if (target.getKey(i).equals(key)) {
				for (int j = i; j < target.getKeyCount(); j++) {
					target.setKey(j, target.getKey(j + 1));
					target.setValue(j, target.getValue(j + 1));
				}
				target.keyTally--;
				break;
			}
		}
	}

	/**
	 * delete() helper function. Merge node to the left or right.
	 */// ******************************************************************************************************** merge()
	public void merge(BPTreeLeafNode<TKey, TValue> target, BPTreeLeafNode<TKey, TValue> left, BPTreeLeafNode<TKey, TValue> right, BPTreeInnerNode<TKey, TValue> parent, TKey key, Boolean mergeLeft) {
		if (mergeLeft) {
			for (int i = 0; i < right.getKeyCount(); i++) {
				left.setKey(left.getKeyCount(), right.getKey(i));
				left.keyTally++;
			}
		} else {
			for (int i = 0; i < target.getKeyCount(); i++) {
				right.setKey(target.getKeyCount() + i, right.getKey(i));
			}

			for (int i = 0; i < target.getKeyCount(); i++) {
				right.setKey(i, target.getKey(i));
				right.keyTally++;
			}
		}

		for (int i = 0; i < parent.getKeyCount(); i++) {
			if (parent.getKey(i).equals(key)) {
				for (int j = i; j < parent.getKeyCount(); j++) {
					parent.setKey(j, parent.getKey(j + 1));
					parent.setChild(j, parent.getChild(j + 1));
				}
				parent.keyTally--;
			}
		}
	}

	/**
	 * delete() helper function. Steal extra key from left sibling.
	 */// ******************************************************************************************************** stealFromLeft()
	public void stealFromLeft(BPTreeLeafNode<TKey, TValue> target, BPTreeLeafNode<TKey, TValue> left, BPTreeInnerNode<TKey, TValue> parent, TKey key) {
		for (int i = target.getKeyCount(); i > 0; i--) {
			target.setKey(i, target.getKey(i - 1));
			target.setValue(i, target.getValue(i - 1));
		}
		target.setKey(0, target.leftSibling.getKey(target.leftSibling.getKeyCount() - 1));
		target.setValue(0, left.getValue(target.leftSibling.getKeyCount() - 1));
		target.keyTally++;

		for (int i = 0; i < parent.getKeyCount(); i++) {
			if (parent.getKey(i).equals(key)) {
				parent.setKey(i, left.getKey(target.leftSibling.getKeyCount() - 1));
			}
		}

		target.leftSibling.keyTally--;
	}

	/**
	 * delete() helper function. Steal extra key from right sibling.
	 */// ******************************************************************************************************** stealFromRight()
	public void stealFromRight(BPTreeLeafNode<TKey, TValue> target, BPTreeLeafNode<TKey, TValue> right, BPTreeInnerNode<TKey, TValue> parent, TKey key) {
		target.setKey(target.getKeyCount(), target.rightSibling.getKey(0));
		target.setValue(target.getKeyCount(), right.getValue(0));
		target.keyTally++;

		for (int i = 0; i < target.getKeyCount(); i++) {
			target.rightSibling.setKey(i, target.rightSibling.getKey(i + 1));
		}
		target.rightSibling.keyTally--;

		for (int i = 0; i < parent.getKeyCount(); i++) {
			if (parent.getKey(i).equals(key)) {
				parent.setKey(i + 1, target.rightSibling.getKey(0));
				break;
			}
		}
	}

	/**
	 * Used by Split() function. Send key from current node to parent.
	 */// ******************************************************************************************************** moveKeyUp()
	public int moveKeyUp(TKey key) {
		for (int i = 0; i < getKeyCount(); i++) {
			if (getKey(i).compareTo(key) > 0) {
				for (int j = getKeyCount(); j > i; j--) {
					setKey(j, getKey(j - 1));
				}
				for (int j = getKeyCount(); j > i; j--) {
					setChild(j + 1, getChild(j));
				}

				setKey(i, key);
				keyTally++;
				return i;
			}
			if (i + 1 == getKeyCount()) {
				setKey(i + 1, key);
				keyTally++;
				return i + 1;
			}
		}
		return 0;
	}

	/**
	 * Returns node containing passed in key.
	 */// ******************************************************************************************************** getNode()
	 @SuppressWarnings({"unchecked", "rawtypes"})
	public BPTreeNode<TKey, TValue> getNode(TKey key) {
		BPTreeInnerNode ptr = (BPTreeInnerNode) this;

		while (!ptr.getChild(0).isLeaf()) {
			ptr = (BPTreeInnerNode) ptr.getChild(0);
		}

		BPTreeLeafNode leafPtr = (BPTreeLeafNode) ptr.getChild(0);
		while (leafPtr != null) {
			for (int i = 0; i < leafPtr.getKeyCount(); i++) {
				if (leafPtr.getKey(i).equals(key)) {
					return leafPtr;
				}
			}

			leafPtr = (BPTreeLeafNode<TKey, TValue>) leafPtr.rightSibling;
		}

		return null;
	}

}