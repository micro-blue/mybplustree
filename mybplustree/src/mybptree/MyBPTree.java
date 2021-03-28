package mybptree;


import mybptree.Arena.*;
import mybptree.Node.AbstractLeafNode;
import mybptree.Node.BTreeNode;
import mybptree.Node.LeafNode;

public class MyBPTree<TKey extends Comparable<TKey>, TValue> {
    public NodesArena<TKey,TValue> root;
    private long size;
    public MyBPTree() {
        this.root = new NullLeafNodesArena(this);
    }
    public TValue get(TKey tKey){
        return root.get(tKey);
    }
    public void add(TKey tKey, TValue tValue) {
        root.insert(tKey, tValue);
        size++;
    }
    public boolean contains(TKey tKey){
        return root.get(tKey)!=null;
    }
    public TValue delete(TKey tKey){
        BTreeNode oldValue=  root.delete(tKey, NullNodesArena.getInstance());
        if(oldValue!=null){
            size--;
            return (TValue) ((AbstractLeafNode)oldValue).gettValue();
        }
        return null;
    }
    public long size(){
        return size;
    }
    public static void main(String[] args) {
        MyBPTree tree=new MyBPTree<>();
        /*for (int i = 1000; i >500 ; i--) {
            tree.add(i,i);
        }
        for (int i = 800; i<1500 ; i++) {
            tree.add(i,i);
        }

        for (int i = 1300; i >600 ; i--) {
           System.out.println(tree.delete(i));
        }
        for (int i = 1400; i <1450 ; i++) {
           // System.out.println(i);
            System.out.println(tree.delete(i));
        }*/
        for (int i = 0; i <100 ; i++) {
            tree.add(i,null);
        }
        for (int i = 30; i <60 ; i++) {
            tree.delete(i);
        }


        System.out.println("====222======");
       Iterator<Integer,Object>iterator=new Iterator<>(tree);
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }
        System.out.println("");
    }
}

class Iterator<TKey extends Comparable<TKey>, TValue>{
   MyBPTree tree;
   LeafNodesArena<TKey,TValue> cur;
   LeafNode<TKey,TValue> curLeafNode;
   public Iterator(MyBPTree tree) {
       this.tree = tree;
       if(tree.root instanceof LeafNodesArena){
           LeafNodesArena tmp= (LeafNodesArena) tree.root;
           curLeafNode= (LeafNode) tmp.firstNode;
       }else{
           IndexNodesArena tmp= (IndexNodesArena) tree.root; //todo 在只有两个以下元素时 是LeafNodesArena
           while(!(tmp.firstChildArena instanceof LeafNodesArena) ){
               tmp= (IndexNodesArena) tmp.firstChildArena;
           }
           cur= (LeafNodesArena) tmp.firstChildArena;
           curLeafNode= (LeafNode) cur.firstNode;
       }
   }
   public boolean hasNext(){
       return curLeafNode!=null||cur!=null;
   }
   public TKey next(){
       LeafNode<TKey,TValue>tmp=curLeafNode;
       if(curLeafNode.next!=null){
           curLeafNode= (LeafNode) curLeafNode.next;
       }else if(cur!=null){
               cur=cur.nextLeafArena;
               if(cur!=null){
                   curLeafNode=(LeafNode<TKey, TValue>) cur.firstNode;
               }else {
                   curLeafNode=null;
               }

       }else{
           curLeafNode=null;
       }
       return tmp.getKey();
   }
}
