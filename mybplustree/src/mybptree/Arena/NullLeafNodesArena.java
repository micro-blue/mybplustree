package mybptree.Arena;


import mybptree.MyBPTree;
import mybptree.Node.BTreeNode;
import mybptree.Node.LeafNode;
import mybptree.Node.nodesnumstate.OneNodeState;

public class NullLeafNodesArena<TKey extends Comparable<TKey>,TValue>  extends NodesArena <TKey,TValue>{

    MyBPTree tree;
    public NullLeafNodesArena(MyBPTree<TKey, TValue> tree) {
        this.parentArena=new DummyNodesArena(tree);
        this.tree=tree;
    }

    public NullLeafNodesArena(MyBPTree tree, DummyNodesArena dummyNodesArena) {
        this.parentArena=dummyNodesArena;
        this.tree=tree;
    }

    @Override
    public  void insert(TKey tKey, TValue tValue) {
        LeafNode leafNode=new LeafNode(tKey,tValue);
        tree.root=new LeafNodesArena(OneNodeState.getInstance());
        tree.root.firstNode=leafNode;
        tree.root.parentArena=this.parentArena;
        this.parentArena=null;
        this.tree=null;
    }

    @Override
    public BTreeNode delete(TKey tKey, NodesArena<TKey, TValue> candidateArena) {
        return null;
    }

    @Override
    public boolean contains(TKey tKey) {
        return false;
    }

    @Override
    public TValue get(TKey tKey) {
        return null;
    }



}
