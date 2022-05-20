// Name: Francois Smith
// Student Number: 19314486

/**
 * A B+ tree leaf node
 * @param <TKey> the data type of the key
 * @param <TValue> the data type of the value
 */
class BPTreeLeafNode<TKey extends Comparable<TKey>, TValue> extends BPTreeNode<TKey, TValue> {
	
	protected Object[] values;
	
	public BPTreeLeafNode(int order) {
		this.m = order;
		// The strategy used here first inserts and then checks for overflow. 
		// Thus an extra space is required i.e. m instead of m-1.
		// You can change this if needed.
		this.keys = new Object[m];
		this.values = new Object[m];
	}

	@SuppressWarnings("unchecked")
	public TValue getValue(int index) {
		return (TValue)this.values[index];
	}

	public void setValue(int index, TValue value) {
		this.values[index] = value;
	}
	
	@Override
	public boolean isLeaf() {
		return true;
	}

	////// You should not change any code above this line //////

	////// Implement functions below this line //////

	/**
	 * Insert a new key and its associated value into the B+ tree.
	 *///******************************************************************************************************** insert()
	public BPTreeNode<TKey, TValue> insert(TKey key, TValue value) 
	{
		if (getKeyCount() < m) {
			if (getKeyCount() == 0) { // if leaf node is empty : just insert
				setKey(0, key);
				setValue(0, value);
				this.keyTally++;
				return this;
			} 
			else { // else find where to insert
				int i = 0;
				boolean insert = false;
				while (!insert && i < getKeyCount()) { // iterate through keys
					if (getKey(i).compareTo(key) > 0) {
						for (int j = getKeyCount(); j > i; j--) {
							setKey(j, getKey(j-1));
							setValue(j, getValue(j-1));
						}
						setKey(i, key);
						setValue(i, value);
						this.keyTally++;
						insert = true;
					}

					if (i+1 == getKeyCount()) { // new key is greatest value in list
						setKey(i+1, key);
						setValue(i+1, value);
						this.keyTally++;
						insert = true;
					}
					i++;
				}
			}
		}
		
		if ( getKeyCount() == m) {
			BPTreeInnerNode<TKey, TValue> splitNode = split(this);
			return splitNode;
		}

		return this;
	}

	/**
	 * If a leaf node is full. Split the ndoe.
	 * Recieves node to be split as param.
	 *///******************************************************************************************************** split()
	int splitIndex; // keep track of index
	@SuppressWarnings({"unchecked", "rawtypes"})
	public BPTreeInnerNode<TKey, TValue> split(BPTreeLeafNode<TKey, TValue> node){
		// create new nodes 
		BPTreeLeafNode left = new BPTreeLeafNode(node.m);
		BPTreeLeafNode right= new BPTreeLeafNode(node.m);
		BPTreeInnerNode inner = new BPTreeInnerNode(node.m);
		int splitKeysAt = node.getKeyCount()/2; // the halfway index of old node's key set

		if (node.parentNode != null){ // if node has parent update parent node
			BPTreeInnerNode par = (BPTreeInnerNode)node.parentNode;
			splitIndex = par.moveKeyUp(node.getKey(splitKeysAt));
			inner = par;
		} else { // else new inner node becomes parent
			inner.setKey(0, node.getKey(splitKeysAt));
			inner.keyTally = 1;
			splitIndex = 0;
		}
		
		return setNodes(inner, left, right, node, splitKeysAt);
	}

	/**
	 * Split()'s helper function. Set the new inner, left and right nodes.'
	 *///******************************************************************************************************** setNodes()
	public BPTreeInnerNode<TKey, TValue> setNodes(BPTreeInnerNode<TKey, TValue> inner,
	BPTreeLeafNode<TKey, TValue> left, BPTreeLeafNode<TKey, TValue> right, BPTreeLeafNode<TKey, TValue> old, int splitKeysAt) {

		for (int i = 0; i < splitKeysAt; i++) { // move values to left node
			left.setKey(i, old.getKey(i));
			left.setValue(i, old.getValue(i));
			left.keyTally++;
		}

		for (int i = splitKeysAt; i < old.getKeyCount(); i++) { // move key values to right node
			right.setKey(right.getKeyCount(), old.getKey(i));
			right.setValue(right.getKeyCount(), old.getValue(i));
			right.keyTally++;
		}

		// update sequence set
		right.leftSibling = left;
		right.rightSibling = old.rightSibling;

		if (right.rightSibling != null)
			right.rightSibling.leftSibling = right;

		left.rightSibling = right;
		left.leftSibling = old.leftSibling;

		if (left.leftSibling != null)
			left.leftSibling.rightSibling = left;

		// set parent node of new leafNodes
		right.setParent(inner);
		left.setParent(inner);

		// set children of new innerNode
		if (old.parentNode != null) {
			inner.setChild(splitIndex + 1, right);
			inner.setChild(splitIndex, left);
		} else {
			inner.setChild(1, right);
			inner.setChild(0, left);
		}

		return inner;
	}

	/**
	 * Delete a key from the sequence set.
	 *///******************************************************************************************************** delete()
	public BPTreeNode<TKey, TValue> delete(TKey key) 
	{
		int index = 0;
		boolean deleted = false;

		while(!deleted && index < getKeyCount()) { // search for key
			if (getKey(index).equals(key)) {
				for (int i = index; i > getKeyCount(); i++) { // if found update keys array
					setKey(i, getKey(i+1));
					setValue(i, getValue(i+1));
				}
				keyTally--;
				deleted = true;
			}
			index++;
		}
		return this; 
	}

}
