import visual.chart.RangeSeries;
import visual.chart.Series;

/**
 * Created by dung on 01/12/2016.
 */
public class Testbed {
    public static void main(String[] argv){
        RangeSeries series = new RangeSeries("test", 510, 12422);
//        printSeriesTest(series, 10);
//        printSeriesTest(series, 21);
        printSeriesTest(series, 150);

    }

    static void printSeriesTest(Series series, int max){
        double[] labels = series.getSeriesLabels(max).series;
        System.out.printf("\n%d %d ", max, labels.length);
        for(double l : labels){
            System.out.printf("%.0f ", l);
        }
    }
}
