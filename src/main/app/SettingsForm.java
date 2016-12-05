package app;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Function;

/**
 * Created by dung on 04/12/2016.
 */
public class SettingsForm extends JDialog {
    public SettingsForm(JFrame parent, SettingModel setting, CallbackListener<SettingModel> callback) {
        super(parent, "Settings", true);

        //init ui
        Box root = Box.createVerticalBox();

        JLabel lb1 = new JLabel("C(n) size:");
        JTextField txt_cn = new JTextField("" +setting.cn);
        JLabel lb3 = new JLabel("Windows Size:");
        JTextField txt_wnd = new JTextField(String.format("%d",setting.windowsize));
        JLabel lb2 = new JLabel("Delay Run Time:");
        JTextField txt_delay = new JTextField(String.format("%.1f",setting.delay));


        root.add(lb1);
        root.add(txt_cn);
        root.add(lb3);
        root.add(txt_wnd);
        root.add(lb2);
        root.add(txt_delay);

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener((ActionEvent event)->{
            try{
                int cn = Integer.parseInt(txt_cn.getText());
                int wndsize = Integer.parseInt(txt_wnd.getText());
                float delay = Float.parseFloat(txt_delay.getText());
                callback.onCallback(new SettingModel(cn,wndsize,delay));
                setVisible(false);
                this.dispose();
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        });

        root.add(btnOk);

//        root.setPreferredSize(new Dimension(360, 300));

        setContentPane(root);
        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - root.getPreferredSize().width) / 2,
                (screenSize.height - root.getPreferredSize().height) / 2);

        setVisible(true);
    }

    public static class SettingModel{
        public int cn;
        public int windowsize;
        public float delay;

        public SettingModel(int cn, int windowsize, float delay){
            this.cn = cn;
            this.windowsize = windowsize;
            this.delay = delay;
        }
    }
}
