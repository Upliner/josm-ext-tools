import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class ToolsListPanel extends JPanel {
    public ToolsListPanel() {
        super(new GridBagLayout());
    }
    public void refresh()
    {
        removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2,5,2,5);
        
        for (final ExtTool tool : ExtTool.getToolsList()) {
            gbc.gridx = 0;
            gbc.weightx = 1.0;
            gbc.anchor = GridBagConstraints.WEST;
            
            final JCheckBox cbTool = new JCheckBox(tool.getName());
            cbTool.setSelected(tool.isEnabled());
            cbTool.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    tool.setEnabled(cbTool.isSelected());
                }
            });            
            add(cbTool, gbc);
            
            gbc.gridx = 1;
            gbc.weightx = 0;
            gbc.anchor = GridBagConstraints.EAST;
            
            final JButton bEdit = new JButton("Edit");
            bEdit.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    //JOptionPane.showMessageDialog(null, tool.getName() + " edited!");
                    JDialog dlg = new EditToolDialog(null, tool);
                    dlg.setVisible(true);
                    dlg.dispose();
                    refresh();
                }
            });
            add(bEdit, gbc);
            
            gbc.gridy++;
        }
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        
        final JButton bNew = new JButton("New tool...");
        bNew.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                ExtTool tool = new ExtTool();
                JDialog dlg = new EditToolDialog(null, tool);
                dlg.setVisible(true);
                dlg.dispose();
                if (tool.name != null)
                    if (!tool.name.equals(""))
                        ExtTool.addTool(tool);
                refresh();
            }
        });
        add(bNew, gbc);
        gbc.gridy++;        
        
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JPanel(), gbc);
        revalidate();
        repaint();
    }

}
