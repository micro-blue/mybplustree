package mybptree.Node;


import mybptree.Arena.IndexNodesArena;
import mybptree.Arena.LeafNodesArena;
import mybptree.Arena.NodesArena;
import mybptree.Arena.arenastate.ArenaState;
import mybptree.Arena.arenastate.Impl.OneLeftArenaState;
import mybptree.Arena.arenastate.Impl.TwoLeftArenaState;

public class LeafNode<TKey extends Comparable<TKey>,TValue> extends AbstractLeafNode<TKey,TValue> {
    private TValue tValue;
    public LeafNode(TKey tKey, TValue tValue) {
        super(tKey);
        this.tValue=tValue;
    }
    public TValue gettValue() {
        return tValue;
    }
    public void settValue(TValue tValue) {
        this.tValue = tValue;
    }
    @Override
    public  void selfDelete(NodesArena<TKey, TValue> nodesArena, ArenaState arenaState) {
        arenaState.doDelete(nodesArena,this);
    }
    // todo
    //  0.  借同arena的节点，如果是两节点则能借到，更新对应index ，结束  (该步骤在此方法前已执行完，即若该方法调用时，必然不是同arena内两节点)
    //  1. （此时已确定是单节点）借邻居arena节点 ，借到则更新对应index ，结束
    //  2. （此时已确定邻居也是单节点）若仍借不到，发生pullDown，将父节点从二节点变成一节点，更新index为被减少（被pulldown）的节点，结束
    //  3, （此时已确定父节点也是单节点）若父节点是单节点，则递归到步骤1，发生进一步pulldown
    // todo indexArena和leafArena 在删除上的不同就是前者要注意维护childArena, 后者维护 nextLeafArena
    // called only in leafnodesArena::delete
    @Override
    public void handleUnderFlow(LeafNodesArena<TKey, TValue> nodesArena, NodesArena<TKey, TValue> winner) {
        NodesArena<TKey,TValue>parentArena= nodesArena.parentArena;
        //比如 twoleft 回去借右边的 twomid 会去借左边，左边若没有会去借右边。 tworight 只会去借中间, 即使是 oneleft oneright 也会尝试去借右边或左边的
        ArenaState parentArenaState=parentArena.getArenaStateOfZeroNode(nodesArena);
        BTreeNode<TKey,TValue>borrowedNode =parentArena.tryToBorrowNodeFromSiblingForLeaf(parentArenaState);
        if(borrowedNode==null){//执行步骤2
            borrowedNode = parentArena.tryToBorrowNodeFromParentForLeaf(parentArenaState);
            if(borrowedNode==null){//执行步骤3 发生 pullDown , parentArena.pullDown(); pullDown是除了delete外 唯一一个调用handleUnderFlow的方法， 也是递归下溢的必须方法， 类似pushUp // 说明必然是 1-1-1 情形  可以直接updateWinner (删左孩子需要update， 右孩子则直接置空(因为右孩子说明父节点必然等于右孩子，此时等于直接删除了父节点的索引，直接进入索引删除的迭代即可))
                parentArena.maintainChildForLeaf(parentArenaState);
                if(parentArenaState instanceof OneLeftArenaState)
                    winner.updateIndex((TKey) ((IndexNodesArena)parentArena).firstChildArena.firstNode.getKey());
                parentArena.pullDown(parentArenaState);
            }else if(parentArenaState instanceof TwoLeftArenaState){
                winner.updateIndex(borrowedNode.getKey());
            }
        }else /*if(parentArenaState instanceof OneLeftArenaState||parentArenaState instanceof TwoLeftArenaState)*/{
            winner.updateIndex(borrowedNode.getKey());
        }
    }
}
