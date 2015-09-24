package enums;

/**
 * Created by yugarsi on 8/27/15.
 */
public interface Configurations {

    String Files = "/Users/yugarsi/git-local/cenProjects/largeData/";
    String meanOutFile = "/Users/yugarsi/git-local/cenProjects/largeData/mean.bin";
    String fileNamePattern = "diffw";
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


}

//smalldata param.
//int areaDimension = 3969;
//int actualDatasize = 3186;

//largeData Param
//int areaDimension = 136192;
//int actualDatasize = 66129;
