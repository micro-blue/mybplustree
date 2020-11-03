package mybptree.Node.nodesnumstate;


import mybptree.Arena.IndexNodesArena;
import mybptree.Arena.NodesArena;
import mybptree.Arena.arenastate.ArenaState;
import mybptree.Arena.arenastate.Impl.OneLeftArenaState;
import mybptree.Arena.arenastate.Impl.OneRightArenaState;
import mybptree.Node.BTreeNode;

public class OneNodeState implements NodesNumState {
    private static OneNodeState oneNodeState=new OneNodeState();
    private OneNodeState(){}
    public static OneNodeState getInstance(){
        return oneNodeState;
    }

    @Override
    public <TKey extends Comparable<TKey>> ArenaState getArenaStateOfInsertion(BTreeNode firstNode, TKey tKey) {
        if(firstNode.getKey().compareTo(tKey)>=0){//只有1个元素，,且小于它 //todo 可改成三元运算
            return NodesArena.ONE_LEFT;//策略1
        }else{//只有1个元素，且大于该元素
            return NodesArena.ONE_RIGHT;//策略2
        }
    }

    @Override
    public <TKey extends Comparable<TKey>> ArenaState getArenaStateOfSelection(BTreeNode firstNode, TKey tKey) {
        if(firstNode.getKey().compareTo(tKey)>0){ //tkey< frist
            return NodesArena.ONE_LEFT;//策略1
        }else{ //first<=tkey
            return NodesArena.ONE_RIGHT;//策略2
        }
    }

    @Override
    public <TKey extends Comparable<TKey>> ArenaState getArenaStateOfDeletion(BTreeNode firstNode, TKey tKey) {
        return null;
    }

    @Override
    public ArenaState getArenaStateOfZeroNode(NodesArena nodesArena, NodesArena parentArena) {
        return ((IndexNodesArena)parentArena).firstChildArena==nodesArena? OneLeftArenaState.getInstance() : OneRightArenaState.getInstance();
    }

    @Override
    public <TKey extends Comparable<TKey>, TValue> BTreeNode<TKey, TValue> tryBorrowForIndex(NodesArena lender, NodesArena borrower, ArenaState arenaState) {
        return null;
    }

    @Override
    public BTreeNode tryBorrow(NodesArena lender, NodesArena borrower, ArenaState arenaStateOfLenderAndBorrower) {
        //只有一个节点的话，就借无可借，只能return null
        return null;
    }
}