package enums;

/**
 * Created by yugarsi on 8/27/15.
 */
public interface Configurations {

    String Files = "/Users/yugarsi/git-local/cenProjects/smallData/";
    int startYear = 1990;
    int endYear = 2005;
    int numWeeks = 52;
    int areaDimension = 3969;
    int timeSeries = (endYear-startYear+1)*numWeeks;
    double minCorrelationCoeff=0.9;
    double maxCorrelationCoeff=0.95;
    int threadLimit=832;
    String meanOutFile = "/Users/yugarsi/git-local/cenProjects/smallData/mean.bin";

}
