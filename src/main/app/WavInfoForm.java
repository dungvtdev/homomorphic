package app;

import io.file.WavFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by dung on 04/12/2016.
 */
public class WavInfoForm extends JDialog implements ActionListener{
    public WavInfoForm(JFrame parent, String title, WavFile wav){
        super(parent, title, true);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);

        Box root = Box.createVerticalBox();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);
        wav.display(out);
        String content = new String(baos.toByteArray(), StandardCharsets.US_ASCII);

        JTextArea txt = new JTextArea(100, 58);
        txt.setEditable(false); // set textArea non-editable
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
//        txt.setAlignmentX(Component.CENTER_ALIGNMENT);
//        JScrollPane scroll = new JScrollPane(txt);
//        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        txt.setText(content);

        //Add Textarea in to middle panel
        root.add(txt);

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(this);
        btnOk.setAlignmentX(Component.CENTER_ALIGNMENT);
//        btnOk.setMaximumSize(new Dimension(80,25));
        root.add(btnOk);


        root.setPreferredSize(new Dimension(360,300));

        setContentPane(root);
        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        root.setLocation((screenSize.width-root.getPreferredSize().width)/2,
                (screenSize.height-root.getPreferredSize().height)/2);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setVisible(false);
        dispose();
    }
}
