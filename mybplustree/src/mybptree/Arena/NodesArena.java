package mybptree.Arena;


import mybptree.Arena.arenastate.ArenaState;
import mybptree.Arena.arenastate.Impl.*;
import mybptree.Node.BTreeNode;
import mybptree.Node.GeneralBPNode;
import mybptree.Node.nodesnumstate.NodesNumState;

public abstract class NodesArena<TKey extends Comparable<TKey>,TValue> extends GeneralBPNode<TKey,TValue> {

    public static ArenaState ONE_LEFT = OneLeftArenaState.getInstance();
    public static ArenaState ONE_RIGHT = OneRightArenaState.getInstance();
    public static ArenaState TWO_LEFT = TwoLeftArenaState.getInstance();
    public static ArenaState TWO_MID = TwoMidArenaState.getInstance();
    public static ArenaState TWO_RIGHT = TwoRightArenaState.getInstance();
    public BTreeNode<TKey,TValue> firstNode;
    public NodesArena<TKey,TValue>parentArena;
    public NodesArena<TKey,TValue>nextSibling;
    public NodesNumState nodesNumState;

    public NodesArena(NodesNumState nodesNumState) {
        this.nodesNumState=nodesNumState;
    }


    public void insert(TKey tKey, TValue tValue){throw new UnsupportedOperationException();}
    public  boolean contains(TKey tKey){throw new UnsupportedOperationException();}
    public TValue get(TKey tKey){throw new UnsupportedOperationException();}


    /**
     * 1. 如果删除的节点是一个叶子节点的第二个节点，则其传进来的candidateArena 必为空，这是由之前的插入规则等决定的
     * 2. 如果删除的节点是一个叶子节点的第二个节点，则只需要把它对应的leafnode从leafArena中删除，即只保留第一个leafnode，其余无需改变
     * 3. 如果删除的节点是一个叶子节点的第一个节点，则其传进来的 candidateArena 必不为空
     * 4. 对于3 中情况，如果该叶子节点的next不为空（即是有两个leafnode的arena），那么直接将传进来的 candidateArena 的key值修改为next的key值，并从leafArena中删除该第一个节点，把第二个作为第一个节点
     * 5. 对于3 中情况，如果该叶子节点的next为空（即是只有一个leafnode的arena），那么该节点必然是parentArena的左侧节点，
     *      因为如果在中间或者右侧，由于是只有一个leafnode的arena，根据分区条件，中间节点>=第一节点，右侧节点>=第二节点，
     *      由于只有一个节点，必然是中间节点 = 第一节点，右侧节点 = 第二节点，而不是大于
     *          如果是 这两种 = 的情况，说明 parentArena 有两个节点， 那么“不能”只把要删除的节点及其arena删掉，形成只有一个节点的parentArena，并连接剩余的两个leafArena，
     *              而是要遵循 “尽量不轻易变动父节点的‘节点数’的原则”，也就是说，若其它叶子或孩子节点（可以是前一个sibling，也可以是后一个sibling,如果前后都有，就抓前一个的节点）是两个节点，那么可以把第二个 “抓过来”填充 因为本次删除而变“空”的arena
     *                  （这个原则同时适用于叶子和索引节点，只是具体的表现不太相同，对于索引节点，它的中间和右侧节点是可以“大于”parentArena中相对左侧的节点的）
     * 6. 对于5 中要删除的节点是左侧节点的情况，此时 candidateArena 不为空，必然只能把 parentArena 的第一个节点的key 设置为 candidateArena 的新key， 因为如果把 parentArena 的第二个节点
     *      设置为candidateArena 的新key， 那么如果你想找 parentArena 的第一个节点，会被错误地路由，会找不到，即不符合二分搜索树的性质
     *
     *
     */

    public BTreeNode<TKey,TValue> delete(TKey tKey, NodesArena<TKey, TValue> candidateArena){throw new UnsupportedOperationException();}
    public void handleOverFlow(BTreeNode popUpIndexNode) {
        NodesArena newRight=splitArenaIntoTwoArena();
        NodesArena newLeft=this;
        newRight.nextSibling=newLeft.nextSibling;
        newLeft.nextSibling=newRight;
        newRight.parentArena=this.parentArena;
        //todo parentArena在树的高度为1 时，是null的，可在初始化时采用空对象+ 状态模式 ，但删除时应注意做判断，必要时将parentArena赋值为 原先的空对象
        parentArena.pushUp(popUpIndexNode,newLeft,newRight);
    }
    public void handleUnderFlow(BTreeNode pullDownNode) {
        throw new UnsupportedOperationException();
    }
    protected void pushUp(BTreeNode outNode, NodesArena left, NodesArena right){throw new UnsupportedOperationException();};
    //Called only when overflow occurs
    protected NodesArena splitArenaIntoTwoArena(){throw new UnsupportedOperationException();};

    public NodesArena() {
    }

    protected NodesArena<TKey,TValue> indexCheck(TKey tKey, ArenaState arenaState, NodesArena<TKey, TValue> candidateArena){
        throw new UnsupportedOperationException();
    };

    public void updateIndex(TKey newKey){
        throw new UnsupportedOperationException();
    };

    public ArenaState getArenaStateOfZeroNode(NodesArena nodesArena){
        throw new UnsupportedOperationException();
    };

    public BTreeNode<TKey,TValue> tryToBorrowNodeFromSiblingForLeaf(ArenaState arenaState){
        throw new UnsupportedOperationException();
    };

    public BTreeNode<TKey,TValue> tryToBorrowNodeFromParentForLeaf(ArenaState parentArenaState){throw new UnsupportedOperationException();};

    public  void pullDown(ArenaState parentArenaState){throw new UnsupportedOperationException();};

    public void maintainChildForLeaf(ArenaState parentArenaState){throw new UnsupportedOperationException();};

    public BTreeNode<TKey,TValue> tryToBorrowNodeFromSiblingForIndex(ArenaState arenaState){throw new UnsupportedOperationException();};

    public BTreeNode<TKey,TValue> tryToBorrowNodeFromParentForIndex(ArenaState parentArenaState){throw new UnsupportedOperationException();};

    public void maintainChildForIndex(ArenaState parentArenaState){throw new UnsupportedOperationException();};

    protected ArenaState getDummy(){throw new UnsupportedOperationException();};


}





