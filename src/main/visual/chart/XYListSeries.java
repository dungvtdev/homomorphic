package visual.chart;


/**
 * Created by dung on 12/12/2016.
 */
public class XYListSeries extends ListSeries{
    public double[] anchorSeries;

    public XYListSeries(String name, double[] series, double[] anchorSeries) {
        super(name, series);
        this.anchorSeries = anchorSeries;
    }
}
