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
        JLabel lb2 = new JLabel("Delay Run Time:");
        JTextField txt_delay = new JTextField(String.format("%.1f",setting.delay));

        root.add(lb1);
        root.add(txt_cn);
        root.add(lb2);
        root.add(txt_delay);

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener((ActionEvent event)->{
            try{
                int cn = Integer.parseInt(txt_cn.getText());
                float delay = Float.parseFloat(txt_delay.getText());
                callback.onCallback(new SettingModel(cn,delay));
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
        root.setLocation((screenSize.width - root.getPreferredSize().width) / 2,
                (screenSize.height - root.getPreferredSize().height) / 2);

        setVisible(true);
    }

    public static class SettingModel{
        public int cn;
        public float delay;

        public SettingModel(int cn, float delay){
            this.cn = cn;
            this.delay = delay;
        }
    }
}
