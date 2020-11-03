package mybptree.Arena;


import mybptree.Arena.arenastate.ArenaState;
import mybptree.Arena.arenastate.Impl.DummyLeafArenaState;
import mybptree.Node.AbstractLeafNode;
import mybptree.Node.BTreeNode;
import mybptree.Node.LeafNode;
import mybptree.Node.NullLeafNode;
import mybptree.Node.nodesnumstate.NodesNumState;
import mybptree.Node.nodesnumstate.OneNodeState;
import mybptree.Node.nodesnumstate.TwoNodeState;

public class LeafNodesArena <TKey extends Comparable<TKey>,TValue>  extends NodesArena <TKey,TValue>{
    public LeafNodesArena nextLeafArena;

    public LeafNodesArena(NodesNumState nodesNumState) {
        super(nodesNumState);
    }

    public LeafNodesArena() {

    }
    @Override
    public  void insert(TKey tKey, TValue tValue) {
        ArenaState arenaState = nodesNumState.getArenaStateOfInsertion(firstNode,tKey);
        LeafNode insertedNode=new LeafNode<>(tKey,tValue);
        BTreeNode popUpNode=arenaState.handleInsertion(this,insertedNode);
        popUpNode.handleOverFlow(this);
    }

    @Override
    public boolean contains(TKey tKey) {
        ArenaState arenaState = nodesNumState.getArenaStateOfSelection(firstNode,tKey);
        return arenaState.handleSelection(this ,tKey)!= NullLeafNode.getInstance();
    }

    @Override
    public TValue get(TKey tKey) {
        ArenaState arenaState = nodesNumState.getArenaStateOfSelection(firstNode,tKey);
        return ((AbstractLeafNode<TKey,TValue>)arenaState.handleSelection(this ,tKey)).gettValue();
    }

    @Override
    public BTreeNode<TKey,TValue> delete(TKey tKey, NodesArena<TKey, TValue> winner) {
        ArenaState arenaState = nodesNumState.getArenaStateOfSelection(firstNode,tKey);
        BTreeNode<TKey,TValue>deletedNode=arenaState.handleSelection(this ,tKey);//确定要删除的元素确实存在，若不存在会返回空对象
        if(deletedNode==NullLeafNode.getInstance())// 这是必要的if 因为我们希望在发现条件不满足时就立刻返回，而不去读取更多其它条， 这其实是一个“只要有一个条件不满足就结束”的情况，那么实际上我们从性能角度会建立优先级，即每读一次条件都判断，少读优先于多读
            return null;
        BTreeNode<TKey, TValue> sinkNode=arenaState.handleDeletion(this,deletedNode,winner);
        sinkNode.handleUnderFlow(this,winner);
        return deletedNode;
    }

    @Override
    public void handleUnderFlow(BTreeNode popUpIndexNode) {
        return;
    }

    @Override
    protected void pushUp(BTreeNode outNode, NodesArena left, NodesArena right) {
        throw new UnsupportedOperationException("pushUp() shouldn`t be called by LeafNodesArena ");
    }
    protected NodesArena splitArenaIntoTwoArena() {
        LeafNodesArena newRightLeafArena=new LeafNodesArena<>(TwoNodeState.getInstance());
        this.nodesNumState= OneNodeState.getInstance();
        BTreeNode secondNode=this.firstNode.next;
        firstNode.next=null;
        newRightLeafArena.firstNode=secondNode;
        LeafNodesArena tmp=this.nextLeafArena;
        this.nextLeafArena=newRightLeafArena;
        newRightLeafArena.nextLeafArena=tmp;
        return newRightLeafArena;
    }

    @Override
    protected ArenaState getDummy() {
        return DummyLeafArenaState.getInstance();
    }
}
