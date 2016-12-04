import algorithm.FFT;
import visual.chart.RangeSeries;
import visual.chart.Series;

/**
 * Created by dung on 01/12/2016.
 */
public class Testbed {
    public static void main(String[] argv){
//        RangeSeries series = new RangeSeries("test", -1510, 1222);
//        printSeriesTest(series, 10);
//        printSeriesTest(series, 21);
//        printSeriesTest(series, 150);
        testFFT();
    }

    static void printSeriesTest(Series series, int max){
        double[] labels = series.getSeriesLabels(max).series;
        System.out.printf("\n%d %d ", max, labels.length);
        for(double l : labels){
            System.out.printf("%.0f ", l);
        }
    }

    static void testFFT(){
        double[][] x = new double[][]{
            {1,2},{3,4},{1,-6},{0,3}, {-6,0}, {9,4},{3,6},{-6,7}
        };
        double[][] fx = FFT.ifft(x);
        for(double[] a:fx){
            System.out.printf("%f+%fj\n",a[0], a[1]);
        }
    }
}
