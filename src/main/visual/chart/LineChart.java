package visual.chart;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dung on 01/12/2016.
 */
public class LineChart {
    private Graphics2D g2d;
    private Dimension size;
    private Series xSeries;
    private List<Series> ySeries;
    private int horizontalGap;
    private Color lineColor;

    private Color axisColor=Color.BLACK;
    private Color backgroundColor=Color.WHITE;
    private Color textColor=Color.BLACK;

    private ScaleRange xRange;
    private ScaleRange yRange;

    public LineChart(Graphics2D g2d){
        ySeries = new ArrayList<Series>();
        setGraphics2d(g2d);
    }

    public void setGraphics2d(Graphics2D g2d){this.g2d = g2d;}

    public void addYSeries(Series series){
        this.ySeries.add(series);
    }

    public void setySeries(Series series){
        removeySeries();
        addYSeries(series);
    }

    public void removeySeries(){this.ySeries.clear();}

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

    protected void drawData(Graphics2D g2d, Series xSeries, List<Series> ySeries, ScaleRange scaleRangeX, ScaleRange scaleRangeY){
        for(Series s : ySeries) {
            if(s instanceof XYListSeries){
                XYListSeries ys = (XYListSeries)s;
                double[] series = ys.getSeries();
                int length = series.length;
                double[] xseries = ys.anchorSeries;

                int px = (int) scaleRangeX.scaleValue(xseries[0]);
                int py = (int) scaleRangeY.scaleValue(series[0]);
                int ppx, ppy;
                for (int i = 1; i < length; i++) {
                    ppx = (int) scaleRangeX.scaleValue(xseries[i]);
                    ppy = (int) scaleRangeY.scaleValue(series[i]);
                    g2d.drawLine(px, py, ppx, ppy);
                    px = ppx;
                    py = ppy;
                }
            }else {
                ListSeries ys = (ListSeries)s;
                double[] series = ys.getSeries();
                int length = series.length;

                double xVal = xSeries.getMin();
                double xStep = (xSeries.getMax() - xSeries.getMin()) / length;
                int px = (int) scaleRangeX.scaleValue(xSeries.getMin());
                int py = (int) scaleRangeY.scaleValue(series[0]);
                int ppx, ppy;
                for (int i = 1; i < length; i++) {
                    xVal += xStep;
                    ppx = (int) scaleRangeX.scaleValue(xVal);
                    ppy = (int) scaleRangeY.scaleValue(series[i]);
                    g2d.drawLine(px, py, ppx, ppy);
                    px = ppx;
                    py = ppy;
                }
            }
        }
    }

    public void draw(Graphics2D g2d, Dimension size, int horizontalGap,
                     Series xSeries, List<Series> ySeries, Color lineColor){

        if(xSeries==null || ySeries==null || ySeries.size() == 0 || size==null ||lineColor==null){
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

        double ymin_t = ySeries.get(0).getMin();
        double ymax_t = ySeries.get(0).getMax();
        for(Series s : ySeries){
            if(ymax_t < s.getMax()) ymax_t=s.getMax();
            if(ymin_t > s.getMin()) ymin_t=s.getMin();
        }
        Series t_ySeries = new RangeSeries(ySeries.get(0).label,ymin_t,ymax_t);
        Series.SeriesLabels yLabels = t_ySeries.getSeriesLabels(maxYLabels);

        int sliceHeight = 5;

        int textHeight = metrics.getHeight();

        int borderHeight = textHeight+6;
        int borderWidth = 0;
        if(yLabels.labels[0].length() > yLabels.labels[yLabels.labels.length-1].length()){
            borderWidth = metrics.stringWidth(yLabels.labels[0]);
        }else{
            borderWidth = metrics.stringWidth(yLabels.labels[yLabels.labels.length-1]);
        }
        borderWidth = Math.max(20,borderWidth);

        int axisNameSize = textHeight+6;

        int width = size.width - 2*borderWidth - axisNameSize;
        int height = size.height - 2*borderHeight - axisNameSize;
        Point origin = new Point(borderWidth+axisNameSize+5, height+borderHeight);

        ScaleRange scaleRangeX = xSeries.getScaleRange(origin.x, origin.x+width);
        ScaleRange scaleRangeY = t_ySeries.getScaleRange(origin.y-horizontalGap, origin.y-height+horizontalGap);

        // draw data
        if(!(xSeries instanceof RangeSeries)) {
            for(Series y : ySeries)
                if(!(y instanceof ListSeries))
                    throw new NotImplementedException();
        }

        g2d.setColor(backgroundColor);
        g2d.fillRect(origin.x,origin.y-height,width,height);

        g2d.setColor(lineColor);
        drawData(g2d,xSeries,ySeries,scaleRangeX,scaleRangeY);
//        double[] series = ((ListSeries) ySeries).getSeries();
//        int length = series.length;
//        double xVal = xSeries.getMin();
//        double xStep = (xSeries.getMax() - xSeries.getMin())/length;
//        int px = (int)scaleRangeX.scaleValue(xSeries.getMin());
//        int py = (int)scaleRangeY.scaleValue(series[0]);
//        int ppx, ppy;
//        for(int i=1;i<length;i++){
//            xVal+=xStep;
//            ppx = (int)scaleRangeX.scaleValue(xVal);
//            ppy = (int)scaleRangeY.scaleValue(series[i]);
//            g2d.drawLine(px,py,ppx,ppy);
//            px=ppx;
//            py=ppy;
//        }

        //draw axis and labels
        g2d.setColor(axisColor);

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
            g2d.setColor(axisColor);
            int x = (int) scaleRangeX.scaleValue(xLabels.series[i]);
            g2d.drawLine(x,origin.y-height,x, origin.y-height+sliceHeight);
            g2d.drawLine(x,origin.y,x, origin.y-sliceHeight);
            g2d.setColor(textColor);
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
            g2d.setColor(axisColor);
            int y = (int) scaleRangeY.scaleValue(yLabels.series[i]);
            g2d.drawLine(origin.x+width,y,origin.x+width-sliceHeight, y);
            g2d.drawLine(origin.x,y,origin.x+sliceHeight, y);
            g2d.setColor(textColor);
            int textWidth = metrics.stringWidth(yLabels.labels[i]);
            g2d.drawString(yLabels.labels[i],origin.x-textWidth-3,y+textHeight/2);
        }

        // draw axis name
        int nameWidth = metrics.stringWidth(xSeries.label);
        g2d.drawString(xSeries.label,origin.x+(width-nameWidth)/2, origin.y+borderHeight+textHeight/2+5);

        nameWidth = metrics.stringWidth(t_ySeries.label);
        AffineTransform ori_at = g2d.getTransform();
        AffineTransform at = new AffineTransform();
        at.setToRotation(-Math.PI / 2.0, 0, 0);
        g2d.setTransform(at);
        g2d.drawString(t_ySeries.label, -height/2 - borderHeight-nameWidth/2, axisNameSize);
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
