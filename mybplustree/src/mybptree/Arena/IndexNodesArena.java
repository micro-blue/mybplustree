package mybptree.Arena;


import mybptree.Arena.arenastate.ArenaState;
import mybptree.Arena.arenastate.Impl.DummyIndexArenaState;
import mybptree.Node.BTreeNode;
import mybptree.Node.nodesnumstate.NodesNumState;
import mybptree.Node.nodesnumstate.OneNodeState;

public class IndexNodesArena <TKey extends Comparable<TKey>,TValue>  extends NodesArena <TKey,TValue>{

    public NodesArena <TKey,TValue>firstChildArena;
    public IndexNodesArena() {
    }
    public IndexNodesArena(NodesNumState nodesNumState) {
        super(nodesNumState);
    }
    @Override
    public void insert(TKey tKey, TValue tValue) {
        ArenaState arenaState = nodesNumState.getArenaStateOfInsertion(firstNode,tKey);
        NodesArena childArena=arenaState.getChildArena(this);
        childArena.insert(tKey,tValue);
    }
    @Override
    public boolean contains(TKey tKey) {
        return nodesNumState.getArenaStateOfSelection(firstNode,tKey).getChildArena(this).contains(tKey);
    }

    @Override
    public void updateIndex(TKey tKey) {
        ArenaState updatedArenaState=nodesNumState.getArenaStateOfSelection(this.firstNode,tKey);
        updatedArenaState.updateIndex(tKey,  this);
    }
    @Override
    public TValue get(TKey tKey) {
        ArenaState arenaState = nodesNumState.getArenaStateOfSelection(firstNode,tKey);
        NodesArena<TKey,TValue>childArena=arenaState.getChildArena(this);
        return childArena.get(tKey);
        //写成一行的话，会由于getChildArena返回的nodesArena没有定义泛型，而导致泛型擦除，从而要求对返回值强转 ，
      //  return (TValue) nodesNumState.getArenaStateOfSelection(firstNode,tKey).getChildArena(this).get(tKey);
    }
    @Override
    public BTreeNode<TKey, TValue> delete(TKey tKey, NodesArena<TKey, TValue> nodesArena) {
        ArenaState arenaState = nodesNumState.getArenaStateOfSelection(firstNode,tKey);
        NodesArena<TKey,TValue>childArena=arenaState.getChildArena(this);
        NodesArena<TKey,TValue>resultArena=nodesArena.indexCheck(tKey,arenaState,this);
        return childArena.delete(tKey, resultArena);
    }
    @Override
    public void handleUnderFlow(BTreeNode pullDownNode) {
        NodesArena<TKey,TValue>parentArena=this.parentArena;
        ArenaState parentArenaState=parentArena.getArenaStateOfZeroNode(this);
        BTreeNode<TKey,TValue>borrowedNode =parentArena.tryToBorrowNodeFromSiblingForIndex(parentArenaState);
        if(borrowedNode==null) {//执行步骤2
            borrowedNode = parentArena.tryToBorrowNodeFromParentForIndex(parentArenaState);
            if(borrowedNode==null) {//执行步骤3
                parentArena.maintainChildForIndex(parentArenaState);
                parentArena.pullDown(parentArenaState);
            }
        }
    }
    @Override
    protected NodesArena<TKey, TValue> indexCheck(TKey tKey, ArenaState arenaState, NodesArena<TKey, TValue> candidateArena) {
        return this;
    }
    @Override
    public ArenaState getArenaStateOfZeroNode(NodesArena nodesArena) {
        return nodesNumState.getArenaStateOfZeroNode(nodesArena,this);
    }
    @Override
    public BTreeNode<TKey, TValue> tryToBorrowNodeFromSiblingForLeaf(ArenaState parentArenaState) {
        return parentArenaState.tryToBorrowNodeFromSiblingForLeaf(this);
    }
    @Override
    public BTreeNode<TKey, TValue> tryToBorrowNodeFromSiblingForIndex(ArenaState parentArenaState) {
        return parentArenaState.tryToBorrowNodeFromSiblingForIndex(this);
    }
    @Override
    public BTreeNode<TKey, TValue> tryToBorrowNodeFromParentForLeaf(ArenaState parentArenaState) {
        return parentArenaState.tryToBorrowNodeFromParentForLeaf(this);
    }
    @Override
    public BTreeNode<TKey, TValue> tryToBorrowNodeFromParentForIndex(ArenaState parentArenaState) {
        return parentArenaState.tryToBorrowNodeFromParentForIndex(this);
    }
    @Override
    protected void pushUp(BTreeNode outNode, NodesArena left, NodesArena right) {
        ArenaState arenaState = nodesNumState.getArenaStateOfInsertion(firstNode,outNode.getKey());
        BTreeNode popUpNode=arenaState.handleInsertion(this,outNode);
        popUpNode.handleOverFlow(this);
    }
    protected IndexNodesArena splitArenaIntoTwoArena() {//左1个(this) 右1个（new）
        IndexNodesArena newRight=new IndexNodesArena<>(OneNodeState.getInstance());
        this.nodesNumState=OneNodeState.getInstance();
        BTreeNode thirdNode=firstNode.next.next;
        //firstNode.next.next=null;//todo 该行可删除？
        firstNode.next=null;
        newRight.firstNode=thirdNode;
        newRight.firstChildArena=this.firstChildArena.nextSibling.nextSibling;
        this.firstChildArena.nextSibling.nextSibling.parentArena=newRight;
        this.firstChildArena.nextSibling.nextSibling.nextSibling.parentArena=newRight;
        this.firstChildArena.nextSibling.nextSibling=null;
        return newRight;
    }
    @Override
    public void maintainChildForLeaf(ArenaState parentArenaState) {
        parentArenaState.maintainChildForLeaf(this);
    }
    @Override
    public void maintainChildForIndex(ArenaState parentArenaState) {
        parentArenaState.maintainChildForIndex(this);
    }
    @Override
    public void pullDown(ArenaState arenaState) {
        BTreeNode<TKey, TValue> sinkNode=arenaState.handleDeletion(this,firstNode);
        sinkNode.handleUnderFlow(this);
    }
    @Override
    protected ArenaState getDummy() {
        return DummyIndexArenaState.getInstance();
    }
}
