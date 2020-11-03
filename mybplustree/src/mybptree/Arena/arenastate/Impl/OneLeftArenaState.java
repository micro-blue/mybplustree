package mybptree.Arena.arenastate.Impl;

import mybptree.Arena.*;
import mybptree.Arena.arenastate.ArenaState;
import mybptree.Node.BTreeNode;
import mybptree.Node.LeafNode;
import mybptree.Node.NullBTreeNode;
import mybptree.Node.NullLeafNode;
import mybptree.Node.nodesnumstate.OneNodeState;
import mybptree.Node.nodesnumstate.TwoNodeState;

public final class OneLeftArenaState implements ArenaState {
    private static OneLeftArenaState oneLeftArenaState=new OneLeftArenaState();
    private OneLeftArenaState(){}
    public static ArenaState getInstance(){
        return oneLeftArenaState;
    }


    @Override
    public BTreeNode handleInsertion(NodesArena nodesArena, BTreeNode insertNode) {
        BTreeNode oldFirst=nodesArena.firstNode;
        nodesArena.firstNode=insertNode;
        insertNode.next=oldFirst;
        nodesArena.nodesNumState= TwoNodeState.getInstance();
        return NullBTreeNode.getInstance();
    }


    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> handleDeletion(NodesArena<TKey, TValue> nodesArena,BTreeNode<TKey,TValue>deletedNode, NodesArena<TKey, TValue> winner) {
        throw new UnsupportedOperationException("handleDeletion should not be called in OneLeftArenaState");
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> doDelete(NodesArena<TKey, TValue> nodesArena, LeafNode<TKey, TValue> deleteNode) {
        throw new UnsupportedOperationException("doDelete should not be called in OneLeftArenaState, NullBTreeNode should be called");
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> NodesArena getChildArena(IndexNodesArena<TKey, TValue> nodesArena) {
        return nodesArena.firstChildArena;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode handleSelection(NodesArena<TKey, TValue> nodesArena, TKey tKey) {
        return NullLeafNode.getInstance();
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> NodesArena<TKey, TValue> checkEqualIndex(TKey tKey, NodesArena<TKey, TValue> candidateArena) {
        return NullNodesArena.getInstance();
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> void updateIndex(TKey tKey, NodesArena<TKey, TValue> targetArena) {
       // targetArena.firstNode.setKey(tKey);

        throw new UnsupportedOperationException("updateIndex should not be called in OneLeftArenaState, NullBTreeNode should be called");
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> tryToBorrowNodeFromSiblingForLeaf(NodesArena parentArena) {
        NodesArena borrower= ((IndexNodesArena)parentArena).firstChildArena;
        NodesArena lender=borrower.nextSibling;
        return lender.nodesNumState.tryBorrow(lender,borrower,this);
    }
    // todo 实际 进行borrow的指针修改
    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> doBorrow(NodesArena lender, NodesArena borrower) {
        BTreeNode borrowedNode=lender.firstNode;
        borrower.firstNode=borrowedNode;
        lender.firstNode=borrowedNode.next;
        lender.parentArena.firstNode.setKey(lender.firstNode.getKey());
        borrower.firstNode.next=null;
        lender.nodesNumState= OneNodeState.getInstance();
        return borrowedNode;
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
        LeafNodesArena leftChild= (LeafNodesArena) nodesArena.firstChildArena;
        LeafNodesArena rightChild= (LeafNodesArena) nodesArena.firstChildArena.nextSibling;
        leftChild.firstNode=leftChild.nextSibling.firstNode;
        leftChild.nextLeafArena=rightChild.nextLeafArena;
        leftChild.nextSibling=null;

        /*nodesArena.firstChildArena.firstNode=nodesArena.firstChildArena.nextSibling.firstNode;
        nodesArena.firstChildArena.nextSibling=nodesArena.firstChildArena.nextSibling.nextSibling;*/
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> tryToBorrowNodeFromSiblingForIndex(NodesArena<TKey, TValue> parentArena) {
        NodesArena borrower= ((IndexNodesArena)parentArena).firstChildArena;
        NodesArena lender=borrower.nextSibling;
        return lender.nodesNumState.tryBorrowForIndex(lender,borrower,this);
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> doBorrowForIndex(NodesArena lender, NodesArena borrower) {
        NodesArena parentArena=borrower.parentArena;
        BTreeNode borrowedNode=parentArena.firstNode;
        borrower.firstNode=borrowedNode;
        parentArena.firstNode=lender.firstNode;
        lender.firstNode=lender.firstNode.next;
        ((IndexNodesArena)borrower).firstChildArena.nextSibling=((IndexNodesArena)lender).firstChildArena;
        ((IndexNodesArena)lender).firstChildArena=((IndexNodesArena)lender).firstChildArena.nextSibling;
        ((IndexNodesArena)borrower).firstChildArena.nextSibling.parentArena=borrower;
        lender.nodesNumState=OneNodeState.getInstance();
        return borrowedNode;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> tryToBorrowNodeFromParentForIndex(NodesArena<TKey, TValue> parentArena) {
        return null;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> void maintainChildForIndex(IndexNodesArena<TKey, TValue> nodesArena) {
        NodesArena childOfLeft=((IndexNodesArena)nodesArena.firstChildArena).firstChildArena;
        IndexNodesArena rightChild= (IndexNodesArena) nodesArena.firstChildArena.nextSibling;
        NodesArena tmp=rightChild.firstChildArena;
        rightChild.firstChildArena=childOfLeft;
        childOfLeft.nextSibling=tmp;
        nodesArena.firstChildArena=rightChild;
        nodesArena.firstNode.next=rightChild.firstNode;
        rightChild.firstNode=nodesArena.firstNode;
        //childOfLeft.parentArena.parentArena=null;
        childOfLeft.parentArena=rightChild;
      //  nodesArena.firstNode=null;
        rightChild.nodesNumState=TwoNodeState.getInstance();
    }

    @Override
    public <TValue, TKey extends Comparable<TKey>> void maintainDummyNodesArena(DummyNodesArena<TKey, TValue> dummyNodesArena) {

    }


}
