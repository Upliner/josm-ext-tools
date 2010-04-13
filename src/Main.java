import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;


public class Main {

	public static void main(String[] args) {
		JDialog dlg = new JDialog((JFrame)null,"Yeehaw!",true);
		ToolsListPanel p = new ToolsListPanel();
		p.refresh();
		JScrollPane sp = new JScrollPane(p);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		dlg.setContentPane(sp);
		//dlg.pack();
		dlg.setSize(300, 500);
		dlg.setVisible(true);
	}

}

