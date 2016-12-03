package visual.chart;

import java.util.List;

/**
 * Created by dung on 01/12/2016.
 */
public class ListSeries extends Series {
    public double[] series;
    private double max;
    private double min;

    public ListSeries(String name, double[] series){
        super(name);
        this.series = series;
        max = min = series[0];
        for(double i : series){
            if(max < i) max = i;
            if(min > i) min = i;
        }
    }

    @Override
    public SeriesLabels getSeriesLabels(int maxLength) {
        RangeSeries range = new RangeSeries(this.label, min, max);
        return range.getSeriesLabels(maxLength);
    }

    @Override
    public double getMax() {
        return max;
    }

    @Override
    public double getMin() {
        return min;
    }

    public double[] getSeries(){
        return this.series;
    }


}
