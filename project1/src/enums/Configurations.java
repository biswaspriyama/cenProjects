package enums;

/**
 * Created by yugarsi on 8/27/15.
 */
public interface Configurations {

    String Files = "/Users/yugarsi/git-local/cenProjects/smallData/";
    String meanOutFile = "/Users/yugarsi/git-local/cenProjects/smallData/mean.bin";
    String fileNamePattern = "Beaufort_Sea_diffw";
    int startYear = 1990;
    int endYear = 2005;
    int numWeeks = 52;
    int areaDimension = 3969;
    int actualDatasize = 3186;
    int timeSeries = (endYear-startYear+1)*numWeeks;
    double minCorrelationCoeff=0.9;
    double maxCorrelationCoeff=0.95;
    int threadLimit=400;
    int cThreadLimit = 1000;


}
