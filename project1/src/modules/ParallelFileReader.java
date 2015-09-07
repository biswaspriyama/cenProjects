package modules;
import enums.Configurations;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by yugarsi on 8/31/15.
 */
public class ParallelFileReader implements Runnable {
    String FileName = new String();
    static InputReaderService readerObj = new InputReaderService();
    public static ArrayList<float[]> completeData = new ArrayList<float[]>();

    public ParallelFileReader() {
        float[] data = null;
    }
    public ParallelFileReader(String FileName) {
        this.FileName = FileName;
    }

    public void run() {
        try{
            float[] data = readerObj.parseBinary(this.FileName);
            completeData.add(data);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<float[]> LoadEntireDataNew() {

        InputReaderService readerObj = new InputReaderService();
        String[] fileNames = readerObj.getAllFileNames();
        int count = 0;
        int i;
        ArrayList<Thread> threadList = new ArrayList<>();
        for ( i=0;i<fileNames.length;i++) {
            ParallelFileReader pRead = new ParallelFileReader(fileNames[i]);
            threadList.add(new Thread(pRead));
            count++;
            if (count == Configurations.threadLimit || i == fileNames.length-1){
                System.out.print("\n" + count + "\n");
                for (Thread thread : threadList)
                    thread.start();
                for (Thread thread : threadList)
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        System.out.print("I am here");
                    }
                threadList = new ArrayList<>();
                count = 0;
            }
        }


        while (completeData.size() != Configurations.timeSeries) {
            try {
                System.out.print("\n" + completeData.size()+ "\n");
                System.out.print("\n"+"the entire data is not loaded in memory"+ "\n");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return completeData;
    }




}
