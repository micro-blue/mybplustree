package mybptree.Arena.arenastate.Impl;


import mybptree.Arena.DummyNodesArena;
import mybptree.Arena.arenastate.ArenaState;

public class DummyLeafArenaState extends AbstractDummyArenaState {
    private static DummyLeafArenaState dummyLeafArenaState=new DummyLeafArenaState();
    private DummyLeafArenaState(){}
    public static ArenaState getInstance(){
        return dummyLeafArenaState;
    }

    @Override
    public <TValue, TKey extends Comparable<TKey>> void maintainDummyNodesArena(DummyNodesArena<TKey, TValue> dummyNodesArena) {

    }
}
