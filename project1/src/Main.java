import enums.Configurations;
import modules.ReadInputFiles;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        ReadInputFiles r1 = new ReadInputFiles();
        try {
            r1.computeGlobalMean();
            r1.readMeanFile(Configurations.meanOutFile);

        }catch (IOException e){
            e.printStackTrace();
        }

    }
}