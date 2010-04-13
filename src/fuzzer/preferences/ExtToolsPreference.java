package fuzzer.preferences;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openstreetmap.josm.gui.preferences.PreferenceSetting;
import org.openstreetmap.josm.gui.preferences.PreferenceTabbedPane;
import org.openstreetmap.josm.tools.GBC;

public class ExtToolsPreference implements PreferenceSetting {

    @Override
    public void addGui(PreferenceTabbedPane gui) {
        JPanel p = gui.createPreferenceTab("ext", tr("External tools"), tr("Use external scripts in JOSM"));
        ToolsListPanel tp = new ToolsListPanel();
        tp.refresh();
        JScrollPane sp = new JScrollPane(tp);
        p.add(sp,GBC.eol().fill(GridBagConstraints.BOTH));
    }

    @Override
    public boolean ok() {
        // TODO Auto-generated method stub
        return false;
    }

}
