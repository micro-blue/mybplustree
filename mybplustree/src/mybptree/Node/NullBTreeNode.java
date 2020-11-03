package mybptree.Node;


import mybptree.Arena.NodesArena;
import mybptree.Arena.arenastate.ArenaState;

public final class NullBTreeNode extends BTreeNode {
    private static NullBTreeNode nullBTreeNode=new NullBTreeNode();
    private NullBTreeNode(){}
    public static NullBTreeNode getInstance(){
        return nullBTreeNode;
    }
    public void handleOverFlow(NodesArena nodesArena){
        return;
    }

    @Override
    public void handleUnderFlow(NodesArena nodesArena) {
        return; //空节点，表示没有节点需要下沉，所以什么也不做
    }

    @Override
    public void selfDelete(NodesArena nodesArena, ArenaState arenaState) {
    }


}
