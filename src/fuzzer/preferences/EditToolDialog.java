package fuzzer.preferences;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.tools.GBC;

public class EditToolDialog extends JDialog {
    ExtTool tool;
    
    private JTextField name = new JTextField();
    private JTextField cmdline = new JTextField();

    
    private void addLabelled(String str, Component c) {
        JLabel label = new JLabel(str);
        add(label, GBC.std());
        label.setLabelFor(c);
        add(c, GBC.eol().fill(GBC.HORIZONTAL));
    }
    private void load()
    {
        name.setText(tool.name);
        cmdline.setText(tool.cmdline);
    }
    private void save()
    {
        tool.name = name.getText();
        tool.cmdline = cmdline.getText();
    }
    public EditToolDialog(ExtTool tool)
    {
        super(JOptionPane.getFrameForComponent(Main.parent),"Edit tool",true);
        this.tool = tool;
        setLayout(new GridBagLayout());
        addLabelled("Name:", name);
        addLabelled("CmdLine:", cmdline);
        load();
        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
                setVisible(false);
            }
        });
        add(btnOk, GBC.std());
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        add(btnCancel, GBC.eol());
        pack();
    }
}
