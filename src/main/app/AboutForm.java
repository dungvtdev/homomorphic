package app;

import io.file.WavFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by dung on 04/12/2016.
 */
public class AboutForm extends JDialog implements ActionListener{
    public AboutForm(JFrame parent){
        super(parent, "About", true);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);

        Box root = Box.createVerticalBox();

        JTextArea txt = new JTextArea(100, 58);
        txt.setEditable(false); // set textArea non-editable
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);

        String content = "BTL Môn học Xử lý tiếng nói.\n"+
                         "Đề bài: Xác định formant và đáp ứng tần số của tín hiệu tiếng nói.\n"+
                         "Giáo viên giảng dạy: Thầy Trịnh Văn Loan."+
                         "Sinh viên: Vũ Trung Dũng - KSTN CNTT K57.";
        txt.setText(content);

        //Add Textarea in to middle panel
        root.add(txt);

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(this);
        btnOk.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(btnOk);

        root.setPreferredSize(new Dimension(360,200));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        root.setLocation((screenSize.width-root.getPreferredSize().width)/2,
                (screenSize.height-root.getPreferredSize().height)/2);
        setContentPane(root);
        pack();

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setVisible(false);
        dispose();
    }
}
