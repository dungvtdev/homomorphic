package app;

import io.file.WavFile;
import javafx.scene.chart.Chart;
import visual.chart.LineChart;
import visual.chart.ListSeries;
import visual.chart.RangeSeries;
import visual.chart.Series;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

/**
 * Created by dung on 01/12/2016.
 */
public class MainForm extends JFrame implements HomomorphicProcessListener {

    private Controller controller;
    private JLabel lbFileName;
    private WavChartPanel wavChart;
    private ChartPanel hammingChart;
    private ChartPanel magResponseChart;
    private ChartPanel phaseResponseChart;
    private JLabel lbHammingStatus;


    // can chinh panel cho de
    private final int[] colSizes = new int[]{240, 450, 450};
    private final int panelGap = 20;

    public MainForm() {
        this.controller = new Controller();

        initUI();

        //test
        File file = new File("/home/dung/wavefile/A96.wav");
        controller.openFile(file);
        lbFileName.setText("File: " + file.getName());

        drawSignal();
        process(0);
    }

    private void initUI() {
        setTitle("Homomorphic");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menu = createMenu();
        this.setJMenuBar(menu);

        JPanel rootPanel = new JPanel(new BorderLayout());

        /**
         * top panel
         */
        JPanel filePane = new JPanel();
        lbFileName = new JLabel("File: fajskdl");
        filePane.add(lbFileName);


        /**
         * content panel
         */
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
//        content.setPreferredSize(new Dimension(1000,600));


        Box hBoxTop = Box.createHorizontalBox();

        Container inoutPane = createInOutPane();
        Container fresponsePane = createFResponsePane();

        hBoxTop.add(inoutPane);
        hBoxTop.add(fresponsePane);

        content.add(hBoxTop);


        Box hBoxBottom = Box.createHorizontalBox();

        JPanel wavPane = createWavPanel();
        Container hammingPane = createHammingPanel();

        hBoxBottom.add(wavPane);
//        hBoxBottom.add(Box.createRigidArea(new Dimension(panelGap,0)));
        hBoxBottom.add(hammingPane);

        content.add(hBoxBottom);

        rootPanel.add(filePane, BorderLayout.PAGE_START);
        rootPanel.add(content, BorderLayout.CENTER);

        this.setContentPane(rootPanel);
        this.pack();
    }

    private JMenuBar createMenu(){
        JMenuBar menuBar = new JMenuBar();

        JMenu mfile = new JMenu("File");
        JMenuItem mFileOpen = new JMenuItem("Open");
        JMenuItem mFileQuit = new JMenuItem("Quit");
        mfile.add(mFileOpen);
        mfile.add(mFileQuit);

        JMenu mtools = new JMenu("Tools");
        JMenuItem mToolFileInfo = new JMenuItem("File Info");
        JMenuItem mToolSetting = new JMenuItem("Setting");
        mtools.add(mToolFileInfo);
        mtools.add(mToolSetting);

        JMenu mhelp = new JMenu("Help");
        JMenuItem mAbout = new JMenuItem("About");
        mhelp.add(mAbout);


        menuBar.add(mfile);
        menuBar.add(mtools);
        menuBar.add(mhelp);

        mFileOpen.addActionListener((ActionEvent event) ->{
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.wav","wav"));
            int returnVal = fileChooser.showOpenDialog(MainForm.this);
            if(returnVal == JFileChooser.APPROVE_OPTION){
                File file = fileChooser.getSelectedFile();
                controller.openFile(file);
                lbFileName.setText("File: " + file.getName());

                drawSignal();
                process(0);
            }
        });

        mFileQuit.addActionListener((ActionEvent event) ->{
            System.exit(0);
        });

        mToolFileInfo.addActionListener((ActionEvent event) ->{

        });

        mToolSetting.addActionListener((ActionEvent event) ->{

        });

        mAbout.addActionListener((ActionEvent event) ->{

        });

        return menuBar;
    }

    private JPanel createWavPanel(){
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

        JLabel lb = new JLabel("Signal");

        wavChart = new WavChartPanel();
        wavChart.setPreferredSize(new Dimension(colSizes[0]+colSizes[1],300));

        Box toolpane = Box.createHorizontalBox();
        JButton btnPrev = new JButton("Prev");
        JButton btnPlay = new JButton("Auto");
        JButton btnNext = new JButton("Next");

        toolpane.add(btnPrev);
        toolpane.add(btnPlay);
        toolpane.add(btnNext);

        pane.add(lb);
        pane.add(Box.createRigidArea(new Dimension(0,5)));
        pane.add(wavChart);
        pane.add(Box.createRigidArea(new Dimension(0,5)));
        pane.add(toolpane);

        return pane;
    }

    private Container createHammingPanel(){
        Box vBox = Box.createVerticalBox();
        JLabel lb = new JLabel("Hamming");
        hammingChart = new ChartPanel();
        hammingChart.setPreferredSize(new Dimension(colSizes[2],300));

        lbHammingStatus=new JLabel("..");

        vBox.add(lb);
        vBox.add(hammingChart);
        vBox.add(lbHammingStatus);

        return vBox;
    }

    private Container createFResponsePane(){
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

        JLabel lb = new JLabel("Frequency Response");
        pane.add(lb);

        Box hBox = Box.createHorizontalBox();

        magResponseChart = new ChartPanel();
        magResponseChart.setPreferredSize(new Dimension(colSizes[1],300));

        phaseResponseChart = new ChartPanel();
        phaseResponseChart.setPreferredSize(new Dimension(colSizes[2],300));

        hBox.add(magResponseChart);
        hBox.add(phaseResponseChart);

        pane.add(hBox);

        return pane;
    }


    private Container createInOutPane(){
        Box parent = Box.createVerticalBox();
        JLabel lb = new JLabel("Formant");
        parent.add(lb);
        parent.setPreferredSize(new Dimension(colSizes[0],300));
        return parent;
    }

    private class WavChartPanel extends ChartPanel{
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
        }

    }

    private class ChartPanel extends JPanel{
        LineChart chart;

        public ChartPanel(){
            chart = new LineChart(null);
            chart.setLineColor(Color.green);
            chart.setHorizontalGap(12);
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            System.out.println("repaint");
            Graphics2D g2d = (Graphics2D)g;
            chart.setDrawSize(this.getSize());
            chart.setGraphics2d(g2d);
            chart.draw();
        }

        public void drawChart(String xLabel, String yLabel, int xMin, int xMax, double[] series){
            Series xSeries = new RangeSeries(xLabel,xMin,xMax);
            Series ySeries = new ListSeries(yLabel, series);
            chart.setXSeriers(xSeries);
            chart.setYSeries(ySeries);
            this.repaint();
        }
    }


    private void drawSignal(){
        double ntoms = nSamplesToSecond()*1000.0;
        double[] raws = controller.getRawSignal();
        wavChart.drawChart("X","Y",0,(int)(raws.length*ntoms), raws);
    }

    private void process(int offset){
        controller.process(this, offset);
    }

    public void onProcessReturn(java.util.List<double[]> result, int offset){
        double ntotime = nSamplesToSecond()*1000.0;     //ms
        //draw hamming
        double[] hamming = result.get(0);
        hammingChart.drawChart("X","Y",(int)(offset*ntotime),
                                (int)((offset+hamming.length)*ntotime),
                                hamming);

        double[] mresponse = result.get(1);
        double[] presponse = result.get(2);

        magResponseChart.drawChart("X", "Y",0, (int)controller.getWav().getSampleRate(),mresponse);
        phaseResponseChart.drawChart("X", "Y",0, (int)controller.getWav().getSampleRate(),presponse);

    }

    private double nSamplesToSecond(){
        WavFile wav = controller.getWav();
        double ntos = 1.0/wav.getSampleRate();
        return ntos;
    }

    private double nSamplesToFrequency(double size){
        WavFile wav = controller.getWav();
        double ntof = wav.getSampleRate()/size;
        return ntof;
    }


}
