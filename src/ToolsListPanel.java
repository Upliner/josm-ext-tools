import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class ToolsListPanel extends JPanel {
    public ToolsListPanel() {
        super(new GridBagLayout());
    }
    public void refresh()
    {
        removeAll();
    	ArrayList<String> tools = new ArrayList<String>();
    	tools.add("Fuzzer");
    	tools.add("Puzzer");
    	tools.add("Muzzer");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2,5,2,5);
        
        for (final String s : tools) {
            gbc.gridx = 0;
            gbc.weightx = 1.0;
            gbc.anchor = GridBagConstraints.WEST;
            
            final JCheckBox cbTool = new JCheckBox(s);
            add(cbTool, gbc);
            
            gbc.gridx = 1;
            gbc.weightx = 0;
            gbc.anchor = GridBagConstraints.EAST;

            
            final JButton bEdit = new JButton("Edit");
            add(bEdit, gbc);
            
            gbc.gridy++;
        }
        
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JPanel(), gbc);
    }

}
