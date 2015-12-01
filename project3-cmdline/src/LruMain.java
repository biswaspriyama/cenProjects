

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by yugarsi on 11/27/15.
 */

class CacheNode{
    int pageKey;
    CacheNode prev;
    CacheNode next;

    public CacheNode(int pageKey){
        this.pageKey = pageKey;
        //this.value = value;
    }
}

class LruCache {
    int cacheSize;
    CacheNode start=null;
    CacheNode end=null;
    HashMap<Integer, CacheNode> cacheMap = new HashMap<Integer, CacheNode>();
    public LruCache(int size) {   //constructor to set LRU Cache Size
        cacheSize = size;
    }
    public int getHit(int key){
        if(cacheMap.containsKey(key)){
            CacheNode existing = cacheMap.get(key);
            removeElement(existing);
            setStart(existing);
            return 1;
        }
        else{

            CacheNode cacheElement = new CacheNode(key);
            if(cacheMap.size()>=cacheSize){
                removeLru();
                setStart(cacheElement);

            }
            else{
                setStart(cacheElement);
            }

            cacheMap.put(key, cacheElement);
            return 0;
        }
    }
    public void removeLru(){
        cacheMap.remove(end.pageKey);
        removeElement(end);

    }
    public void removeElement(CacheNode node){
        if(node.prev!=null){
            node.prev.next = node.next;
        }
        else{
            start = node.next;
        }

        if(node.next!=null){
            node.next.prev = node.prev;
        }
        else{
            end = node.prev;
        }

    }

    public void setStart(CacheNode node){
        node.prev = null;
        node.next = start;

        if(start!=null)
            start.prev = node;

        start = node;
        if(end == null)
            end = start;
    }

    public void getCurrentSize(){
        System.out.print(cacheMap.size());
    }


}

public  class LruMain {


    public static double roundRatio(double number){
        double res = (double)Math.round(number * 100d) / 100d;
        return res;
    }
    public static void computeLruHitRatio(String fileName, int cacheSize){
        String line = null;
        LruCache lruObj = new LruCache(cacheSize);
        int hits = 0;
        int miss = 0;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                int start = Integer.parseInt(line.split(" ")[0]);
                int limit = Integer.parseInt(line.split(" ")[1]);
                for (int page=start;page<start+limit;page++){
                    //System.out.print(page + ",");
                    int ret = lruObj.getHit(page);
                    if (ret == 1)
                        hits++;
                    else
                        miss++;

                }


            }


            bufferedReader.close();

        }catch (FileNotFoundException e){
            System.out.print("File not found");
        }catch (IOException e){
            System.out.print("Error reading File");
        }

//        System.out.print(hits+":"+miss+"\n");
        double ratio = (double)hits/(double)(hits+miss)*100;

        System.out.print(ratio+"\n");
    }



    //MAIN STARTS HERE
    public static void main(String args[]){
        String file = args[0];
        String size = args [1];
        int c = Integer.parseInt(size);
        computeLruHitRatio(file, c);



    }



}
