package mybptree.Node;


import mybptree.Arena.NodesArena;

public class IndexNode<TKey extends Comparable<TKey>,TValue> extends BTreeNode<TKey,TValue> {
    public IndexNode(TKey key) {
        this.setKey(key);
    }
    // todo
    //  1. （此时已确定是单节点）借邻居arena节点 ，借到则更新对应index ，结束
    //  2. （此时已确定邻居也是单节点）若仍借不到，发生pullDown，将父节点从二节点变成一节点，更新index为被减少（被pulldown）的节点，结束
    //  3, （此时已确定父节点也是单节点）若父节点是单节点，则递归到步骤1，发生进一步pulldown
    @Override
    public void handleUnderFlow(NodesArena nodesArena) {
        nodesArena.handleUnderFlow(this);
    }
}
