package mybptree.Node.nodesnumstate;


import mybptree.Arena.NodesArena;
import mybptree.Arena.arenastate.ArenaState;
import mybptree.Node.BTreeNode;

public interface NodesNumState {
    //插入时按照 小于等于 为左侧
    <TKey extends Comparable<TKey>> ArenaState getArenaStateOfInsertion(BTreeNode firstNode, TKey tKey);
    //查询时按照 大于等于 为右侧
    <TKey extends Comparable<TKey>> ArenaState getArenaStateOfSelection(BTreeNode firstNode, TKey tKey);
    //查询时按照 大于等于 为右侧
    <TKey extends Comparable<TKey>> ArenaState getArenaStateOfDeletion(BTreeNode firstNode, TKey tKey);

    ArenaState getArenaStateOfZeroNode(NodesArena nodesArena, NodesArena parentArena);

    <TKey extends Comparable<TKey>, TValue>BTreeNode<TKey,TValue> tryBorrow(NodesArena lender, NodesArena borrower, ArenaState arenaStateOfLenderAndBorrower);

    <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey,TValue> tryBorrowForIndex(NodesArena lender, NodesArena borrower, ArenaState arenaState);

    //   public abstract <TKey extends Comparable<TKey>> ArenaState getArenaState(BTreeNode firstNode, TKey tKey);
}
