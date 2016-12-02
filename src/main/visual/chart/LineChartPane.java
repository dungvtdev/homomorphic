package visual.chart;

import visual.chart.LineChart;

import javax.swing.*;
import java.awt.*;

/**
 * Created by dung on 02/12/2016.
 */
public class LineChartPane extends JPanel{
    private LineChart chart;

    public LineChartPane(){
        chart = new LineChart();
    }

    @Override
    public void paintComponent(Graphics g) {
        System.out.println("repaint");
//        //rectangle originates at 10,10 and ends at 240,240
//        g.drawRect(10, 10, 240, 240);
//        //filled Rectangle with rounded corners.
//        g.fillRoundRect(50, 50, 100, 100, 80, 80);
        Series xSeries = new RangeSeries("X",0,35000);
        Series ySeries = new RangeSeries("Y",-4500,6000);
        chart.draw((Graphics2D)g,this.getSize(),16,xSeries,ySeries);
    }

    public void draw(Dimension size, int topGap, Series xSeries, Series ySeries){
        chart.setDrawSize(size);
        chart.setTopGap(topGap);
        chart.setXSeriers(xSeries);
        chart.setYSeries(ySeries);
        this.repaint();
    }
}
