package modules;
import enums.Configurations;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by yugarsi on 9/6/15.
 */
public class ParallelFileReader implements Runnable{
    String FileName = new String();
    static InputReaderService readerObj = new InputReaderService();
    public static ArrayList<float[]> completeData = new ArrayList<float[]>();
    public int Count=0;

    public ParallelFileReader() {
        for(int i=0;i < Configurations.actualDatasize;i++){
            float[] data = new float[Configurations.timeSeries];
            completeData.add(data);
        }
    }
    public ParallelFileReader(String FileName, int count) {
        this.FileName = FileName;
        this.Count = count;
    }

    public void run() {
        try {
            float[] data = readerObj.parseBinary(this.FileName);
            synchronized (this) {
                for (int i = 0; i < data.length; i++) {
                    completeData.get(i)[this.Count] = data[i];

                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public ArrayList<float[]> LoadEntireData() {

        InputReaderService readerObj = new InputReaderService();
        String[] fileNames = readerObj.getAllFileNames(Configurations.weekLag);
        int count = 0;
        ArrayList<Thread> threadList = new ArrayList<>();

        for (int i=0;i <fileNames.length; i++) {
            ParallelFileReader pRead = new ParallelFileReader(fileNames[i], i);
            threadList.add(new Thread(pRead));
            count++;
            if (count == Configurations.threadLimit || i == fileNames.length-1){
                System.out.print("\n" + count);

                for (Thread thread : threadList)
                    thread.start();
                for (Thread thread : threadList)
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                threadList = new ArrayList<>();
                count = 0;
            }
        }

//        for(int i=0;i<50000;i++)
//            System.out.print(completeData.get(i)[0] + ",");
//        System.out.print("\n");
//        for(int i=0;i<50000;i++)
//            System.out.print(completeData.get(i)[1] + ",");


        java.util.Date date = new java.util.Date();
        System.out.println(new Timestamp(date.getTime()));
        return completeData;
    }



}
