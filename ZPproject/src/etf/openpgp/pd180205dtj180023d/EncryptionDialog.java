package etf.openpgp.pd180205dtj180023d;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EncryptionDialog extends Dialog {
    public EncryptionDialog(Frame owner) {
        super(owner,"Encryption", true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                reset();
                setVisible(false); }
        });
        setSize(350, 350);
        setLocation(450, 100);
        setLayout(new GridLayout(4,2,10,10));
        fillScreen();
    }

    private void reset() {

    }

    private void fillScreen(){

    }
}
