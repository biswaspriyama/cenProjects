package modules;
import enums.Configurations;

import java.io.FileInputStream;

import java.io.IOException;
import java.io.*;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by yugarsi on 8/27/15.
 */
public class ReadInputFiles {

    //function to read on 32 bits little endian floating number from data

    private static float read32bits(DataInputStream fp) throws IOException {

        byte[] binData = new byte[4];
        for(int i=0;i<4;i++)
            binData[i] = fp.readByte();

        float number = ByteBuffer.wrap(binData).order(java.nio.ByteOrder.LITTLE_ENDIAN).getFloat();
        return number;
    }

    //function to read entire binary file and returning it as an arrayList of Floats

    private static ArrayList<Float> parseBinary(String fileName) throws IOException {
        FileInputStream fObj = new FileInputStream(fileName);
        DataInputStream inputFile = new DataInputStream(fObj);
        ArrayList<Float> outArray = new ArrayList<Float>();
        boolean eof = false;
        int i = 0;
        try {
            while (!eof){
                float number = read32bits(inputFile);
                if(number != 168) {
                    outArray.add(number);
                    //System.out.print(number + "\n");
                    i++;
                }
            }
        } catch (IOException e) {
            eof = true;
        }
        System.out.print(i + "\n");
        inputFile.close();
        return outArray;


    }

    //function to get all file names

    public String[] getAllFileNames(){

        String[] fileNames = new String[Configurations.timeSeries];
        int fileNo = 0;
        DecimalFormat formatter = new DecimalFormat("00");
        for (int x = Configurations.startYear; x <= Configurations.endYear; x++) {
            for (int week = 1; week <= Configurations.numWeeks; week++) {
                fileNames[fileNo] = Configurations.Files + Integer.toString(x) + "/Beaufort_Sea_diffw" + formatter.format(week) + "y" + Integer.toString(x) + "+landmask";
                fileNo++;
            }
        }
        return fileNames;
    }

    //Function to compute global mean of entire area and save to binary file

    public void computeGlobalMean() throws IOException {

        float[] sum = new float[Configurations.areaDimension];
        for (int i = 0; i < Configurations.areaDimension; i++)
            sum[i] = 0;

        String[] fileNames = getAllFileNames();
        int dynamicSize = 0;
        for (String fileName : fileNames) {
            ArrayList<Float> dataList = parseBinary(fileName);
            dynamicSize = dataList.size();
            for (int i = 0; i < dataList.size(); i++)
                sum[i] = sum[i] + dataList.get(i);
        }

        FileOutputStream fp = new FileOutputStream(Configurations.meanOutFile);
        DataOutputStream dp = new DataOutputStream(fp);

        for (int i = 0;i< dynamicSize ; i++) {
            dp.writeFloat(sum[i] / Configurations.timeSeries);
        }
    }


    //Function that reads from the saved mean binary file for faster access

    public ArrayList<Float> readMeanFile (String fileName) throws FileNotFoundException {
        FileInputStream fObj = new FileInputStream(fileName);
        DataInputStream iObj = new DataInputStream(fObj);
        ArrayList<Float> meanValues = new ArrayList<Float>();

        boolean eof = false;
        int i = 0;
        try {
            while (!eof){
                float number = iObj.readFloat();
                meanValues.add(number);
                i++;
            }
        } catch (IOException e) {
            eof = true;
        }

//        for(i=0;i<meanValues.size();i++)
//            System.out.print(meanValues.get(i)+"\n");
        return meanValues;
    }

}

