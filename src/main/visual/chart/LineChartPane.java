package visual.chart;

import visual.chart.LineChart;

import javax.swing.*;
import java.awt.*;

/**
 * Created by dung on 02/12/2016.
 */
public class LineChartPane  extends JPanel{
    private LineChart chart;

    public LineChartPane(){
    }

    @Override
    public void paintComponent(Graphics g) {
        System.out.println("repaint");
        chart = new LineChart((Graphics2D)g);
        chart.setXSeriers(new RangeSeries("x",512,12345));
        chart.setYSeries(new RangeSeries("y",-424235,652365));
        chart.setDrawSize(this.getSize());
        chart.draw(new Dimension(30,12),5);
//        //rectangle originates at 10,10 and ends at 240,240
//        g.drawRect(10, 10, 240, 240);
//        //filled Rectangle with rounded corners.
//        g.fillRoundRect(50, 50, 100, 100, 80, 80);
    }
}
