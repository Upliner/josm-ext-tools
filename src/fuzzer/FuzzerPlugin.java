package fuzzer;

import java.io.File;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.preferences.PreferenceSetting;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginHandler;
import org.openstreetmap.josm.plugins.PluginInformation;

import fuzzer.preferences.ExtToolsPreference;

public class FuzzerPlugin extends Plugin {
    public static FuzzerPlugin plugin;
    public static boolean haveWmsPlugin;

    public FuzzerPlugin(PluginInformation info) {
        super(info);
        MainMenu.add(Main.main.menu.toolsMenu, new FuzzySelectAction());
        if (!(new File(this.getPluginDir(), "fuzzyselect.py")).exists()) {
            try {
                copy("resources/fuzzyselect.py", "fuzzyselect.py");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        plugin = this;
        haveWmsPlugin = false;
    }

    @Override
    public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
        if (PluginHandler.getPlugin("wmsplugin") != null)
            haveWmsPlugin = true;
    }

    @Override
    public PreferenceSetting getPreferenceSetting() {
        return new ExtToolsPreference();
    }
}
