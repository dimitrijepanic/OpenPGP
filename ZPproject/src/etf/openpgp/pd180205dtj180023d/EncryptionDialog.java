package etf.openpgp.pd180205dtj180023d;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

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


    private java.util.List<NavigationPanel> panels=new ArrayList<>();
    private Panel content =new Panel();
    private Button next;
    private int index=0;
    private void reset() {
        panels.forEach(p->p.setVisible(false));
        panels.get(0).setVisible(true);
        index=0;
    }

    private void fillScreen(){
        setFont(new Font("Serif", Font.PLAIN, 15));
        setLayout(new BorderLayout());
        content.setLayout(new CardLayout());
        panels.add(new InitialPanel());
        panels.add(new AutenticationPanel());
        panels.add(new EncryptionPanel());
        content.add(panels.get(0));
        content.add(panels.get(1));
        content.add(panels.get(2));
        Panel bottom=new Panel(new BorderLayout());
        next=new Button("finish");
        next.addActionListener(button->{
            navigate();
        });
        bottom.add(next,BorderLayout.CENTER);
        add(content, BorderLayout.NORTH);
        add(bottom,BorderLayout.SOUTH);

    }


    private void navigate(){
        panels.forEach(p->p.setVisible(false));
        panels.get(index).nextPanel().setVisible(true);
    }

    private Checkbox autentication;
    private Checkbox compression;
    private Checkbox encryption;
    private Checkbox compatibility;

    private abstract class NavigationPanel extends Panel{
        public abstract NavigationPanel nextPanel();
    }
    private class InitialPanel extends NavigationPanel{
        public InitialPanel() {
            setLayout(new GridLayout(3,2));
            Label label=new Label("Choose feature: ");
            Label labele=new Label();
            add(label);
            add(labele);
            autentication=new Checkbox("autentication");
            compression=new Checkbox("compression");
            encryption=new Checkbox("encryption");
            compatibility=new Checkbox("compatibility");
            autentication.addItemListener(e->{
                changeNextLabel();
            });
            encryption.addItemListener(e->{
                changeNextLabel();
            });
            add(autentication);
            add(compression);
            add(encryption);
            add(compatibility);
        }

        @Override
        public NavigationPanel nextPanel() {
            if(autentication.getState()) {
                index++;
                autentication.setState(false);
                changeNextLabel();
                return panels.get(1);

            };
            if(encryption.getState()) {
                index+=2;
                encryption.setState(false);
                changeNextLabel();
                return panels.get(2);
            }
            else return panels.get(0);//promeni
        }
    }

    private class EncryptionPanel extends NavigationPanel{

        public EncryptionPanel() {
            Label l=new Label("EncryptionPanel");
            add(l);
        }
        @Override
        public NavigationPanel nextPanel() {
            return panels.get(2);//promeni
        }
    }

    private void changeNextLabel(){
        if(autentication.getState()||encryption.getState())next.setLabel("next");
        else next.setLabel("finish");
    }

    private class AutenticationPanel extends NavigationPanel{
        public AutenticationPanel() {
            Label l=new Label("Autentication");
            add(l);
        }

        @Override
        public NavigationPanel nextPanel() {
            if(encryption.getState()) {
                encryption.setState(false);
                index++;
                changeNextLabel();
                return panels.get(2);
            }
            else return panels.get(1);//promeni
        }
    }

}
