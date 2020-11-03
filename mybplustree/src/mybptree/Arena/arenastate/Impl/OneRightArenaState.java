package mybptree.Arena.arenastate.Impl;

import mybptree.Arena.*;
import mybptree.Arena.arenastate.ArenaState;
import mybptree.Node.BTreeNode;
import mybptree.Node.LeafNode;
import mybptree.Node.NullBTreeNode;
import mybptree.Node.NullLeafNode;
import mybptree.Node.nodesnumstate.OneNodeState;
import mybptree.Node.nodesnumstate.TwoNodeState;

public class OneRightArenaState implements ArenaState {
    private static OneRightArenaState oneRightArenaState=new OneRightArenaState();
    private OneRightArenaState(){}
    public static ArenaState getInstance(){
        return oneRightArenaState;
    }
    @Override
    public BTreeNode handleInsertion(NodesArena nodesArena, BTreeNode insertNode) {
        nodesArena.firstNode.next=insertNode;
        nodesArena.nodesNumState= TwoNodeState.getInstance();
        return NullBTreeNode.getInstance();
    }

    //called only in leafNodesArena delete
    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> handleDeletion(NodesArena<TKey, TValue> nodesArena, BTreeNode<TKey,TValue>deletedNode, NodesArena<TKey, TValue> winner) {
        deletedNode.selfDelete(nodesArena,this); // 删除内部指针，
        return deletedNode;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> doDelete(NodesArena<TKey, TValue> nodesArena, LeafNode<TKey, TValue> deleteNode) {
        BTreeNode deletedNode=nodesArena.firstNode;
        nodesArena.firstNode=null;
       // nodesArena.nodesNumState= OneNodeState.getInstance();
        return deletedNode;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> NodesArena getChildArena(IndexNodesArena<TKey, TValue> nodesArena) {
        return nodesArena.firstChildArena.nextSibling;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode handleSelection(NodesArena<TKey, TValue> nodesArena, TKey tKey) {
        return nodesArena.firstNode.getKey().compareTo(tKey)==0?nodesArena.firstNode: NullLeafNode.getInstance();
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> NodesArena<TKey, TValue> checkEqualIndex(TKey tKey, NodesArena<TKey, TValue> nodesArena) {
        return tKey.compareTo(nodesArena.firstNode.getKey())==0?nodesArena: NullNodesArena.getInstance();
    }

    @Override
    public <TKey extends Comparable<TKey>,TValue> void updateIndex(TKey tKey, NodesArena<TKey, TValue> targetArena) {
        assert tKey.compareTo(targetArena.firstNode.getKey())!=0;
        targetArena.firstNode.setKey(tKey);
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> tryToBorrowNodeFromParentForLeaf(NodesArena<TKey, TValue> parentArena) {
        return null;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> handleDeletion(IndexNodesArena<TKey, TValue> nodesArena, BTreeNode<TKey, TValue> deletedNode) {
        nodesArena.firstNode=null;
        return deletedNode;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> void maintainChildForLeaf(IndexNodesArena<TKey, TValue> nodesArena) {
        LeafNodesArena newSibling=((LeafNodesArena)nodesArena.firstChildArena.nextSibling).nextLeafArena;
        ((LeafNodesArena)nodesArena.firstChildArena).nextLeafArena=newSibling;
        nodesArena.firstChildArena.nextSibling=null;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> tryToBorrowNodeFromSiblingForIndex(NodesArena<TKey, TValue> parentArena) {
        NodesArena lender= ((IndexNodesArena)parentArena).firstChildArena;
        NodesArena borrower= lender.nextSibling;
        return lender.nodesNumState.tryBorrowForIndex(lender,borrower,this);
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> doBorrowForIndex(NodesArena lender, NodesArena borrower) {
        NodesArena parentArena=borrower.parentArena;
        BTreeNode borrowedNode=parentArena.firstNode;
        borrower.firstNode=borrowedNode;
        parentArena.firstNode=lender.firstNode.next;
        lender.firstNode.next=null;
        NodesArena oldFirst=((IndexNodesArena)borrower).firstChildArena;
        ((IndexNodesArena)borrower).firstChildArena=((IndexNodesArena)lender).firstChildArena.nextSibling.nextSibling;
        ((IndexNodesArena)borrower).firstChildArena.nextSibling=oldFirst;
        ((IndexNodesArena)lender).firstChildArena.nextSibling.nextSibling=null;
        ((IndexNodesArena)borrower).firstChildArena.parentArena=borrower;
        lender.nodesNumState= OneNodeState.getInstance();
        return borrowedNode;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> tryToBorrowNodeFromParentForIndex(NodesArena<TKey, TValue> parentArena) {
        return null;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> void maintainChildForIndex(IndexNodesArena<TKey, TValue> nodesArena) {
        NodesArena childOfRight=((IndexNodesArena)nodesArena.firstChildArena.nextSibling).firstChildArena;
        IndexNodesArena leftChild= (IndexNodesArena) nodesArena.firstChildArena;
        leftChild.firstChildArena.nextSibling.nextSibling=childOfRight;
        leftChild.firstNode.next=nodesArena.firstNode;
        nodesArena.firstChildArena.nextSibling=null;
        childOfRight.parentArena=leftChild;
        leftChild.nodesNumState=TwoNodeState.getInstance();
    }

    @Override
    public <TValue, TKey extends Comparable<TKey>> void maintainDummyNodesArena(DummyNodesArena<TKey, TValue> dummyNodesArena) {

    }

    @Override
    public BTreeNode tryToBorrowNodeFromSiblingForLeaf(NodesArena parentArena) {
        NodesArena lender= ((IndexNodesArena)parentArena).firstChildArena;
        NodesArena borrower= lender.nextSibling;
        return lender.nodesNumState.tryBorrow(lender,borrower,this);
    }

    @Override
    public BTreeNode doBorrow(NodesArena lender, NodesArena borrower) {
        BTreeNode borrowedNode=lender.firstNode.next;
        borrower.firstNode=borrowedNode;
        lender.firstNode.next=null;
        borrower.parentArena.firstNode.setKey(borrowedNode.getKey());
        borrower.parentArena.firstNode.next=null;
        lender.nodesNumState= OneNodeState.getInstance();
        return borrowedNode;
    }
}
