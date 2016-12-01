/**
 * Created by dung on 01/12/2016.
 */
import app.*;

import java.awt.*;

public class Main {
    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            MainForm ex = new MainForm();
            ex.setVisible(true);
        });
    }
}
