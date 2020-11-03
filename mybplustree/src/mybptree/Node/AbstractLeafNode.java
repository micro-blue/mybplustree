package mybptree.Node;

public abstract class AbstractLeafNode<TKey extends Comparable<TKey>,TValue> extends BTreeNode<TKey,TValue> {
    public AbstractLeafNode(TKey tKey) {
        super(tKey);
    }

    public AbstractLeafNode() {
    }

    public abstract TValue gettValue() ;

    public abstract void settValue(TValue tValue);


}
