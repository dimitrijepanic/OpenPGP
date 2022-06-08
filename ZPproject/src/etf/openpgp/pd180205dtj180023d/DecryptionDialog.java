package etf.openpgp.pd180205dtj180023d;

import org.bouncycastle.openpgp.PGPSecretKey;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DecryptionDialog extends Dialog {
    private FileDialog fd;
    private String filename;
    private String selectedPassword;
    Label l=new Label();
    Label label=new Label();

    public DecryptionDialog(Frame owner) {
        super(owner);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                setVisible(false);
                reset();
            }
        });
        setSize(400, 200);
        setLocation(500, 200);
        setLayout(new GridLayout(4,2,10,10));
        fillScreen();
    }

    private void fillScreen(){
        setFont(new Font("Serif", Font.PLAIN, 15));
        Panel p=new Panel(new GridLayout(3,1));
        setLayout(new BorderLayout());
        add(p,BorderLayout.NORTH);
        Button choose=new Button("Choose file to decrypt");
        l.setFont(new Font("Serif", Font.PLAIN, 15));
        l.setForeground(Color.RED);
        choose.addActionListener(b->{
            if(fd==null) {
                fd=new FileDialog(this);
            }
            fd.setVisible(true);
            filename=fd.getDirectory()+"\\"+fd.getFile();
            label.setText(fd.getFile());
        });
        Button decrypt=new Button("decrypt");
        decrypt.addActionListener(button->{
            try {
                PGPProtocol.decrypt(filename,((AppMainFrame)getParent()).getKeyRings(), (key)->{
                    PasswordDialog dialog=new PasswordDialog(DecryptionDialog.this,key);
                    dialog.setVisible(true);
                    return selectedPassword;
                });
                DecryptionDialog.this.setVisible(false);
            } catch (PGPException e) {
                l.setText("ERROR:"+ e.getMessage());
            }
        });
        add(decrypt, BorderLayout.SOUTH);
        p.add(choose);
        p.add(label);
        p.add(l);
    }

    public class PasswordDialog extends Dialog{
        public PasswordDialog(Dialog owner, PGPSecretKey key) {
            super(owner);
            setModal(true);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    setVisible(false);
                }
            });
            setSize(300, 200);
            setLocation(550, 200);
            Panel p=new Panel(new GridLayout(3,2));
            setLayout(new BorderLayout());
            add(p,BorderLayout.NORTH);
            Label l2=new Label("User: ");
            Label user=new Label(key.getUserIDs().next());
            Label l=new Label("Key: ");
            Label keyid=new Label(Long.toUnsignedString(key.getKeyID()));
            Label pass=new Label("Password");
            p.add(l2);
            p.add(user);
            p.add(l);
            p.add(keyid);
            p.add(pass);
            JPasswordField tf=new JPasswordField(10);
            p.add(tf);
            Button addPassword=new Button("setPassword");
            addPassword.addActionListener(butt->{
                selectedPassword=String.copyValueOf(tf.getPassword());
                setVisible(false);
            });
            add(addPassword, BorderLayout.SOUTH);
        }
    }

    public void reset(){
        filename=null;
        label.setText("");
        selectedPassword=null;
        l.setText("");
    }
}
