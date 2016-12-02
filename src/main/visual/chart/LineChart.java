package visual.chart;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by dung on 01/12/2016.
 */
public class LineChart {
    private Graphics2D g2d;
    private Dimension size;
    private Series xSeries;
    private Series ySeries;
    private int topGap;

    public LineChart(){}

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

    public void setTopGap(int topGap){
        this.topGap = topGap;
    }

    public void draw(){
        draw(g2d, size, topGap, xSeries, ySeries);
    }

    public void draw(Graphics2D g2d, Dimension size, int topGap,
                     Series xSeries, Series ySeries){

        int maxXLabels = 10;
        int maxYLabels = 10;
        Series.SeriesLabels xLabels = xSeries.getSeriesLabels(maxXLabels);
        Series.SeriesLabels yLabels = ySeries.getSeriesLabels(maxYLabels);

        int sliceHeight = 5;

        Font font = g2d.getFont();
        g2d.setFont(new Font(font.getFontName(), Font.PLAIN, 10));

        FontMetrics metrics = g2d.getFontMetrics();
        int textHeight = metrics.getHeight();

        int borderHeight = textHeight+6;
        int borderWidth = 0;
        if(yLabels.labels[0].length() > yLabels.labels[yLabels.labels.length-1].length()){
            borderWidth = metrics.stringWidth(yLabels.labels[0]);
        }else{
            borderWidth = metrics.stringWidth(yLabels.labels[yLabels.labels.length]);
        }
        int axisNameSize = textHeight+6;

        int width = size.width - 2*borderWidth - axisNameSize;
        int height = size.height - 2*borderHeight - axisNameSize;
        Point origin = new Point(borderWidth+axisNameSize+5, height+borderHeight);

        //draw axis and labels
        g2d.drawRect(origin.x,origin.y-height,width,height);

        // x
        double rootx = xLabels.series[0];
        double v2px = width/(xSeries.getMax()-rootx);

        for(int i=0;i<xLabels.labels.length;i++){
            int x = (int)((xLabels.series[i]-rootx)*v2px+origin.x);
            g2d.drawLine(x,origin.y-height,x, origin.y-height+sliceHeight);
            g2d.drawLine(x,origin.y,x, origin.y-sliceHeight);
            int textWidth = metrics.stringWidth(xLabels.labels[i]);
            g2d.drawString(xLabels.labels[i],x-textWidth/2,origin.y + textHeight+3);
        }

        // y
        double rooty= yLabels.series[0];
        double v2py = (height-topGap)/(ySeries.getMax()-rooty);
        for(int i=0;i<yLabels.labels.length;i++){
            int y = (int)(origin.y - (yLabels.series[i]-rooty)*v2py);
            g2d.drawLine(origin.x+width,y,origin.x+width-sliceHeight, y);
            g2d.drawLine(origin.x,y,origin.x+sliceHeight, y);
            int textWidth = metrics.stringWidth(yLabels.labels[i]);
            g2d.drawString(yLabels.labels[i],origin.x-textWidth-3,y+textHeight/2);
        }

        // draw axis name
        int nameWidth = metrics.stringWidth(xSeries.label);
        g2d.drawString(xSeries.label,origin.x+(width-nameWidth)/2, origin.y+borderHeight+textHeight/2+5);

        nameWidth = metrics.stringWidth(ySeries.label);
        AffineTransform ori_at = g2d.getTransform();
        AffineTransform at = new AffineTransform();
        at.setToRotation(-Math.PI / 2.0, 0, 0);
        g2d.setTransform(at);
        g2d.drawString(ySeries.label, -height/2 - borderHeight-nameWidth/2, axisNameSize);
        g2d.setTransform(ori_at);

        g2d.setFont(font);
    }
}
