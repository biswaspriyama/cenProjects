package enums;

/**
 * Created by yugarsi on 8/27/15.
 */
public interface Configurations {

    String Files = "/Users/yugarsi/git-local/cenProjects/largeData/";
    String meanOutFile = "/Users/yugarsi/git-local/cenProjects/largeData/mean.bin";
    String fileNamePattern = "diffw";
    String sampleFileName = Files+"1979/diffw01y1979+landmask";
    int startYear = 1979;
    int endYear = 2005;
    int numWeeks = 52;
    int areaDimension = 136192;
    int actualDatasize = 66129;
    int timeSeries = (endYear-startYear+1)*numWeeks;
    double minCorrelationCoeff=0.9;
    double maxCorrelationCoeff=0.95;
    int threadLimit=400;
    boolean FirstRun = false;
    boolean writeToDb = true;
    int weekLag = 4;
    String[] tableNames1 = {"ice9years1","ice9years1large","ice9years2","ice9years2large","ice9years3","ice9years3large","Graph_test","iceLargeData1"};
    String[] tableNames2 = {"1weeklag","2weeklag","3weeklag","4weeklag"};


}

//smalldata param.
//int areaDimension = 3969;
//int actualDatasize = 3186;

//largeData Param
//int areaDimension = 136192;
//int actualDatasize = 66129;
