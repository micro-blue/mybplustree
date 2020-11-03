package mybptree.Node.nodesnumstate;


import mybptree.Arena.IndexNodesArena;
import mybptree.Arena.NodesArena;
import mybptree.Arena.arenastate.ArenaState;
import mybptree.Arena.arenastate.Impl.TwoLeftArenaState;
import mybptree.Arena.arenastate.Impl.TwoMidArenaState;
import mybptree.Arena.arenastate.Impl.TwoRightArenaState;
import mybptree.Node.BTreeNode;

public class TwoNodeState implements NodesNumState {
    private static TwoNodeState twoNodeState=new TwoNodeState();
    private TwoNodeState(){}
    public static TwoNodeState getInstance(){
        return twoNodeState;
    }

    @Override
    public <TKey extends Comparable<TKey>> ArenaState getArenaStateOfInsertion(BTreeNode firstNode, TKey tKey) {
        if(firstNode.getKey().compareTo(tKey)>=0){//有2个元素，,且小于最小
            return NodesArena.TWO_LEFT;//策略3
        }else if(firstNode.next.getKey().compareTo(tKey)>=0){//在中间
            return NodesArena.TWO_MID;//策略4
        }else{//大于最大
            return NodesArena.TWO_RIGHT;//策略5
        }
    }

    @Override
    public <TKey extends Comparable<TKey>> ArenaState getArenaStateOfSelection(BTreeNode firstNode, TKey tKey) {
        if(firstNode.getKey().compareTo(tKey)>0){// tKey < firstNode
            return NodesArena.TWO_LEFT;//策略3
        }else if(firstNode.next.getKey().compareTo(tKey)>0){// firstNode<=tKey<secondNode
            return NodesArena.TWO_MID;//策略4
        }else{// secondNode<=tKey
            return NodesArena.TWO_RIGHT;//策略5
        }
    }

    @Override
    public <TKey extends Comparable<TKey>> ArenaState getArenaStateOfDeletion(BTreeNode firstNode, TKey tKey) {
        return null;
    }

    @Override
    public ArenaState getArenaStateOfZeroNode(NodesArena nodesArena, NodesArena parentArena) {
        if(((IndexNodesArena)parentArena).firstChildArena==nodesArena)
            return TwoLeftArenaState.getInstance();
        return ((IndexNodesArena)parentArena).firstChildArena.nextSibling==nodesArena? TwoMidArenaState.getInstance(): TwoRightArenaState.getInstance();
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> tryBorrowForIndex(NodesArena lender, NodesArena borrower, ArenaState arenaState) {
        return arenaState.doBorrowForIndex(lender,borrower);
    }

    @Override
    public BTreeNode tryBorrow(NodesArena lender, NodesArena borrower, ArenaState arenaStateOfLenderAndBorrow) {
        //todo  向另一个分支借，如果另一个分支也是单节点，则返回failed， 否则 执行借成功的逻辑， 借失败的逻辑是：先把parent从两节点删为一节点，或一节点删除为零节点， 如果删除后是零节点，则递归继续执行delete ，走index的删除逻辑
        // 当这个方法被调用时，说明lender必然是二节点的，（单节点会直接返回failed）
        // 此时可以 编写 doBorrow 接口： arenaStateOfLenderAndBorrow.doBorrow(lender,borrower); 在doBorrow逻辑里只要处理指针即可，不用关心lender是否为双节点

        return arenaStateOfLenderAndBorrow.doBorrow(lender,borrower);
    }
}
