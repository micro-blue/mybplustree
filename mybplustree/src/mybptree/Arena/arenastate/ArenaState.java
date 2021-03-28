package mybptree.Arena.arenastate;


import mybptree.Arena.DummyNodesArena;
import mybptree.Arena.IndexNodesArena;
import mybptree.Arena.NodesArena;
import mybptree.Node.BTreeNode;
import mybptree.Node.LeafNode;

// 接口不设计泛型，根据调用时方法所规定的泛型决定
public interface ArenaState {
    <TKey extends Comparable<TKey>, TValue> BTreeNode handleInsertion(NodesArena nodesArena, BTreeNode insertNode);

    <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey,TValue> handleDeletion(NodesArena<TKey, TValue> nodesArena, BTreeNode<TKey, TValue> deletedNode, NodesArena<TKey, TValue> winner);

    <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey,TValue> doDelete(NodesArena<TKey, TValue> nodesArena, LeafNode<TKey, TValue> deleteNode);

    <TKey extends Comparable<TKey>, TValue> NodesArena getChildArena(IndexNodesArena<TKey, TValue> nodesArena);

    <TKey extends Comparable<TKey>, TValue> BTreeNode handleSelection(NodesArena<TKey, TValue> nodesArena, TKey tKey);

    <TKey extends Comparable<TKey>, TValue> NodesArena<TKey,TValue> checkEqualIndex(TKey tKey, NodesArena<TKey, TValue> nodesArena);

    <TKey extends Comparable<TKey>, TValue> void updateIndex(TKey tKey, NodesArena<TKey, TValue> targetArena);

    <TKey extends Comparable<TKey>, TValue>BTreeNode<TKey,TValue> tryToBorrowNodeFromSiblingForLeaf(NodesArena parentArena);

    <TKey extends Comparable<TKey>, TValue>BTreeNode<TKey,TValue> doBorrow(NodesArena lender, NodesArena borrower);

    <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey,TValue> tryToBorrowNodeFromParentForLeaf(NodesArena<TKey, TValue> parentArena);

    <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey,TValue> handleDeletion(IndexNodesArena<TKey, TValue> nodesArena, BTreeNode<TKey, TValue> deletedNode);

    <TKey extends Comparable<TKey>, TValue> void maintainChildForLeaf(IndexNodesArena<TKey, TValue> nodesArena);

    <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey,TValue> tryToBorrowNodeFromSiblingForIndex(NodesArena<TKey, TValue> nodesArena);

    <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey,TValue> doBorrowForIndex(NodesArena lender, NodesArena borrower);

    <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey,TValue> tryToBorrowNodeFromParentForIndex(NodesArena<TKey, TValue> parentArena);

    <TKey extends Comparable<TKey>, TValue> void maintainChildForIndex(IndexNodesArena<TKey, TValue> nodesArena);

    <TValue, TKey extends Comparable<TKey>> void maintainDummyNodesArena(DummyNodesArena<TKey, TValue> dummyNodesArena);
}
