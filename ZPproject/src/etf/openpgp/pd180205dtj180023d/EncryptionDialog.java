package etf.openpgp.pd180205dtj180023d;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class EncryptionDialog extends Dialog {
    public EncryptionDialog(Frame owner) {
        super(owner,"Encryption", true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                setVisible(false);
                reset();
            }
        });
        setSize(350, 350);
        setLocation(450, 100);
        setLayout(new GridLayout(4,2,10,10));
        fillScreen();
    }

    private PGPEncryptor.SymetricKeyAlgorithm symetricKeyAlgorithm;
    private List<PGPProtocol.PGPOptions> options=new ArrayList<>();


    private java.util.List<NavigationPanel> panels=new ArrayList<>();
    private Panel content =new Panel();
    private Button next;
    private int index=0;
    private void reset() {
        symetricKeyAlgorithm=PGPEncryptor.SymetricKeyAlgorithm.CAST5;
        options.clear();
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
        NavigationPanel nextPanel=panels.get(index).nextPanel();
        if(nextPanel!=null){
            nextPanel.setVisible(true);
        }
        else {
            //pokreni enkripciju
            setVisible(false);
            reset();
        }
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

        private void getOptions(){
            if(autentication.getState()) options.add(PGPProtocol.PGPOptions.AUTENTICATION);
            if(encryption.getState()) options.add(PGPProtocol.PGPOptions.ENCRYPTION);
            if(compression.getState()) options.add(PGPProtocol.PGPOptions.COMPRESSION);
            if(compatibility.getState()) options.add(PGPProtocol.PGPOptions.COMPATIBILITY);
        }

        @Override
        public NavigationPanel nextPanel() {
            getOptions();
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
            else return null;
        }
    }



    private class EncryptionPanel extends NavigationPanel{
        private Checkbox cast5;
        private Checkbox trides;
        public EncryptionPanel() {
            setLayout(new GridLayout(2,2));
            Label l=new Label("Select symetric algorythm:");
            Label labele=new Label();
            add(l);
            add(labele);
            CheckboxGroup radio=new CheckboxGroup();
            cast5=new Checkbox("CAST5/128b",radio,true);
            trides=new Checkbox("3DES/EDE",radio,false);
            add(cast5);
            add(trides);
        }
        @Override
        public NavigationPanel nextPanel() {
            if(cast5.getState()) symetricKeyAlgorithm=PGPEncryptor.SymetricKeyAlgorithm.CAST5;
            if(trides.getState()) symetricKeyAlgorithm=PGPEncryptor.SymetricKeyAlgorithm.TRIPLEDES;
            return null;
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
            else return null;
        }
    }

}
