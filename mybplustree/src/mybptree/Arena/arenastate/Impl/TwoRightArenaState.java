package mybptree.Arena.arenastate.Impl;


import mybptree.Arena.*;
import mybptree.Arena.arenastate.ArenaState;
import mybptree.Node.BTreeNode;
import mybptree.Node.IndexNode;
import mybptree.Node.LeafNode;
import mybptree.Node.NullLeafNode;
import mybptree.Node.nodesnumstate.OneNodeState;
import mybptree.Node.nodesnumstate.TwoNodeState;

public class TwoRightArenaState implements ArenaState {
    private static TwoRightArenaState twoRightArenaState=new TwoRightArenaState();
    private TwoRightArenaState(){}
    public static ArenaState getInstance(){
        return twoRightArenaState;
    }
    @Override
    public BTreeNode handleInsertion(NodesArena nodesArena, BTreeNode insertNode) {
        nodesArena.firstNode.next.next=insertNode;
        return new IndexNode(nodesArena.firstNode.next.getKey());
    }


    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> handleDeletion(NodesArena<TKey, TValue> nodesArena,BTreeNode<TKey,TValue>deletedNode, NodesArena<TKey, TValue> winner) {
        deletedNode.selfDelete(nodesArena,this);
        return NullLeafNode.getInstance();
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> doDelete(NodesArena<TKey, TValue> nodesArena, LeafNode<TKey, TValue> deleteNode) {
        nodesArena.firstNode.next=null;
        nodesArena.nodesNumState= OneNodeState.getInstance();
        return deleteNode;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> NodesArena getChildArena(IndexNodesArena<TKey, TValue> nodesArena) {
        return nodesArena.firstChildArena.nextSibling.nextSibling;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode handleSelection(NodesArena<TKey, TValue> nodesArena, TKey tKey) {
        //todo 可以避免使用三元运算(分支语句)， 因为compareTo 可以约定 返回1,0，-1，
        // 如 arr[0] -> function{nodesArena->nodesArena.firstNode} 所以可以用数组做 映射，这是理论上能做到，但由于所需表达的逻辑比较简单，此时性能为控制条件
        return nodesArena.firstNode.next.getKey().compareTo(tKey)==0?nodesArena.firstNode.next: NullLeafNode.getInstance();
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> NodesArena<TKey, TValue> checkEqualIndex(TKey tKey, NodesArena<TKey, TValue> nodesArena) {
        return nodesArena.firstNode.next.getKey().compareTo(tKey)==0?nodesArena: NullNodesArena.getInstance();
    }

    @Override
    public <TKey extends Comparable<TKey>,TValue> void updateIndex(TKey tKey, NodesArena<TKey, TValue> targetArena) {
        targetArena.firstNode.next.setKey(tKey);
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> tryToBorrowNodeFromParentForLeaf(NodesArena<TKey, TValue> parentArena) {
        BTreeNode borrowedNode=parentArena.firstNode;
        LeafNodesArena borrower= (LeafNodesArena) ((IndexNodesArena)parentArena).firstChildArena.nextSibling.nextSibling;
        LeafNodesArena lender= (LeafNodesArena) ((IndexNodesArena)parentArena).firstChildArena.nextSibling;
        lender.nextSibling=null;
        lender.nextLeafArena=borrower.nextLeafArena;
        parentArena.firstNode.next=null;
        parentArena.nodesNumState=OneNodeState.getInstance();
        return borrowedNode;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> handleDeletion(IndexNodesArena<TKey, TValue> nodesArena, BTreeNode<TKey, TValue> deletedNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> void maintainChildForLeaf(IndexNodesArena<TKey, TValue> nodesArena) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> tryToBorrowNodeFromSiblingForIndex(NodesArena<TKey, TValue> parentArena) {
        NodesArena lender= ((IndexNodesArena)parentArena).firstChildArena.nextSibling;
        NodesArena borrower= lender.nextSibling;
        return lender.nodesNumState.tryBorrowForIndex(lender,borrower,this);
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> doBorrowForIndex(NodesArena lender, NodesArena borrower) {
        NodesArena parentArena=borrower.parentArena;
        BTreeNode borrowedNode=parentArena.firstNode;
        borrower.firstNode=parentArena.firstNode.next;
        parentArena.firstNode.next=lender.firstNode.next;
        NodesArena oldFirst=((IndexNodesArena)borrower).firstChildArena;
        ((IndexNodesArena)borrower).firstChildArena=((IndexNodesArena)lender).firstChildArena.nextSibling.nextSibling;
        ((IndexNodesArena)borrower).firstChildArena.nextSibling=oldFirst;
        ((IndexNodesArena)lender).firstChildArena.nextSibling.nextSibling=null;
        ((IndexNodesArena)borrower).firstChildArena.parentArena=borrower;
        lender.nodesNumState=OneNodeState.getInstance();
        return borrowedNode;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> tryToBorrowNodeFromParentForIndex(NodesArena<TKey, TValue> parentArena) {
        BTreeNode borrowedNode=parentArena.firstNode.next;
        IndexNodesArena midChildArena= (IndexNodesArena) ((IndexNodesArena<TKey, TValue>)parentArena).firstChildArena.nextSibling;
        IndexNodesArena rightChildArena= (IndexNodesArena) ((IndexNodesArena<TKey, TValue>)parentArena).firstChildArena.nextSibling.nextSibling;
        NodesArena childOfBorrower=rightChildArena.firstChildArena;
        midChildArena.firstNode.next=borrowedNode;
        parentArena.firstNode.next=null;
        midChildArena.nextSibling=null;
        midChildArena.firstChildArena.nextSibling.nextSibling=childOfBorrower;
        childOfBorrower.parentArena=midChildArena;
        parentArena.nodesNumState=OneNodeState.getInstance();
        midChildArena.nodesNumState= TwoNodeState.getInstance();
        return borrowedNode;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> void maintainChildForIndex(IndexNodesArena<TKey, TValue> nodesArena) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <TValue, TKey extends Comparable<TKey>> void maintainDummyNodesArena(DummyNodesArena<TKey, TValue> dummyNodesArena) {

    }

    @Override
    public BTreeNode tryToBorrowNodeFromSiblingForLeaf(NodesArena parentArena) {
        NodesArena lender= ((IndexNodesArena)parentArena).firstChildArena.nextSibling;
        NodesArena borrower= lender.nextSibling;
        return lender.nodesNumState.tryBorrow(lender,borrower,this);
    }

    @Override
    public BTreeNode doBorrow(NodesArena lender, NodesArena borrower) {
     //   lender.parentArena
        BTreeNode borrowedNode=lender.firstNode.next;
        borrower.firstNode=borrowedNode;
        lender.firstNode.next=null;
        lender.parentArena.firstNode.next.setKey(borrowedNode.getKey());
        lender.nodesNumState= OneNodeState.getInstance();
        return borrowedNode;
    }


}
