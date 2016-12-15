package app;

import io.file.WavFile;
import javafx.scene.chart.Chart;
import jdk.nashorn.internal.objects.annotations.Constructor;
import visual.chart.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.zip.GZIPOutputStream;

/**
 * Created by dung on 01/12/2016.
 */
public class MainForm extends JFrame implements HomomorphicProcessListener{

    private Controller controller;
    private JLabel lbFileName;
    private WavChartPanel wavChart;
    private ChartPanel hammingChart;
    private ChartPanel magResponseChart;
    private ChartPanel phaseResponseChart;
    private FormantChartPanel formantChart;

    private JLabel lbHammingStatus;
    private JLabel lbFormants;
    private JButton btnPrev;
    private JButton btnPlay;
    private JButton btnNext;

    private JTextField tWndSize;
    private JTextField tCnSize;
    private JTextField tZcrThreshold;
    private JTextField tPowerThreshold;
    private JTextArea txtWavInfo;

    private State state;

    // can chinh panel cho de
    private final int[] colSizes = new int[]{150, 280, 280, 280};
    private final int[] rowsSizes = new int[]{220, 220, 260};
    private final int panelGap = 20;

    public MainForm() {
        this.controller = new Controller();

        initUI();

        setState(State.Nothing);

//        //test
//        File file = new File("/home/dung/wavefile/Xe.wav");
//        controller.openFile(file);
//        lbFileName.setText("File: " + file.getName());

//        processFirst(0);
    }

    private void processFirst(int offset){
        ResetParamsUI();
        drawSignal();
        drawFormants();
        process(offset);
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
//        Box content = Box.createHorizontalBox();
//        content.setPreferredSize(new Dimension(1000,600));


        Box vRightBox = Box.createVerticalBox();

        Box hTopBox = Box.createHorizontalBox();
        Container hammingPane = createHammingPanel();
        Container fresponsePane = createFResponsePane();

        hTopBox.add(fresponsePane);
        hTopBox.add(hammingPane);

        Container formantPane = createFormantChartPanel();
        JPanel wavPane = createWavPanel();

        vRightBox.add(hTopBox);
        vRightBox.add(formantPane);
        vRightBox.add(wavPane);

        Box vLeftBox = Box.createVerticalBox();

        Container resultPanel = createResultPanel();
        Container paramsPanel = createParamsPanel();

        txtWavInfo = new JTextArea();
        txtWavInfo.setEditable(false); // set textArea non-editable
        txtWavInfo.setLineWrap(true);
        txtWavInfo.setWrapStyleWord(true);
        txtWavInfo.setPreferredSize(new Dimension(colSizes[0],rowsSizes[2]));


        vLeftBox.add(resultPanel);
        vLeftBox.add(Box.createRigidArea(new Dimension(0,10)));
        vLeftBox.add(paramsPanel);
        vLeftBox.add(Box.createRigidArea(new Dimension(0,10)));
        vLeftBox.add(txtWavInfo);

//        content.add(vLeftBox);
//        content.add(vRightBox);

        rootPanel.add(filePane, BorderLayout.PAGE_START);
//        rootPanel.add(content, BorderLayout.CENTER);
        rootPanel.add(vLeftBox, BorderLayout.WEST);
        rootPanel.add(vRightBox, BorderLayout.CENTER);

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

        JMenu mhelp = new JMenu("Help");
        JMenuItem mAbout = new JMenuItem("About");
        mhelp.add(mAbout);


        menuBar.add(mfile);
        menuBar.add(mhelp);

        mFileOpen.addActionListener((ActionEvent event) -> {
            if (state == State.Normal)
                return;

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.wav", "wav"));
            int returnVal = fileChooser.showOpenDialog(MainForm.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                controller.openFile(file);
                lbFileName.setText("File: " + file.getName());

                drawWavInfo();
                processFirst(0);
            }
        });

        mFileQuit.addActionListener((ActionEvent event) -> {
            System.exit(0);
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
        wavChart.setPreferredSize(new Dimension(colSizes[1] + colSizes[2] + colSizes[3], rowsSizes[2]));

        Box toolpane = Box.createHorizontalBox();
        btnPrev = new JButton("Prev");
        btnPlay = new JButton("Auto");
        btnNext = new JButton("Next");

        lbHammingStatus = new JLabel("..");

        btnPrev.addActionListener((ActionEvent event) -> {
            controller.processBack(this);
        });

        btnPlay.addActionListener((ActionEvent event) -> {
            if (state == State.Normal || state == State.Nothing) {
                setState(State.Running);
                controller.processAuto(this, (x) -> {
                    setState(State.Normal);
                    return null;
                });
            } else {
                controller.stopRunning();
            }
        });

        btnNext.addActionListener((ActionEvent event) -> {
            controller.processNext(this);
        });

        toolpane.add(btnPrev);
        toolpane.add(Box.createRigidArea(new Dimension(10,0)));
        toolpane.add(btnPlay);
        toolpane.add(Box.createRigidArea(new Dimension(10,0)));
        toolpane.add(btnNext);
        toolpane.add(Box.createRigidArea(new Dimension(10,0)));
        toolpane.add(lbHammingStatus);

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
        hammingChart.setPreferredSize(new Dimension(colSizes[3], rowsSizes[0]));

        vBox.add(lb);
        vBox.add(hammingChart);
//        vBox.add(lbHammingStatus);

        return vBox;
    }

    private Container createFResponsePane() {
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

        JLabel lb = new JLabel("Frequency Response");
        pane.add(lb);

        Box hBox = Box.createHorizontalBox();

        magResponseChart = new ChartPanel();
        magResponseChart.setPreferredSize(new Dimension(colSizes[1], rowsSizes[0]));

        phaseResponseChart = new ChartPanel();
        phaseResponseChart.setPreferredSize(new Dimension(colSizes[2], rowsSizes[0]));

        hBox.add(magResponseChart);
        hBox.add(phaseResponseChart);

        pane.add(hBox);

        return pane;
    }

    private Container createFormantChartPanel() {
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

        JLabel lb = new JLabel("Formant");
        formantChart = new FormantChartPanel();
        formantChart.setPreferredSize(new Dimension(colSizes[1] + colSizes[2] + colSizes[3], rowsSizes[1]));

        pane.add(lb);
        pane.add(formantChart);

        return pane;
    }

    private Container createResultPanel() {
        JLabel lb = new JLabel("Formants:");
        lbFormants = new JLabel("");

        Box parent = Box.createVerticalBox();
//        parent.setLayout(new BoxLayout(parent, BoxLayout.PAGE_AXIS));

        parent.add(lb);
        parent.add(lbFormants);
        parent.setPreferredSize(new Dimension(colSizes[0], rowsSizes[0]));

        return parent;
    }

    private Container createParamsPanel() {
        JLabel lb = new JLabel("Params:");

//        JPanel parent = new JPanel();
//        parent.setLayout(new BoxLayout(parent, BoxLayout.PAGE_AXIS));
        Box vBox = Box.createVerticalBox();
        vBox.add(lb);

        vBox.add(new JLabel("Window Size:"));
        tWndSize = new JTextField("");
//        tWndSize.setMaximumSize(new Dimension(1000,100));
        vBox.add(tWndSize);

        vBox.add(new JLabel("Cn Size:"));
        tCnSize = new JTextField("");
//        tCnSize.setMaximumSize(new Dimension(1000,100));
        vBox.add(tCnSize);

        vBox.add(new JLabel("Zrc Threshold:"));
        tZcrThreshold = new JTextField("");
//        tZcrThreshold.setMaximumSize(new Dimension(1000,100));
        vBox.add(tZcrThreshold);

        vBox.add(new JLabel("Power Threshold:"));
        tPowerThreshold = new JTextField("");
//        tPowerThreshold.setMaximumSize(new Dimension(1000,100));
        vBox.add(tPowerThreshold);

//        vBox.add(Box.createRigidArea(new Dimension(1000,300)));

        Box hBox = Box.createHorizontalBox();
        JButton btnSubmit = new JButton("Apply");
        JButton btnReset = new JButton("Reset");
        hBox.add(btnReset);
        hBox.add(Box.createRigidArea(new Dimension(10,0)));
        hBox.add(btnSubmit);

//        vBox.setMaximumSize(new Dimension(200,800));
        vBox.add(Box.createRigidArea(new Dimension(0,10)));
        vBox.add(hBox);
//        parent.add(new Box.Filler(new Dimension(0,100), new Dimension(0,1000), new Dimension(0,1000)));

        btnSubmit.addActionListener((ActionEvent e)->{
            int wndSize = Integer.parseInt(tWndSize.getText());
            int cnSize = Integer.parseInt(tCnSize.getText());
            double zcrThres = Double.parseDouble(tZcrThreshold.getText());
            double powerThres = Double.parseDouble(tPowerThreshold.getText());

            controller.homomorphic.windowSize = wndSize;
            controller.homomorphic.cnSize = cnSize;
            controller.homomorphic.zcrThreshold = zcrThres;
            controller.homomorphic.powerThreshold = powerThres;

            processFirst(-1);
        });

        btnReset.addActionListener((ActionEvent e)->{
            ResetParamsUI();
        });

        ResetParamsUI();

        return vBox;
    }

    private void ResetParamsUI(){
        if(controller == null || controller.homomorphic == null)
            return;
        tWndSize.setText(""+controller.homomorphic.windowSize);
        tCnSize.setText(""+controller.homomorphic.cnSize);
        tZcrThreshold.setText(""+controller.homomorphic.zcrThreshold);
        tPowerThreshold.setText(""+controller.homomorphic.powerThreshold);
    }


    private class FormantChartPanel extends WavChartPanel {
        public FormantChartPanel() {
            super();
            this.chart = new PlotChart(null);
            chart.setLineColor(Color.RED);
            chart.setHorizontalGap(12);
        }


        public void drawChart(String xLabel, String yLabel, int xMin, int xMax, double[][] series, double[] anchorSeries) {
            // series formant[n][n=5]

            if (series == null || series.length == 0) {
                return;
            }

            orderRedrawChart = true;
            Series xSeries = new RangeSeries(xLabel, xMin, xMax);
            chart.setXSeriers(xSeries);

            chart.removeySeries();
            int nFormants = 0;
            for(double[] s : series){
                if(s!=null) {
                    nFormants = Math.max(nFormants,s.length);
                }
            }
            if(nFormants==0)
                return;

            int nFrames = series.length;
            double[] sf = new double[nFrames];
            double[] xsf = new double[nFrames];
            for (int i = 0; i < nFormants; i++) {
                int index=0;
                for (int j = 0; j < nFrames; j++){
                    if(series[j]!=null){
                        if(series[j].length<=i)
                            continue;
                        sf[index] = series[j][i];
                        xsf[index] = anchorSeries[j];
                        index++;
                    }
                }
                double[] ssf = new double[index];
                double[] xxsf = new double[index];
                for(int j=0;j<index;j++){
                    ssf[j]=sf[j];
                    xxsf[j]=xsf[j];
                }
                chart.addYSeries(new XYListSeries(yLabel, ssf, xxsf));
            }
            this.repaint();
        }
    }

    private class WavChartPanel extends ChartPanel implements MouseListener {
        private BufferedImage buffer;
        protected boolean orderRedrawChart = false;
        private int currentSampleX = 0;
//        private int windowSampleSize = 512;

        public WavChartPanel() {
            super();
            this.addMouseListener(this);
        }

        @Override
        public void paintComponent(Graphics g) {
//            System.out.println("repaint");

            Graphics2D g2d = (Graphics2D) g;
            g2d.clearRect(0, 0, getWidth(), getHeight());
            // draw chart to buffer
            checkRedrawChartBuffer(getSize());

            // draw chart
            g2d.drawImage(buffer, 0, 0, null);

            // draw window
            drawWindow(g2d);
        }

        private void drawWindow(Graphics2D g2d) {
            if (chart.getXScaleRange() != null && chart.getYScaleRange() != null) {
                int wndsize = 512;
                if(controller.homomorphic!=null)
                    wndsize = controller.homomorphic.windowSize;

                int xleft = (int) chart.getXScaleRange().scaleValue(sampleToXTime(currentSampleX)) + 1;
                int xright = (int) chart.getXScaleRange().scaleValue(sampleToXTime(currentSampleX + wndsize));
                int ybottom = (int) chart.getYScaleRange().scaleValue("min") - 1;
                int ytop = (int) chart.getYScaleRange().scaleValue("max") + 1;

                xright = (int) (Math.min(xright, chart.getXScaleRange().scaleValue("max"))) - 1;

                // draw rect
                Color color = g2d.getColor();

                Composite composite = g2d.getComposite();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.75f));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(xleft, ytop, xright - xleft, Math.abs(ytop - ybottom));

                // draw arrow
                Stroke stroke = g2d.getStroke();
                g2d.setStroke(new BasicStroke(2));

                g2d.setComposite(composite);
                g2d.setColor(Color.RED);
                g2d.fillPolygon(new int[]{xleft - 8, xleft + 8, xleft},
                        new int[]{ytop - 12, ytop - 12, ytop},
                        3);
                g2d.drawLine(xleft, ybottom, xleft, ytop);

                g2d.setStroke(stroke);
                g2d.setColor(color);
            }
        }

        private void checkRedrawChartBuffer(Dimension size) {
            // draw chart to buffer
//            Dimension size = this.getSize();
            if (orderRedrawChart || buffer == null || buffer.getWidth() != size.width || buffer.getHeight() != size.height) {
                buffer = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
                orderRedrawChart = true;
            }

            if (orderRedrawChart) {
                Graphics2D gbuffer = buffer.createGraphics();
//                gbuffer.setColor(Color.white);
//                gbuffer.fillRect(0, 0, size.width, size.height);
//                gbuffer.setColor(Color.black);
                drawChartToGraphics(gbuffer, size);
                orderRedrawChart = false;
            }
        }

        public void drawChart(String xLabel, String yLabel, int xMin, int xMax, double[] series) {
            orderRedrawChart = true;
            super.drawChart(xLabel, yLabel, xMin, xMax, series);
        }

        public void setCurrentPointer(int index) {
            currentSampleX = index;
            this.repaint();
        }

        protected void doMouseClick(){
            process(currentSampleX);    // process callback se repaint sau
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (state == State.Running)
                return;

            Point p = e.getPoint();
            double ms = chart.getXScaleRange().inverseScaleValue(p.x);
            currentSampleX = xTimeToSample(ms);
            currentSampleX = controller.TruncateSampleIndex(currentSampleX);
            System.out.printf("Mouse click %d\n", currentSampleX);
//            this.repaint();
            doMouseClick();
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    private class ChartPanel extends JPanel {
        protected LineChart chart;

        public ChartPanel() {
            chart = new LineChart(null);
            chart.setLineColor(Color.GREEN);
            chart.setHorizontalGap(12);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.white);
            g2d.fillRect(0,0,this.getWidth(), this.getHeight());
            drawChartToGraphics(g2d, getSize());
        }

        public void drawChart(String xLabel, String yLabel, int xMin, int xMax, double[] series) {
            setupChart(xLabel, yLabel, xMin, xMax, series);
            this.repaint();
        }

        protected void setupChart(String xLabel, String yLabel, int xMin, int xMax, double[] series) {
            Series xSeries = new RangeSeries(xLabel, xMin, xMax);
            Series ySeries = new ListSeries(yLabel, series);
            chart.setXSeriers(xSeries);
            chart.setySeries(ySeries);
        }

        protected void drawChartToGraphics(Graphics2D g2d, Dimension size) {
//            RenderingHints rh = new RenderingHints(
//                    RenderingHints.KEY_ANTIALIASING,
//                    RenderingHints.VALUE_ANTIALIAS_ON);
//            g2d.setRenderingHints(rh);

            chart.setDrawSize(size);
            chart.setGraphics2d(g2d);
            chart.draw();
        }
    }

    private void drawWavInfo(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);
        out.println("Wav Info:");
        controller.wav.display(out);
        String content = new String(baos.toByteArray(), StandardCharsets.US_ASCII);

        txtWavInfo.setText(content);
    }

    private void drawSignal() {
        double[] raws = controller.getRawSignal();
        int max = (int) sampleToXTime(raws.length);
        wavChart.drawChart("Time (ms)", "Amplitude", 0, max, raws);
    }

    private void drawFormants() {
        double[][] formants = controller.processFormants();
        if (formants == null || formants.length == 0)
            return;

        int halfWnd = controller.homomorphic.windowSize / 2;
        int gap = (int) sampleToXTime(halfWnd);
        int max = (int) sampleToXTime(controller.getRawSignal().length);
        double[] xs = new double[formants.length];
        xs[0] = gap;
        for (int i = 1; i < formants.length; i++)
            xs[i] = xs[i - 1] + gap;
        this.formantChart.drawChart("Time (ms)", "Frequency (Hz)", 0, max, formants, xs);

    }

    private void process(int offset) {
        controller.process(this, offset);
    }

    @Override
    public void onProcessReturn(boolean success, java.util.List<double[]> result, int offset) {
        if (!success)
            return;

        //draw hamming
        double[] hamming = result.get(0);
        int xmin = (int) sampleToXTime(offset);
        int xmax = (int) sampleToXTime(offset + hamming.length);
        hammingChart.drawChart("Time (ms)", "Amplitude", xmin,
                xmax,
                hamming);
        lbHammingStatus.setText(String.format("Samples: %d to %d", offset, hamming.length + offset));

        double[] mresponse = result.get(1);
        double[] presponse = result.get(2);

        int halflength=mresponse.length/2;
        double[] halfmresponse = new double[halflength];
        for(int i=0;i<halflength;i++)
            halfmresponse[i]=mresponse[i];
        magResponseChart.drawChart("Frequency (Hz)", "Magnitude (dB)", 0, (int) controller.getWav().getSampleRate()/2, halfmresponse);
        phaseResponseChart.drawChart("Frequency (Hz)", "Phase (Radiance)", 0, (int) controller.getWav().getSampleRate(), presponse);

        // formants
        double[] formants = result.get(3);
        if(formants==null){
            lbFormants.setText("Khong co Formant");
        }else{
            StringBuilder str = new StringBuilder();
            str.append("<html>");
            for (int i = 0; i < formants.length; i++) {
                str.append("F[" + (i + 1) + "] = " + (int) formants[i] + "<br>");
            }
            str.append("</html>");
            lbFormants.setText(str.toString());
        }
        wavChart.setCurrentPointer(offset);
        formantChart.setCurrentPointer(offset);
    }

    private double sampleToXTime(int value) {
        if (controller.homomorphic == null) {
            return 0;
        }
        return value * controller.homomorphic.nSamplesToSecond() * 1000;   //ms
    }

    private int xTimeToSample(double value) {
        return (int) (value / (controller.homomorphic.nSamplesToSecond() * 1000));
    }

    private void setState(State state) {
        this.state = state;
        switch (state) {
            case Nothing:
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

    public enum State {
        Nothing,
        Normal,
        Running
    }

}
