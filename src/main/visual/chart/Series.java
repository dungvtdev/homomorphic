package visual.chart;

/**
 * Created by dung on 01/12/2016.
 */
public abstract class Series {
    public String label;

    public Series(String label){
        this.label = label;
    }

    public abstract SeriesLabels getSeriesLabels(int maxLength);

    public class SeriesLabels{
        public double[] series;
        public String[] labels;

        public SeriesLabels(String[] labels, double[] series){
            this.labels = labels;
            this.series = series;
        }
    }

    public abstract double getMax();
    public abstract double getMin();
}
