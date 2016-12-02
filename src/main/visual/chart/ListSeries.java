package visual.chart;

import java.util.List;

/**
 * Created by dung on 01/12/2016.
 */
public class ListSeries extends Series {
    public double[] series;

    public ListSeries(String name, double[] series){
        super(name);
        this.series = series;
    }

    @Override
    public SeriesLabels getSeriesLabels(int maxLength) {
        return null;
    }

    @Override
    public double getMax() {
        return 0;
    }
}
