// Name: Francois Smith
// Student Number: 19314486

/**
 * A B+ tree generic node
 * Abstract class with common methods and data. Each kind of node implements
 * this class.
 * 
 * @param <TKey>   the data type of the key
 * @param <TValue> the data type of the value
 */
abstract class BPTreeNode<TKey extends Comparable<TKey>, TValue> {

	protected Object[] keys;
	protected int keyTally;
	protected int m;
	protected BPTreeNode<TKey, TValue> parentNode;
	protected BPTreeNode<TKey, TValue> leftSibling;
	protected BPTreeNode<TKey, TValue> rightSibling;
	protected static int level = 0; // do not modify this variable's value as it is used for printing purposes. You
									// can create another variable with a different name if you need to store the
									// level.

	protected BPTreeNode() {
		this.keyTally = 0;
		this.parentNode = null;
		this.leftSibling = null;
		this.rightSibling = null;
	}

	public int getKeyCount() {
		return this.keyTally;
	}

	@SuppressWarnings("unchecked")
	public TKey getKey(int index) {
		return (TKey) this.keys[index];
	}

	public void setKey(int index, TKey key) {
		this.keys[index] = key;
	}

	public BPTreeNode<TKey, TValue> getParent() {
		return this.parentNode;
	}

	public void setParent(BPTreeNode<TKey, TValue> parent) {
		this.parentNode = parent;
	}

	public abstract boolean isLeaf();

	/**
	 * Print all nodes in a subtree rooted with this node
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void print(BPTreeNode<TKey, TValue> node) {
		level++;
		if (node != null) {
			System.out.print("Level " + level + " ");
			node.printKeys();
			System.out.println();

			// If this node is not a leaf, then
			// print all the subtrees rooted with this node.
			if (!node.isLeaf()) {
				BPTreeInnerNode inner = (BPTreeInnerNode<TKey, TValue>) node;
				for (int j = 0; j < (node.m); j++) {
					this.print((BPTreeNode<TKey, TValue>) inner.references[j]);
				}
			}
		}
		level--;
	}

	/**
	 * Print all the keys in this node
	 */
	protected void printKeys() {
		System.out.print("[");
		for (int i = 0; i < this.getKeyCount(); i++) {
			System.out.print(" " + this.keys[i]);
		}
		System.out.print("]");
	}

	////// You may not change any code above this line. You may add extra variables
	////// if need be //////

	////// Implement the functions below this line //////

	/**
	 * Search a key on the B+ tree and return its associated value using the index
	 * set. If the given key
	 * is not found, null should be returned.
	*///******************************************************************************************************** search()
	public TValue search(TKey key) {
		// Your code goes here
		return search(key, this);
	}

	//******************************************************************************************************** search()
	protected TValue search(TKey key, BPTreeNode<TKey, TValue> node) {
		if (node.isLeaf()) { // if node is already a leaf node search keys
			// int i = 0;
			for (int i = 0; i < getKeyCount(); i++) {
				if (getKey(i).equals(key)) {
					BPTreeLeafNode<TKey, TValue> leafNode = (BPTreeLeafNode<TKey, TValue>) node;
					return leafNode.getValue(i);
				}
			}
				return null;
		}

		BPTreeInnerNode<TKey, TValue> ptr = (BPTreeInnerNode<TKey, TValue>) node; // create an innerNode ptr

		while (!ptr.getChild(0).isLeaf()) { // traverse the tree until ptr's child is a leaf node
			ptr = (BPTreeInnerNode<TKey, TValue>) ptr.getChild(0);
		}

		BPTreeLeafNode<TKey, TValue> leafPtr = (BPTreeLeafNode<TKey, TValue>) ptr.getChild(0); // create a leafNode ptr

		while (leafPtr != null) {
			for (int j = 0; j < leafPtr.getKeyCount(); j++) { // search keys of sequence set
				if (leafPtr.getKey(j).equals(key)) {
					return leafPtr.getValue(j);
				}
			}

			leafPtr = (BPTreeLeafNode<TKey, TValue>) leafPtr.rightSibling; // if not found in first set move to the next
			// if rightSibling == null : loop will break and function will return null
		}

		return null;
	}

	/**
	 * Insert a new key and its associated value into the B+ tree. The root node of
	 * the
	 * changed tree should be returned.
	 *///******************************************************************************************************** insert()
	public BPTreeNode<TKey, TValue> insert(TKey key, TValue value) {
		// Your code goes here
		return this;
	}

	/**
	 * Delete a key and its associated value from the B+ tree. The root node of the
	 * changed tree should be returned.
	 *///******************************************************************************************************** delete()
	public BPTreeNode<TKey, TValue> delete(TKey key) {
		// Your code goes here
		return this;
	}

	/**
	 * Return all associated key values on the B+ tree in ascending key order using
	 * the sequence set. An array
	 * of the key values should be returned.
	 *///******************************************************************************************************** values()
	@SuppressWarnings({"unchecked", "rawtypes"})
	public TValue[] values()
	{
	// Your code goes here
		BPTreeInnerNode ptr = (BPTreeInnerNode)this;

		while (!ptr.getChild(0).isLeaf()) {
			ptr = (BPTreeInnerNode)ptr.getChild(0);
		}

		BPTreeLeafNode leafptr = (BPTreeLeafNode)ptr.getChild(0);

		int numValues = 0;
		while (leafptr != null) {
			numValues += leafptr.getKeyCount();
			
			leafptr = (BPTreeLeafNode) leafptr.rightSibling;
		}

		Object[] values = new Object[numValues];

		leafptr = (BPTreeLeafNode)ptr.getChild(0);
		int i = 0;
		while (leafptr != null) {
			for (int j = 0; j < leafptr.getKeyCount(); j++) {
				values[i++] = leafptr.getValue(j);
			}

			leafptr = (BPTreeLeafNode) leafptr.rightSibling;
		}
		
		return (TValue[])values;
	}
}