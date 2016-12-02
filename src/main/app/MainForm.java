package app;

import visual.chart.LineChart;
import visual.chart.LineChartPane;
import visual.chart.RangeSeries;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by dung on 01/12/2016.
 */
public class MainForm extends JFrame {
    JLabel lbFileName;

    public MainForm() {

        initUI();
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

        JPanel wavPane = createWavPanel();

        content.add(wavPane);

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

        LineChartPane chart = new LineChartPane();
        chart.setPreferredSize(new Dimension(640,300));

        Box toolpane = Box.createHorizontalBox();
        JButton btnPrev = new JButton("Prev");
        JButton btnPlay = new JButton("Play");
        JButton btnNext = new JButton("Next");

        toolpane.add(btnPrev);
        toolpane.add(btnPlay);
        toolpane.add(btnNext);

        pane.add(lb);
        pane.add(Box.createRigidArea(new Dimension(0,5)));
        pane.add(chart);
        pane.add(Box.createRigidArea(new Dimension(0,5)));
        pane.add(toolpane);

        return pane;
    }

}
