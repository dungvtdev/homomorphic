package visual.chart;

import javax.swing.*;
import java.awt.*;

/**
 * Created by dung on 01/12/2016.
 */
public class LineChart {
    private Graphics2D g2d;
    private Dimension size;
    private Series xSeries;
    private Series ySeries;

    public LineChart(Graphics2D g2d){
        this.g2d = g2d;
    }

    public void setYSeries(Series series){
        this.ySeries = series;
    }

    public void setXSeriers(Series series){
        this.xSeries = series;
    }

    public void setDrawSize(Dimension size){
        this.size = size;
    }

    public void draw(Dimension borderSize, int axisNameSize){
        int sliceHeight = 3;

        int width = size.width - 2*borderSize.width - axisNameSize;
        int height = size.height - 2*borderSize.height - axisNameSize;
        Point origin = new Point(borderSize.width+axisNameSize, height+borderSize.height);

        int maxXLabels = 10;
        int maxYLabels = 10;

        Series.SeriesLabels xLabels = xSeries.getSeriesLabels(maxXLabels);
        Series.SeriesLabels yLabels = ySeries.getSeriesLabels(maxYLabels);

        //draw axis and labels
        g2d.drawRect(origin.x,origin.y-height,width,height);

        Font font = g2d.getFont();
        g2d.setFont(new Font(font.getFontName(), Font.PLAIN, 10));

        FontMetrics metrics = g2d.getFontMetrics();
        int textHeight = metrics.getHeight();

        // x
        double v2px = width/(xSeries.getMax()-xLabels.series[0]);
        for(int i=0;i<xLabels.labels.length;i++){
            int x = (int)(xLabels.series[i]*v2px+origin.x);
            g2d.drawLine(x,origin.y-height,x, origin.y-height+sliceHeight);
            g2d.drawLine(x,origin.y,x, origin.y-sliceHeight);
            int textWidth = metrics.stringWidth(xLabels.labels[i]);
            g2d.drawString(xLabels.labels[i],x-textWidth/2,origin.y + textHeight+3);
        }

        // y
        double v2py = height/(ySeries.getMax()-yLabels.series[0]);
        for(int i=0;i<yLabels.labels.length;i++){
            int y = (int)(origin.y - yLabels.series[i]*v2py);
            g2d.drawLine(origin.x+width,y,origin.x+width-sliceHeight, y);
            g2d.drawLine(origin.x,y,origin.x+sliceHeight, y);
            int textWidth = metrics.stringWidth(yLabels.labels[i]);
            g2d.drawString(yLabels.labels[i],origin.x-textWidth-3,y+textHeight/2);
        }

        g2d.setFont(font);
    }
}
