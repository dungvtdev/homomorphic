package visual.chart;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.security.InvalidParameterException;

/**
 * Created by dung on 01/12/2016.
 */
public class LineChart {
    private Graphics2D g2d;
    private Dimension size;
    private Series xSeries;
    private Series ySeries;
    private int horizontalGap;
    private Color lineColor;

    private ScaleRange xRange;
    private ScaleRange yRange;

    public LineChart(Graphics2D g2d){
        setGraphics2d(g2d);
    }

    public void setGraphics2d(Graphics2D g2d){this.g2d = g2d;}

    public void setYSeries(Series series){
        this.ySeries = series;
    }

    public void setXSeriers(Series series){
        this.xSeries = series;
    }

    public void setDrawSize(Dimension size){
        this.size = size;
    }

    public void setHorizontalGap(int horizontalGap){
        this.horizontalGap = horizontalGap;
    }

    public void setLineColor(Color lineColor){
        this.lineColor = lineColor;
    }


    public void draw(){
        draw(g2d, size, horizontalGap, xSeries, ySeries, lineColor);
    }

    public void draw(Graphics2D g2d, Dimension size, int horizontalGap,
                     Series xSeries, Series ySeries, Color lineColor){

        if(xSeries==null || ySeries==null || size==null ||lineColor==null){
            int width = size.width-40;
            int height = size.height - 40;
            Point origin = new Point(20,20+height);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(origin.x,origin.y-height,width,height);
            return;
        }

        Font font = g2d.getFont();
        g2d.setFont(new Font(font.getFontName(), Font.PLAIN, 10));

        Stroke stroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(1));

        Color oldLineColor = g2d.getColor();

        FontMetrics metrics = g2d.getFontMetrics();




        int maxXLabels = (size.width/metrics.stringWidth("00000"));
        int maxYLabels = (int)(size.height/(1.5f*metrics.getHeight())-3);

        Series.SeriesLabels xLabels = xSeries.getSeriesLabels(maxXLabels);
        Series.SeriesLabels yLabels = ySeries.getSeriesLabels(maxYLabels);

        int sliceHeight = 5;

        int textHeight = metrics.getHeight();

        int borderHeight = textHeight+6;
        int borderWidth = 0;
        if(yLabels.labels[0].length() > yLabels.labels[yLabels.labels.length-1].length()){
            borderWidth = metrics.stringWidth(yLabels.labels[0]);
        }else{
            borderWidth = metrics.stringWidth(yLabels.labels[yLabels.labels.length-1]);
        }
        borderWidth = Math.max(40,borderWidth);

        int axisNameSize = textHeight+6;

        int width = size.width - 2*borderWidth - axisNameSize;
        int height = size.height - 2*borderHeight - axisNameSize;
        Point origin = new Point(borderWidth+axisNameSize+5, height+borderHeight);

        ScaleRange scaleRangeX = xSeries.getScaleRange(origin.x, origin.x+width);
        ScaleRange scaleRangeY = ySeries.getScaleRange(origin.y-horizontalGap, origin.y-height+horizontalGap);

        // draw data
        if(!(xSeries instanceof RangeSeries && ySeries instanceof ListSeries)) {
            throw new NotImplementedException();
        }

        g2d.setColor(lineColor);
        double[] series = ((ListSeries) ySeries).getSeries();
        int length = series.length;
        double xVal = xSeries.getMin();
        double xStep = (xSeries.getMax() - xSeries.getMin())/length;
        int px = (int)scaleRangeX.scaleValue(xSeries.getMin());
        int py = (int)scaleRangeY.scaleValue(series[0]);
        int ppx, ppy;
        for(int i=1;i<length;i++){
            xVal+=xStep;
            ppx = (int)scaleRangeX.scaleValue(xVal);
            ppy = (int)scaleRangeY.scaleValue(series[i]);
            g2d.drawLine(px,py,ppx,ppy);
            px=ppx;
            py=ppy;
        }

        //draw axis and labels
        g2d.setColor(Color.BLACK);

        g2d.drawRect(origin.x,origin.y-height,width,height);

        // x
//        double rootx = xLabels.series[0];
//        ScaleRange scaleRangeX = new ScaleRange(xLabels.series[0],xLabels.series[xLabels.series.length-1], origin.x, origin.x+width);
//        double v2px = width/(xSeries.getMax()-rootx);
//        double v2px = scaleRangeX.getScale();

        int xn = xLabels.labels.length;
        for(int i=0;i< xn;i++){
            if(i==0 && xn>1 &&
                    (metrics.stringWidth(xLabels.labels[0])+metrics.stringWidth(xLabels.labels[1]))/2
                            > scaleRangeX.getScale()*(xLabels.series[1] - xLabels.series[0])){
                continue;
            }
            if(i==xn-1 && xn>1 &&
                    (metrics.stringWidth(xLabels.labels[xn-1])+metrics.stringWidth(xLabels.labels[xn-2]))/2
                            > scaleRangeX.getScale()* (xLabels.series[xn-1] - xLabels.series[xn-2])){
                continue;
            }
//            int x = (int)((xLabels.series[i]-rootx)*v2px+origin.x);
            int x = (int) scaleRangeX.scaleValue(xLabels.series[i]);
            g2d.drawLine(x,origin.y-height,x, origin.y-height+sliceHeight);
            g2d.drawLine(x,origin.y,x, origin.y-sliceHeight);
            int textWidth = metrics.stringWidth(xLabels.labels[i]);
            g2d.drawString(xLabels.labels[i],x-textWidth/2,origin.y + textHeight+3);
        }

        // y
//        double rooty= yLabels.series[0];
//        ScaleRange scaleRangeY = new ScaleRange(yLabels.series[0],yLabels.series[yLabels.series.length-1], origin.y-horizontalGap, origin.y-height+horizontalGap);
//        double v2py = (height-topGap)/(ySeries.getMax()-rooty);
        int yn = yLabels.labels.length;
        double minGapYToDraw = Math.abs(metrics.getHeight()/scaleRangeY.getScale());
        for(int i=0;i<yn;i++){
            if(i==0 && yn>1 &&
                    minGapYToDraw > (yLabels.series[1] - yLabels.series[0])){
                continue;
            }
            if(i==yn-1 && yn>1 &&
                    minGapYToDraw > (yLabels.series[yn-1] - yLabels.series[yn-2])){
                continue;
            }
//            int y = (int)(origin.y - (yLabels.series[i]-rooty)*v2py);
            int y = (int) scaleRangeY.scaleValue(yLabels.series[i]);
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


        g2d.setColor(oldLineColor);
        g2d.setStroke(stroke);
        g2d.setFont(font);

        // export
        ScaleRange.expandRange(scaleRangeX, origin.x, origin.x+width);
        ScaleRange.expandRange(scaleRangeY, origin.y, origin.y-height);
        this.xRange = scaleRangeX;
        this.yRange = scaleRangeY;
    }

    public ScaleRange getXScaleRange(){return xRange;}

    public ScaleRange getYScaleRange(){return yRange;}

}
