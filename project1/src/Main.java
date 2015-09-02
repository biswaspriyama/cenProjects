import enums.Configurations;
import modules.*;
import java.io.IOException;
import java.sql.Timestamp;

public class Main {

    public static void main(String[] args) {
        java.util.Date date = new java.util.Date();
        System.out.println(new Timestamp(date.getTime()));

        CorrelationCalculator crObj = new CorrelationCalculator();
        crObj.createCorrelationMatrix();

        date = new java.util.Date();
        System.out.println(new Timestamp(date.getTime()));



    }
}