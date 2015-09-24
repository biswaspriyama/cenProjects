package modules;


import enums.Configurations;

import java.io.FileInputStream;

import java.io.*;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by yugarsi on 8/27/15.
 */
public class InputReaderService {

    public static int actualDataSize = 0;

    //function to read on 32 bits little endian floating number from data

    private static float read32bits(DataInputStream fp) throws IOException {

        byte[] binData = new byte[4];
        for(int i=0;i<4;i++)
            binData[i] = fp.readByte();

        float number = ByteBuffer.wrap(binData).order(java.nio.ByteOrder.LITTLE_ENDIAN).getFloat();
        return number;
    }

    //function to read entire binary file and returning it as an arrayList of Floats

    public static float[] parseBinary(String fileName) throws IOException {
        FileInputStream fObj = new FileInputStream(fileName);
        DataInputStream inputFile = new DataInputStream(fObj);
        ArrayList<Float> outArrayList = new ArrayList<Float>();
        boolean eof = false;
        int count = 0;

        try {
            while (!eof){
                float number = read32bits(inputFile);
                if(number != 168.0 && number != 157.0) {
                    //System.out.print(number+",");
                    outArrayList.add(number);
                    count++;
                }

            }
        } catch (IOException e) {
            eof = true;
        }

        actualDataSize = count;
        //System.out.print("\n"+count+",");
        inputFile.close();
        float[] outArray = new float[count];
        count=0;
        for (Float f : outArrayList) {
            Float data = new Float(f.floatValue());
            outArray[count++] = (data != null ? data : Float.NaN); // Or whatever default you want.
        }
        return outArray;


    }

    //function to get all file names

    public String[] getAllFileNames(){

        String[] fileNames = new String[Configurations.timeSeries];
        int fileNo = 0;
        DecimalFormat formatter = new DecimalFormat("00");
        for (int x = Configurations.startYear; x <= Configurations.endYear; x++) {
            for (int week = 1; week <= Configurations.numWeeks; week++) {
                fileNames[fileNo] = Configurations.Files + Integer.toString(x) + "/"+Configurations.fileNamePattern + formatter.format(week) + "y" + Integer.toString(x) + "+landmask";
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
            float[] dataList = parseBinary(fileName);
            dynamicSize = dataList.length;
            for (int i = 0; i < dataList.length; i++)
                sum[i] = sum[i] + dataList[i];
        }
        System.out.print("\n"+dynamicSize+"\n");
        FileOutputStream fp = new FileOutputStream(Configurations.meanOutFile);
        DataOutputStream dp = new DataOutputStream(fp);
        float mean;
        for (int i = 0;i< dynamicSize ; i++) {
            mean = sum[i] / Configurations.timeSeries;
            //System.out.print(mean+"\n");
            dp.writeFloat(mean);
        }
    }


    //Function that reads from the saved mean binary file for faster access

    public float[] readMeanFile (String fileName) throws FileNotFoundException {
        FileInputStream fObj = new FileInputStream(fileName);
        DataInputStream iObj = new DataInputStream(fObj);
        ArrayList<Float> meanValues = new ArrayList<Float>();

        boolean eof = false;
        try {
            while (!eof){
                float number = iObj.readFloat();
                meanValues.add(number);
            }
        } catch (IOException e) {
            eof = true;
        }
        float[] meanValuesArr = new float[meanValues.size()];  //Converting into float array from list
        int count=0;
        for (Float f : meanValues) {
            Float data = new Float(f.floatValue());
            meanValuesArr[count++] = (data != null ? data : Float.NaN);
        }

        try {
            iObj.close();
            fObj.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return meanValuesArr;
    }

}

