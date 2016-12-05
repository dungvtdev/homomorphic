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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

/**
 * Created by dung on 01/12/2016.
 */
public class MainForm extends JFrame implements HomomorphicProcessListener,
        CallbackListener<SettingsForm.SettingModel> {

    private Controller controller;
    private JLabel lbFileName;
    private WavChartPanel wavChart;
    private ChartPanel hammingChart;
    private ChartPanel magResponseChart;
    private ChartPanel phaseResponseChart;
    private JLabel lbHammingStatus;
    private JLabel lbFormants;
    private JButton btnPrev;
    private JButton btnPlay;
    private JButton btnNext;

    private State state;

    // can chinh panel cho de
    private final int[] colSizes = new int[]{240, 450, 450};
    private final int panelGap = 20;

    public MainForm() {
        this.controller = new Controller();

        initUI();

        setState(State.Normal);

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

    private JMenuBar createMenu() {
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

        mFileOpen.addActionListener((ActionEvent event) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.wav", "wav"));
            int returnVal = fileChooser.showOpenDialog(MainForm.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                controller.openFile(file);
                lbFileName.setText("File: " + file.getName());

                drawSignal();
                process(0);
            }
        });

        mFileQuit.addActionListener((ActionEvent event) -> {
            System.exit(0);
        });

        mToolFileInfo.addActionListener((ActionEvent event) -> {
            new WavInfoForm(this,"Wav File Info",controller.wav);
        });

        mToolSetting.addActionListener((ActionEvent event) -> {
            int cn = controller.homomorphic.cnSize;
            float delay = controller.delayTime;
            new SettingsForm(this, new SettingsForm.SettingModel(cn,delay), this);
        });

        mAbout.addActionListener((ActionEvent event) -> {
            new AboutForm(this);
        });

        return menuBar;
    }

    private JPanel createWavPanel() {
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

        JLabel lb = new JLabel("Signal");

        wavChart = new WavChartPanel();
        wavChart.setPreferredSize(new Dimension(colSizes[0] + colSizes[1], 300));

        Box toolpane = Box.createHorizontalBox();
        btnPrev = new JButton("Prev");
        btnPlay = new JButton("Auto");
        btnNext = new JButton("Next");

        btnPrev.addActionListener((ActionEvent event) -> {
            controller.processBack(this);
        });

        btnPlay.addActionListener((ActionEvent event) -> {
            if(state==State.Normal){
                setState(State.Running);
                controller.processAuto(this,(x)->{
                    setState(State.Normal);
                    return null;
                });
            }else{
                controller.stopRunning();
            }
        });

        btnNext.addActionListener((ActionEvent event) -> {
            controller.processNext(this);
        });

        toolpane.add(btnPrev);
        toolpane.add(btnPlay);
        toolpane.add(btnNext);

        pane.add(lb);
        pane.add(Box.createRigidArea(new Dimension(0, 5)));
        pane.add(wavChart);
        pane.add(Box.createRigidArea(new Dimension(0, 5)));
        pane.add(toolpane);

        return pane;
    }

    private Container createHammingPanel() {
        Box vBox = Box.createVerticalBox();
        JLabel lb = new JLabel("Hamming");
        hammingChart = new ChartPanel();
        hammingChart.setPreferredSize(new Dimension(colSizes[2], 300));

        lbHammingStatus = new JLabel("..");

        vBox.add(lb);
        vBox.add(hammingChart);
        vBox.add(lbHammingStatus);

        return vBox;
    }

    private Container createFResponsePane() {
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

        JLabel lb = new JLabel("Frequency Response");
        pane.add(lb);

        Box hBox = Box.createHorizontalBox();

        magResponseChart = new ChartPanel();
        magResponseChart.setPreferredSize(new Dimension(colSizes[1], 300));

        phaseResponseChart = new ChartPanel();
        phaseResponseChart.setPreferredSize(new Dimension(colSizes[2], 300));

        hBox.add(magResponseChart);
        hBox.add(phaseResponseChart);

        pane.add(hBox);

        return pane;
    }


    private Container createInOutPane() {
        Box parent = Box.createVerticalBox();
        parent.createHorizontalGlue();
        JLabel lb = new JLabel("Formant");
        lbFormants = new JLabel("");
        parent.add(lb);
        parent.add(lbFormants);
        parent.setPreferredSize(new Dimension(colSizes[0], 300));
        return parent;
    }

    private class WavChartPanel extends ChartPanel implements MouseListener{
        private BufferedImage buffer;
        private boolean orderRedrawChart = false;
        private int currentSampleX=0;
        private int windowSampleSize=512;

        public WavChartPanel(){
            this.addMouseListener(this);
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.clearRect(0,0,getWidth(),getHeight());
            // draw chart to buffer
            checkRedrawChartBuffer();

            // draw chart
            g2d.drawImage(buffer, 0, 0, null);

            // draw window
            drawWindow(g2d);
        }

        private void drawWindow(Graphics2D g2d){
            if(chart.getXScaleRange() !=null && chart.getYScaleRange() !=null){
                int xleft = (int)chart.getXScaleRange().scaleValue(sampleToXTime(currentSampleX))+1;
                int xright = (int)chart.getXScaleRange().scaleValue(sampleToXTime(currentSampleX+windowSampleSize));
                int ybottom = (int)chart.getYScaleRange().scaleValue("min")-1;
                int ytop = (int)chart.getYScaleRange().scaleValue("max")+1;

                xright = (int)(Math.min(xright, chart.getXScaleRange().scaleValue("max")))-1;

                // draw rect
                Color color = g2d.getColor();

                Composite composite = g2d.getComposite();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.75f));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(xleft, ytop, xright-xleft, Math.abs(ytop-ybottom));

                // draw arrow
                Stroke stroke = g2d.getStroke();
                g2d.setStroke(new BasicStroke(2));

                g2d.setComposite(composite);
                g2d.setColor(Color.RED);
                g2d.fillPolygon(new int[]{xleft-8, xleft+8, xleft},
                                new int[]{ytop-12, ytop-12, ytop},
                                3);
                g2d.drawLine(xleft, ybottom, xleft, ytop);

                g2d.setStroke(stroke);
                g2d.setColor(color);
            }
        }

        private void checkRedrawChartBuffer() {
            // draw chart to buffer
            Dimension size = this.getSize();
            if (buffer == null || buffer.getWidth() != size.width || buffer.getHeight() != size.height) {
                buffer = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
                orderRedrawChart = true;
            }

            if (orderRedrawChart) {
                Graphics2D gbuffer = buffer.createGraphics();
                Color color = new Color(1.0f, 0.0f, 1.0f, 0.0f);
                gbuffer.setColor(color);
                gbuffer.fillRect(0, 0, size.width, size.height);
                drawChartToGraphics(gbuffer);
                orderRedrawChart = false;
            }
        }

        public void setCurrentPointer(int index){
            currentSampleX = index;
            this.repaint();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(state==State.Running)
                return;

            Point p = e.getPoint();
            double ms = chart.getXScaleRange().inverseScaleValue(p.x);
            currentSampleX = xTimeToSample(ms);
            currentSampleX = controller.TruncateSampleIndex(currentSampleX);
            System.out.printf("Mouse click %d\n",currentSampleX);
//            this.repaint();
            process(currentSampleX);    // process callback se repaint sau
        }

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
    }

    private class ChartPanel extends JPanel {
        protected LineChart chart;

        public ChartPanel() {
            chart = new LineChart(null);
            chart.setLineColor(Color.green);
            chart.setHorizontalGap(12);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            System.out.println("repaint");
            Graphics2D g2d = (Graphics2D) g;
            drawChartToGraphics(g2d);
        }

        public void drawChart(String xLabel, String yLabel, int xMin, int xMax, double[] series) {
            setupChart(xLabel, yLabel, xMin, xMax, series);
            this.repaint();
        }

        protected void setupChart(String xLabel, String yLabel, int xMin, int xMax, double[] series) {
            Series xSeries = new RangeSeries(xLabel, xMin, xMax);
            Series ySeries = new ListSeries(yLabel, series);
            chart.setXSeriers(xSeries);
            chart.setYSeries(ySeries);
        }

        protected void drawChartToGraphics(Graphics2D g2d) {
            RenderingHints rh = new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHints(rh);

            chart.setDrawSize(this.getSize());
            chart.setGraphics2d(g2d);
            chart.draw();
        }
    }

    private void drawSignal() {
        double[] raws = controller.getRawSignal();
        int max = (int)sampleToXTime(raws.length);
        wavChart.drawChart("Time (ms)", "Amplitude", 0, max, raws);
    }

    private void process(int offset) {
        controller.process(this, offset);
    }

    @Override
    public void onProcessReturn(boolean success, java.util.List<double[]> result, int offset) {
        if(!success) return;

        //draw hamming
        double[] hamming = result.get(0);
        int xmin=(int)sampleToXTime(offset);
        int xmax =(int)sampleToXTime(offset+hamming.length);
        hammingChart.drawChart("Time (ms)", "Amplitude", xmin,
                xmax,
                hamming);
        lbHammingStatus.setText(String.format("Samples: %d to %d", offset, hamming.length+offset));

        double[] mresponse = result.get(1);
        double[] presponse = result.get(2);

        magResponseChart.drawChart("Frequency (Hz)", "Magnitude (dB)", 0, (int) controller.getWav().getSampleRate(), mresponse);
        phaseResponseChart.drawChart("Frequency (Hz)", "Phase (P", 0, (int) controller.getWav().getSampleRate(), presponse);

        // formants
        double[] formants = result.get(3);
        StringBuilder str = new StringBuilder();
        str.append("<html>");
        for(int i=0;i<formants.length;i++){
            str.append("F["+(i+1)+"] = "+(int)formants[i]+"<br>");
        }
        str.append("</html>");
        lbFormants.setText(str.toString());
        wavChart.setCurrentPointer(offset);
    }

    // when SettingsForm callback
    @Override
    public void onCallback(SettingsForm.SettingModel setting) {
        controller.delayTime = setting.delay;
        int cn = (setting.cn > controller.homomorphic.windowSize)?
                controller.homomorphic.windowSize:
                setting.cn;
        controller.homomorphic.cnSize = cn;

        process(controller.getOffset());
    }

    private double sampleToXTime(int value){
        return value*controller.homomorphic.nSamplesToSecond()*1000;   //ms
    }

    private int xTimeToSample(double value){
        return (int)(value/(controller.homomorphic.nSamplesToSecond()*1000));
    }

    private void setState(State state){
        this.state = state;
        switch (state){
            case Normal:
                btnPrev.setEnabled(true);
                btnNext.setEnabled(true);
                btnPlay.setText("Play");
                break;
            case Running:
                btnPrev.setEnabled(false);
                btnNext.setEnabled(false);
                btnPlay.setText("Pause");
                break;
        }
    }

    public enum State{
        Normal,
        Running
    }

}
