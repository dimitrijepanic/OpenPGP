package etf.openpgp.pd180205dtj180023d;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DeleteKeyDialog extends Dialog {

	public DeleteKeyDialog(Frame frame) {
		super(frame, "Delete Key", true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				setVisible(false);
			}
		});
		
		setSize(200, 200);
	}
	
	public void setValues() {
		
	}
}
