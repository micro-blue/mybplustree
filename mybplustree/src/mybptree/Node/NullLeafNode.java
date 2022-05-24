package mybptree.Node;

import mybptree.Arena.LeafNodesArena;
import mybptree.Arena.NodesArena;

public class NullLeafNode<TKey extends Comparable<TKey>, TValue> extends AbstractLeafNode<TKey, TValue> {
    private static NullLeafNode nullLeafNode = new NullLeafNode();



    private NullLeafNode() {
    }

    public static NullLeafNode getInstance() {
        return nullLeafNode;
    }

    @Override
    public TValue gettValue() {
        return null;
    }

    @Override
    public void settValue(TValue tValue) {

    }

    @Override
    public void handleUnderFlow(NodesArena nodesArena) {
        return;  //什么也不做， 表示没有需要下沉的元素
    }

    @Override
    public void handleUnderFlow(LeafNodesArena nodesArena, NodesArena winner) {
        return;
    }

    public NullLeafNode(TKey tKey) {
        super(tKey);
    }
}
