import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by yugarsi on 11/27/15.
 */

class Node{
    int pageKey;
    Node prev;
    Node next;
    public Node(int pageKey){
        this.pageKey = pageKey;
    }
}

class ArcDataStructure {


    Node start=null;
    Node end=null;
    private HashMap<Integer, Node> cacheMap = new HashMap<Integer, Node>();

    public Node createElement(int pageNo){
        Node cacheElement = new Node(pageNo);
        return cacheElement;
    }

    public void removeLru(){
        try {
            deleteKey(end.pageKey);
            removeNode(end);

        }catch (NullPointerException e){
            //System.out.print("null ptr");
        }
    }

    public void removeNode(Node node){

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

    public void setStart(Node node){

        node.prev = null;
        node.next = start;

        if(start!=null)
            start.prev = node;

        start = node;
        if(end == null)
            end = start;
    }

    public int getSize(){
        return cacheMap.size();
    }

    public boolean contains(int pageNo){
        return cacheMap.containsKey(pageNo);
    }

    public Node getNode(int pageNo){
        return cacheMap.get(pageNo);
    }

    public void deleteKey(int pageNo){
        cacheMap.remove(pageNo);
    }

    public void addDictEntry(int pageNo , Node n){
        cacheMap.put(pageNo, n);
    }

    public void printCacheElements(){
        Node temp = start;
        while (temp != null){
            System.out.print(temp.pageKey+"->");
            temp = temp.next;
        }

    }

}

class ArcLibCache {
    int c;
    double p;
    ArcDataStructure T1,T2,B1,B2;
    double delta1;
    double delta2;

    public ArcLibCache(int c){
        this.c = c;
        p = 0;
        T1= new ArcDataStructure();
        T2= new ArcDataStructure();
        B1= new ArcDataStructure();
        B2= new ArcDataStructure();
    }

    private void replace(int pageKey, double p) {
        if ((T1.getSize()>0) && ((T1.getSize() > p)||(B2.contains(pageKey) && (T1.getSize() == p)))) {
            Node node = T1.end;
            int lruPage = T1.end.pageKey;
            T1.removeLru();
            B1.setStart(node);
            B1.addDictEntry(lruPage, node);
        } else {
            Node node = T2.end;
            int lruPage = T2.end.pageKey;
            T2.removeLru();
            B2.setStart(node);
            B2.addDictEntry(lruPage, node);
        }
    }

    public int getHit(int pageKey) {
        if (T1.contains(pageKey)) {
            Node existing = T1.getNode(pageKey);
            existing.pageKey = pageKey;
            T1.removeNode(existing);
            T1.deleteKey(pageKey);
            T2.addDictEntry(pageKey, existing);
            T2.setStart(existing);

            return 1;

        } else if (T2.contains(pageKey)) {
            Node existing = T2.getNode(pageKey);
            existing.pageKey = pageKey;
            T2.removeNode(existing);
            T2.setStart(existing);
            return 1;
        } else if (B1.contains(pageKey)) {
            Node existing = B1.getNode(pageKey);
            if (B1.getSize() >= B2.getSize()) {
                delta1 = 1;
            } else {
                if (B1.getSize() != 0) {
                    delta1 = B2.getSize() / B1.getSize();
                }
            }
            p = Math.min(p + delta1, c);
            replace(pageKey, p);
            B1.deleteKey(pageKey);
            B1.removeNode(existing);
            T2.setStart(existing);
            T2.addDictEntry(pageKey, existing);
            return 0;

        } else if (B2.contains(pageKey)) {
            Node existing = B2.getNode(pageKey);
            if (B2.getSize() >= B1.getSize()) {
                delta2 = 1;
            } else {
                if (B2.getSize() != 0) {
                    delta2 = B1.getSize() / B2.getSize();
                }
            }
            p = Math.max(p - delta2, 0);
            replace(pageKey, p);
            B2.deleteKey(pageKey);
            B2.removeNode(existing);
            T2.setStart(existing);
            T2.addDictEntry(pageKey, existing);
            return 0;
        } else {
            Node cacheElement = new Node(pageKey);
            if (T1.getSize() + B1.getSize() == c) {
                if (T1.getSize() < c) {
                    B1.removeLru();
                    replace(pageKey, p);
                } else {
                    T1.deleteKey(T1.end.pageKey);
                    T1.removeNode(T1.end);
                }
            } else if (T1.getSize() + B1.getSize() < c) {
                if (T1.getSize()  + T2.getSize() + B1.getSize()+ B2.getSize() >= c) {
                    if (T1.getSize() + B1.getSize() + T2.getSize() + B2.getSize() == 2 * c) {
                        B2.removeLru();
                    }
                    replace(pageKey, p);
                }
            }
            T1.addDictEntry(pageKey, cacheElement);
            T1.setStart(cacheElement);

            return 0;
        }
    }



}


public class ArcMain {

    public static double roundRatio(double number){
        double res = (double)Math.round(number * 100d) / 100d;
        return res;
    }

    public static void computeArcHitRatio(String fileName, int cacheSize){
        int hits = 0;
        int miss = 0;
        String line = null;
        ArcLibCache arcObj = new ArcLibCache(cacheSize);


        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                int start = Integer.parseInt(line.split(" ")[0]);
                int limit = Integer.parseInt(line.split(" ")[1]);

                for (int page=start;page<start+limit;page++){
                    int ret = arcObj.getHit(page);
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
        double ratio = (double)hits/(double)(hits+miss)*100;
        //ratio = roundRatio(ratio);
        System.out.print(ratio+"\n");

    }


    //MAIN STARTS HERE
    public static void main(String args[]){

            String file = args[0];
            String size = args [1];
            int c = Integer.parseInt(size);
            computeArcHitRatio(file, c);

       // String file = "/Users/yugarsi/git-local/cenProjects/project3/src/files/p4.lis";
        //computeArcHitRatio(file, 2048);



    }

}
