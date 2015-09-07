import enums.Configurations;
import modules.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        java.util.Date date = new java.util.Date();
        System.out.println(new Timestamp(date.getTime()));


        //To compute Mean
//        InputReaderService ir = new InputReaderService();
//        try {
//            ir.computeGlobalMean();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        //To calculate correlation

        CorrelationCalculator crObj = new CorrelationCalculator();
        //crObj.createCorrelationMatrix();
        crObj.createCorrelationMatrixNew();


        date = new java.util.Date();
        System.out.println(new Timestamp(date.getTime()));




    }
}