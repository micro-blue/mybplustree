package mybptree.Arena.arenastate.Impl;

import mybptree.Arena.*;
import mybptree.Arena.arenastate.ArenaState;
import mybptree.Node.BTreeNode;
import mybptree.Node.IndexNode;
import mybptree.Node.LeafNode;
import mybptree.Node.NullLeafNode;
import mybptree.Node.nodesnumstate.NodesNumState;
import mybptree.Node.nodesnumstate.OneNodeState;
import mybptree.Node.nodesnumstate.TwoNodeState;

public class TwoMidArenaState implements ArenaState {
    private static TwoMidArenaState twoMidArenaState=new TwoMidArenaState();
    private TwoMidArenaState(){}
    public static ArenaState getInstance(){
        return twoMidArenaState;
    }
    @Override
    public BTreeNode handleInsertion(NodesArena nodesArena, BTreeNode insertNode) {
        BTreeNode lastNode=nodesArena.firstNode.next;
        nodesArena.firstNode.next=insertNode;
        insertNode.next=lastNode;
        return new IndexNode(insertNode.getKey());
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> handleDeletion(NodesArena<TKey, TValue> nodesArena, BTreeNode<TKey,TValue>deletedNode, NodesArena<TKey, TValue> winner) {
        winner.updateIndex(nodesArena.firstNode.next.getKey());//修改对应的index, twoleft 和tworight 都不会调用updateIndex
        deletedNode.selfDelete(nodesArena,this);
        return NullLeafNode.getInstance();
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
        NodesArena borrower= ((IndexNodesArena)parentArena).firstChildArena.nextSibling;
        NodesArena lender= ((IndexNodesArena)parentArena).firstChildArena;
        NodesNumState numState=lender.nodesNumState;
        BTreeNode borrowedNode= numState.tryBorrowForIndex(lender,borrower,this);
        if(borrowedNode == null){
            lender=borrower.nextSibling;
            numState=lender.nodesNumState;
            return numState.tryBorrowForIndex(lender,borrower,this);
        }
        return borrowedNode;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> doBorrowForIndex(NodesArena lender, NodesArena borrower) {
        NodesArena parentArena=borrower.parentArena;
        BTreeNode borrowedNode;
        if(lender.nextSibling==borrower){
            borrowedNode=parentArena.firstNode;
            borrower.firstNode=borrowedNode;
            BTreeNode second=parentArena.firstNode.next;
            parentArena.firstNode=lender.firstNode.next;
            parentArena.firstNode.next=second;
            lender.firstNode.next=null;
            NodesArena oldFirst=((IndexNodesArena)borrower).firstChildArena;
            ((IndexNodesArena)borrower).firstChildArena=((IndexNodesArena)lender).firstChildArena.nextSibling.nextSibling;
            ((IndexNodesArena)borrower).firstChildArena.nextSibling=oldFirst;
            ((IndexNodesArena)lender).firstChildArena.nextSibling.nextSibling=null;
            ((IndexNodesArena)borrower).firstChildArena.parentArena=borrower;
        }else{
            borrowedNode=parentArena.firstNode.next;
            borrower.firstNode=borrowedNode;
            parentArena.firstNode.next=lender.firstNode;
            lender.firstNode=lender.firstNode.next;
            ((IndexNodesArena)lender).firstChildArena.parentArena=borrower;
            ((IndexNodesArena)borrower).firstChildArena.nextSibling=((IndexNodesArena)lender).firstChildArena;
            ((IndexNodesArena)lender).firstChildArena=((IndexNodesArena)lender).firstChildArena.nextSibling;
        }
        lender.nodesNumState= OneNodeState.getInstance();
        return borrowedNode;
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> tryToBorrowNodeFromParentForIndex(NodesArena<TKey, TValue> parentArena) {
        BTreeNode borrowedNode=parentArena.firstNode;
        IndexNodesArena leftChildArena= (IndexNodesArena) ((IndexNodesArena<TKey, TValue>)parentArena).firstChildArena;
        IndexNodesArena midChildArena= (IndexNodesArena) ((IndexNodesArena<TKey, TValue>)parentArena).firstChildArena.nextSibling;
        NodesArena childOfBorrower=((IndexNodesArena)leftChildArena.nextSibling).firstChildArena;
        leftChildArena.firstNode.next=borrowedNode;
        leftChildArena.firstChildArena.nextSibling.nextSibling=childOfBorrower;
        parentArena.firstNode=parentArena.firstNode.next;
        leftChildArena.nextSibling=midChildArena.nextSibling;
        childOfBorrower.parentArena=leftChildArena;
        parentArena.nodesNumState=OneNodeState.getInstance();
        leftChildArena.nodesNumState= TwoNodeState.getInstance();
        borrowedNode.next=null;
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
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> doDelete(NodesArena<TKey, TValue> nodesArena, LeafNode<TKey, TValue> deleteNode) {
        nodesArena.firstNode=deleteNode.next;
        deleteNode.next=null;
        nodesArena.nodesNumState= OneNodeState.getInstance();
        return deleteNode;
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
    public <TKey extends Comparable<TKey>, TValue> void updateIndex(TKey tKey, NodesArena<TKey, TValue> targetArena) {
        targetArena.firstNode.setKey(tKey);
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> tryToBorrowNodeFromParentForLeaf(NodesArena<TKey, TValue> parentArena) {
        BTreeNode borrowedNode=parentArena.firstNode.next;
        LeafNodesArena borrower= (LeafNodesArena) ((IndexNodesArena)parentArena).firstChildArena.nextSibling;
        LeafNodesArena lender= (LeafNodesArena) ((IndexNodesArena)parentArena).firstChildArena;
      //  borrower.firstNode=null;
        /*lender.parentArena=null;
        lender.nextLeafArena=null;
        lender.nextSibling=null;*/
        parentArena.firstNode=parentArena.firstNode.next;
        lender.nextSibling=borrower.nextSibling;
        lender.nextLeafArena=borrower.nextLeafArena;
        parentArena.nodesNumState=OneNodeState.getInstance();
        return borrowedNode;
    }



    @Override
    public BTreeNode tryToBorrowNodeFromSiblingForLeaf(NodesArena parentArena) {
        //  TwoMid 比较特殊， 必须要要写一个if 来规定优先级 关系， 要求mid 先借left 如果 判断返回的是 failed对象，就再向right借
        NodesArena borrower= ((IndexNodesArena)parentArena).firstChildArena.nextSibling;
        NodesArena lender= ((IndexNodesArena)parentArena).firstChildArena;
        NodesNumState numState=lender.nodesNumState;
        BTreeNode borrowedNode= numState.tryBorrow(lender,borrower,this);
        if(borrowedNode == null){
            lender=borrower.nextSibling;
            numState=lender.nodesNumState;
            return numState.tryBorrow(lender,borrower,this);
        }
        return borrowedNode;
    }

    @Override
    public BTreeNode doBorrow(NodesArena lender, NodesArena borrower) {
        BTreeNode borrowedNode;
        if(lender.nextSibling==borrower){
            borrowedNode=lender.firstNode.next;
            lender.parentArena.firstNode.setKey(borrowedNode.getKey());
            lender.firstNode.next=null;
            borrower.firstNode=borrowedNode;
        }else{
            borrowedNode=lender.firstNode;
            lender.parentArena.firstNode.setKey(borrowedNode.getKey());
            lender.parentArena.firstNode.next.setKey(lender.firstNode.next.getKey());
            lender.firstNode=lender.firstNode.next;
            borrower.firstNode=borrowedNode;
            borrower.firstNode.next=null;
        }
        lender.nodesNumState=OneNodeState.getInstance();
        return borrowedNode;
        //return lender.nextSibling==borrower?OneRightArenaState.getInstance().doBorrow(lender,borrower):OneLeftArenaState.getInstance().doBorrow(borrower,lender);
    }
}
