package mybptree.Arena;


import mybptree.Arena.arenastate.ArenaState;
import mybptree.Arena.arenastate.Impl.DummyIndexArenaState;
import mybptree.Arena.arenastate.Impl.DummyLeafArenaState;
import mybptree.MyBPTree;
import mybptree.Node.BTreeNode;
import mybptree.Node.nodesnumstate.OneNodeState;

public class DummyNodesArena<TKey extends Comparable<TKey>,TValue> extends NodesArena<TKey,TValue> {
    MyBPTree<TKey,TValue> bpTree;
    public DummyNodesArena() {
    }
    public DummyNodesArena(MyBPTree bpTree) {
        this.bpTree = bpTree;
    }
    @Override
    protected void pushUp(BTreeNode outNode, NodesArena left, NodesArena right) {
        IndexNodesArena newRootArena=new IndexNodesArena<>(OneNodeState.getInstance());
        newRootArena.firstNode=outNode;
        newRootArena.firstChildArena=left;
        newRootArena.parentArena=this;
        newRootArena.firstChildArena.parentArena=newRootArena;
        newRootArena.firstChildArena.nextSibling.parentArena=newRootArena;
        bpTree.root=newRootArena;
    }
    @Override
    public void handleUnderFlow(BTreeNode pullDownNode) {
        if(bpTree.root instanceof IndexNodesArena){
            bpTree.root=((IndexNodesArena)bpTree.root).firstChildArena;
            this.bpTree.root.parentArena=this;
        }else if(bpTree.root instanceof LeafNodesArena){
            bpTree.root = new NullLeafNodesArena<>(bpTree,this);
        }
    }
    @Override
    protected NodesArena splitArenaIntoTwoArena() {
        throw new UnsupportedOperationException();
    }
    @Override
    public void pullDown(ArenaState parentArenaState) {
        if(parentArenaState instanceof DummyIndexArenaState){
            this.bpTree.root=((IndexNodesArena)this.bpTree.root).firstChildArena;
            this.bpTree.root.parentArena=this;
        }else if(parentArenaState instanceof DummyLeafArenaState){
            this.bpTree.root= new NullLeafNodesArena(this.bpTree,this);
        }else {
            throw new UnsupportedOperationException();
        }
    }
    @Override
    public ArenaState getArenaStateOfZeroNode(NodesArena nodesArena) {
        return nodesArena.getDummy();
    }
    @Override
    public BTreeNode tryToBorrowNodeFromSiblingForIndex(ArenaState arenaState) {
        return null;
    }
    @Override
    public BTreeNode tryToBorrowNodeFromParentForIndex(ArenaState parentArenaState) {
        return null;
    }
    @Override
    public void maintainChildForIndex(ArenaState parentArenaState) {
        parentArenaState.maintainDummyNodesArena(this);
    }
    @Override
    public BTreeNode tryToBorrowNodeFromSiblingForLeaf(ArenaState arenaState) {
        return null;
    }
    @Override
    public BTreeNode tryToBorrowNodeFromParentForLeaf(ArenaState parentArenaState) {
        return null;
    }
    @Override
    public void maintainChildForLeaf(ArenaState parentArenaState) {
        return;
    }

}
