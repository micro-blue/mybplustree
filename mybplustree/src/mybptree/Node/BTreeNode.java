package mybptree.Node;


import mybptree.Arena.LeafNodesArena;
import mybptree.Arena.NodesArena;
import mybptree.Arena.arenastate.ArenaState;

public abstract class BTreeNode<TKey extends Comparable<TKey>,TValue> extends GeneralBPNode<TKey,TValue> {
    private TKey key;
    public BTreeNode <TKey,TValue>next;

    public BTreeNode(TKey key) {
        this.key = key;
    }

    protected BTreeNode() {
    }

    public TKey getKey() {
        return key;
    }

    public void setKey(TKey key) {
        this.key = key;
    }

    public void handleOverFlow(NodesArena nodesArena){
        nodesArena.handleOverFlow(this);
    }
    public  void handleUnderFlow(LeafNodesArena<TKey,TValue> nodesArena, NodesArena<TKey,TValue> winner){throw new UnsupportedOperationException();};
    public void handleUnderFlow(NodesArena nodesArena){
        throw new UnsupportedOperationException();
    }

    public void selfDelete(NodesArena<TKey,TValue> nodesArena, ArenaState arenaState){throw new UnsupportedOperationException();};

    public BTreeNode<TKey,TValue> pullDown(ArenaState arenaState){throw new UnsupportedOperationException();};
}
