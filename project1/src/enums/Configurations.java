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
    int endYear = 1980;
    int numWeeks = 52;
    int areaDimension = 136192;
    int actualDatasize = 66129;
    int timeSeries = (endYear-startYear+1)*numWeeks;
    double minCorrelationCoeff=0.9;
    double maxCorrelationCoeff=0.95;
    int threadLimit=1;
    boolean FirstRun = false;
    boolean writeToDb = true;
    int weekLag = 4;


}

//smalldata param.
//int areaDimension = 3969;
//int actualDatasize = 3186;

//largeData Param
//int areaDimension = 136192;
//int actualDatasize = 66129;
// tableNames = "1weeklagMin","1weeklagMax"