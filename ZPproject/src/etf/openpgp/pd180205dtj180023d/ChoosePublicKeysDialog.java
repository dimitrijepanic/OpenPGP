package etf.openpgp.pd180205dtj180023d;

import org.bouncycastle.openpgp.PGPPublicKeyRing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class ChoosePublicKeysDialog extends Dialog {


    // panels for keys
    private Panel keyPanel;
    private JScrollPane p1;
    private JTable table;


    public ChoosePublicKeysDialog(Dialog owner) {
        super(owner);
        setSize(600, 600);
        setLocation(400, 50);
        setLayout(new BorderLayout());
        fillScreen();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                setVisible(false);
//                reset();
            }
        });
    }

    private void fillScreen() {
        Panel panel = new Panel();
        //Button b1 = new Button("Add New Key");

        add(panel, BorderLayout.PAGE_START);

        keyPanel = new Panel();
        table = new JTable();
        DefaultTableModel model = new DefaultTableModel(new Object[][] {}, new String[] {"userId", "keyId", "timestamp"});
        table.setModel(model);
        List<MyKeyRing> rings=((AppMainFrame)(getParent()).getParent()).getKeyRings();
        rings.forEach(ring->{
            ((DefaultTableModel)table.getModel()).addRow(Util.generateTableRow(ring));
        });
        p1 = new JScrollPane(table);
        keyPanel.add(p1);
        add(keyPanel, BorderLayout.CENTER);
        Button choose=new Button("Select public keys");
        choose.addActionListener(button->{
            int[] rows=table.getSelectedRows();
            ArrayList<PGPPublicKeyRing> selectedRings=new ArrayList<>();
            for(int row:rows){
                selectedRings.add(rings.get(row).getPublicKeyRing());
            }
            ((EncryptionDialog)getParent()).setEncryptionKeyRings(selectedRings);
            setVisible(false);
        });
        add(choose,BorderLayout.SOUTH);
    }
}
