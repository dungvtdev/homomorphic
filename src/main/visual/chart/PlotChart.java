package visual.chart;

import java.awt.*;
import java.util.*;

/**
 * Created by dung on 12/12/2016.
 */
public class PlotChart extends LineChart {

    public PlotChart(Graphics2D g2d) {
        super(g2d);
    }

    @Override
    protected void drawData(Graphics2D g2d, Series xSeries, java.util.List<Series> ySeries, ScaleRange scaleRangeX, ScaleRange scaleRangeY){
        for(Series s : ySeries) {
            if(s instanceof XYListSeries) {
                XYListSeries ys = (XYListSeries) s;
                double[] series = ys.getSeries();
                double[] xseries = ys.anchorSeries;

                int length = series.length;
                int px, py;
                for (int i = 0; i < length; i++) {
                    px = (int) scaleRangeX.scaleValue(xseries[i]);
                    py = (int) scaleRangeY.scaleValue(series[i]);
                    g2d.fillRect(px, py, 3, 3);
                }
            }else {
                ListSeries ys = (ListSeries) s;
                double[] series = ys.getSeries();
                int length = series.length;
                double xVal = xSeries.getMin();
                double xStep = (xSeries.getMax() - xSeries.getMin()) / length;
                int px, py;
                for (int i = 0; i < length; i++) {
                    xVal += xStep;
                    px = (int) scaleRangeX.scaleValue(xVal);
                    py = (int) scaleRangeY.scaleValue(series[i]);
                    g2d.fillRect(px, py, 3, 3);
                }
            }
        }
    }
}
