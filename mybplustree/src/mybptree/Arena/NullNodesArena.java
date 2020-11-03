package mybptree.Arena;


import mybptree.Arena.arenastate.ArenaState;

public class NullNodesArena<TKey extends Comparable<TKey>,TValue> extends NodesArena<TKey,TValue> {
    private static NullNodesArena nullNodesArena=new NullNodesArena();
    private NullNodesArena(){}
    public static NullNodesArena getInstance(){
        return nullNodesArena;
    }
    @Override
    protected NodesArena<TKey, TValue> indexCheck(TKey tKey, ArenaState arenaState, NodesArena<TKey, TValue> candidateArena) {
        return arenaState.checkEqualIndex(tKey,candidateArena);
    }
    @Override
    public void updateIndex(TKey tKey) {
        return;
    }
}
