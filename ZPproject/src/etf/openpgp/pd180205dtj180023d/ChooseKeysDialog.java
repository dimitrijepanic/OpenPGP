package etf.openpgp.pd180205dtj180023d;

import org.bouncycastle.openpgp.PGPPublicKeyRing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ChooseKeysDialog extends Dialog {


    // panels for keys
    private Panel keyPanel;
    private JScrollPane p1;
    private JTable table=new JTable();


    public ChooseKeysDialog(Dialog owner, boolean ispublic) {
        super(owner);
        setSize(600, 600);
        setLocation(400, 50);
        setLayout(new BorderLayout());
        fillScreen(ispublic);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                setVisible(false);
//                reset();
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                DefaultTableModel model = new DefaultTableModel(new Object[][] {}, new String[] {"userId", "keyId", "timestamp"});
                table.setModel(model);
                List<MyKeyRing> rings=((AppMainFrame)(getParent()).getParent()).getKeyRings();
                rings.forEach(ring->{
                    ((DefaultTableModel)table.getModel()).addRow(Util.generateTableRow(ring));
                });
            }
        });


    }

    private void fillScreen(boolean ispublic) {
        Panel panel = new Panel();
        //Button b1 = new Button("Add New Key");

        add(panel, BorderLayout.PAGE_START);
        keyPanel = new Panel();
        if(!ispublic) table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        List<MyKeyRing> rings=((AppMainFrame)(getParent()).getParent()).getKeyRings();
        p1 = new JScrollPane(table);
        keyPanel.add(p1);
        add(keyPanel, BorderLayout.CENTER);
        Button choose=new Button((ispublic)?"Select public keys":"Select private key");
        choose.addActionListener(button->{
            int[] rows=table.getSelectedRows();
            ArrayList<MyKeyRing> selectedRings=new ArrayList<>();
            for(int row:rows){
                selectedRings.add(rings.get(row));
            }
            if(ispublic)((EncryptionDialog)getParent()).setEncryptionKeyRings(selectedRings);
            else ((EncryptionDialog)getParent()).setSignatureKeyRing(selectedRings.get(0));
            setVisible(false);
        });
        add(choose,BorderLayout.SOUTH);
    }
}
