package mybptree.Arena.arenastate.Impl;

import mybptree.Arena.*;
import mybptree.Arena.arenastate.ArenaState;
import mybptree.Node.BTreeNode;
import mybptree.Node.IndexNode;
import mybptree.Node.LeafNode;
import mybptree.Node.NullLeafNode;
import mybptree.Node.nodesnumstate.OneNodeState;
import mybptree.Node.nodesnumstate.TwoNodeState;

public class TwoLeftArenaState implements ArenaState {
    private static TwoLeftArenaState twoLeftArenaState=new TwoLeftArenaState();
    private TwoLeftArenaState(){}
    public static ArenaState getInstance(){
        return twoLeftArenaState;
    }
    @Override
    public BTreeNode handleInsertion(NodesArena nodesArena, BTreeNode insertNode) {
        BTreeNode oldFirst= nodesArena.firstNode;
        nodesArena.firstNode=insertNode;
        insertNode.next=oldFirst;
        return new IndexNode(oldFirst.getKey());
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> handleDeletion(NodesArena<TKey, TValue> nodesArena, BTreeNode<TKey,TValue>deletedNode, NodesArena<TKey, TValue> winner) {
        throw new UnsupportedOperationException("handleDeletion should not be called in TwoLeftArenaState");
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> doDelete(NodesArena<TKey, TValue> nodesArena, LeafNode<TKey, TValue> deleteNode) {
        throw new UnsupportedOperationException("doDelete should not be called in TwoLeftArenaState, NullBTreeNode should be called");

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
        //targetArena.firstNode.setKey(tKey);
        throw new UnsupportedOperationException("updateIndex should not be called in TwoLeftArenaState, NullBTreeNode should be called");
        //return;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> tryToBorrowNodeFromParentForLeaf(NodesArena<TKey, TValue> parentArena) {
        BTreeNode borrowedNode=parentArena.firstNode;
        LeafNodesArena borrower= (LeafNodesArena) ((IndexNodesArena)parentArena).firstChildArena;
        LeafNodesArena lender= (LeafNodesArena) borrower.nextSibling;
        borrower.nextSibling=lender.nextSibling;
        borrower.nextLeafArena=lender.nextLeafArena;
        lender.nextSibling=null;
        borrower.firstNode=lender.firstNode;
        parentArena.firstNode=parentArena.firstNode.next;
        parentArena.nodesNumState= OneNodeState.getInstance();
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
        NodesArena borrower= ((IndexNodesArena)parentArena).firstChildArena;
        NodesArena lender= borrower.nextSibling;
        return lender.nodesNumState.tryBorrowForIndex(lender,borrower,this);
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> doBorrowForIndex(NodesArena lender, NodesArena borrower) {
        NodesArena parentArena=borrower.parentArena;
        BTreeNode borrowedNode=parentArena.firstNode;
        borrower.firstNode=borrowedNode;
        BTreeNode second=borrowedNode.next;
        parentArena.firstNode=lender.firstNode;
        lender.firstNode=lender.firstNode.next;
        parentArena.firstNode.next=second;
        ((IndexNodesArena)borrower).firstChildArena.nextSibling= ((IndexNodesArena)lender).firstChildArena;
        ((IndexNodesArena)lender).firstChildArena=((IndexNodesArena)lender).firstChildArena.nextSibling;
        ((IndexNodesArena)borrower).firstChildArena.nextSibling.parentArena=borrower;
        lender.nodesNumState=OneNodeState.getInstance();
        return borrowedNode;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> tryToBorrowNodeFromParentForIndex(NodesArena<TKey, TValue> parentArena) {
        BTreeNode borrowedNode=parentArena.firstNode;
        IndexNodesArena leftChildArena= (IndexNodesArena) ((IndexNodesArena<TKey, TValue>)parentArena).firstChildArena;
        IndexNodesArena midChildArena= (IndexNodesArena) ((IndexNodesArena<TKey, TValue>)parentArena).firstChildArena.nextSibling;
        BTreeNode oldFirst=midChildArena.firstNode;
        NodesArena childOfBorrower=leftChildArena.firstChildArena;
        NodesArena childOfLender=midChildArena.firstChildArena;
        parentArena.firstNode=borrowedNode.next;
        ((IndexNodesArena<TKey, TValue>)parentArena).firstChildArena=((IndexNodesArena<TKey, TValue>)parentArena).firstChildArena.nextSibling;
        ((IndexNodesArena<TKey, TValue>)parentArena).firstChildArena.firstNode=borrowedNode;
        ((IndexNodesArena<TKey, TValue>)parentArena).firstChildArena.firstNode.next=oldFirst;
        childOfBorrower.nextSibling=childOfLender;
        midChildArena.firstChildArena=childOfBorrower;
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
        throw new UnsupportedOperationException();
    }

    @Override
    public BTreeNode tryToBorrowNodeFromSiblingForLeaf(NodesArena parentArena) {
        NodesArena borrower= ((IndexNodesArena)parentArena).firstChildArena;
        NodesArena lender= borrower.nextSibling;
        return lender.nodesNumState.tryBorrow(lender,borrower,this);
    }

    @Override
    public BTreeNode doBorrow(NodesArena lender, NodesArena borrower) {
        return OneLeftArenaState.getInstance().doBorrow(lender,borrower);
    }
}
