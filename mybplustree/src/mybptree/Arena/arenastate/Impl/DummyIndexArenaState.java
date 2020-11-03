package mybptree.Arena.arenastate.Impl;


import mybptree.Arena.DummyNodesArena;
import mybptree.Arena.arenastate.ArenaState;

public class DummyIndexArenaState extends AbstractDummyArenaState {
    private static DummyIndexArenaState dummyIndexArenaState=new DummyIndexArenaState();
    private DummyIndexArenaState(){}
    public static ArenaState getInstance(){
        return dummyIndexArenaState;
    }


}
