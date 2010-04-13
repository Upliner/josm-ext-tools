package fuzzer;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.mapmode.MapMode;
import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.ProjectionBounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.ExceptionDialogUtil;
import org.openstreetmap.josm.gui.PleaseWaitRunnable;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.IllegalDataException;
import org.openstreetmap.josm.io.OsmReader;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.Shortcut;

import wmsplugin.WMSLayer;

/**
 * 
 * @author Upliner
 * 
 */
class FuzzySelectAction extends MapMode {

    private static final long serialVersionUID = 1L;
    protected MapMode oldMapMode;
    protected Process process;
    protected volatile boolean running;

    public FuzzySelectAction() {
        super(tr("Fuzzy select"), "empty", tr("Trace any objects from WMS."),
                Shortcut.registerShortcut("tools:fuzzer", tr("Tool: {0}",
                        tr("Fuzzy Select")), KeyEvent.VK_K,
                        Shortcut.GROUP_EDIT, Shortcut.SHIFT_DEFAULT), null,
                ImageProvider.getCursor("crosshair", null));
        setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (Main.map == null || Main.map.mapView == null)
            return;
        oldMapMode = Main.map.mapMode;
        super.actionPerformed(e);
    }
    
    protected LatLon getPointLatLon(Point point,EastNorth shift)
    {
        if (shift == null)
            return Main.map.mapView.getLatLon(point.x, point.y);
        else
            return Main.proj.eastNorth2latlon(
                   Main.map.mapView.getEastNorth(point.x, point.y)
                   .add(-shift.east(), -shift.north())); 
    }
    
    public double getPPD(){
        ProjectionBounds bounds = Main.map.mapView.getProjectionBounds();
        return Main.map.mapView.getWidth() /
                (bounds.max.east() - bounds.min.east());
    }
    
    protected void runfuzzer(Point clickPoint, final EastNorth shift) {
        try {
            final Object syncObj = new Object();
            // invoke $pluginpath/fuzzyselect.py
            ProcessBuilder builder;
            LatLon pos = getPointLatLon(clickPoint, shift);
            builder = new ProcessBuilder("python", "./fuzzyselect.py",
                    "" + pos.lon(), "" + pos.lat(),
                    "" + getPPD());
            builder.directory(new File(FuzzerPlugin.plugin.getPluginDir()));

            // debug: print cmdline
            for (String s : builder.command())
                System.out.print(s + " ");
            System.out.print("\n");

            process = builder.start();
            running = true;

            // redirect child process's stderr to JOSM stderr
            new Thread(new Runnable() {
                public void run() {
                    try {
                        byte[] buffer = new byte[1024];
                        InputStream errStream = process.getErrorStream();
                        int len;
                        while ((len = errStream.read(buffer)) > 0) {
                            System.err.write(buffer, 0, len);
                        }
                    } catch (IOException e) {
                    }
                }
            }).start();

            // read stdout stream
            Thread osmParseThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        final InputStream inputStream = process.getInputStream();
                        final DataSet ds = OsmReader.parseDataSet(inputStream,
                            NullProgressMonitor.INSTANCE);
                        final LinkedList<Command> cmdlist = new LinkedList<Command>();
                        for (OsmPrimitive p : ds.allPrimitives()) {
                            if (shift != null && p instanceof Node) {
                                final Node n = (Node) p;
                                n.setCoor(Main.proj.eastNorth2latlon(
                                        n.getEastNorth()
                                        .add(shift.east(),shift.north())));
                                cmdlist.add(new AddCommand(n));
                            } else {
                                cmdlist.add(new AddCommand(p));
                            }
                        }
                        if (!cmdlist.isEmpty()) {
                            SequenceCommand cmd =
                                new SequenceCommand("FuzzySelect", 
                                        new DataSetToCmd(ds).getCommandList());
                            Main.main.undoRedo.add(cmd);
                        }
                    } catch (IllegalDataException e) {
                        e.printStackTrace();
                        if (running) {
                            process.destroy();
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    JOptionPane.showMessageDialog(Main.parent,
                                            tr("Child script have returned invalid data."));
                                }
                            });
                        }
                    } finally {
                        synchronized (syncObj) {
                            running = false;
                            syncObj.notifyAll();
                        }
                    }
                }

            });
            osmParseThread.start();

            synchronized (syncObj) {
                syncObj.wait(10000);
            }
            if (running) {
                new Thread(new PleaseWaitRunnable("Tracing") {
                    @Override
                    protected void realRun() {
                        try {
                            progressMonitor.indeterminateSubTask(null);
                            synchronized (syncObj) {
                                if (running)
                                    syncObj.wait();
                            }
                        } catch (InterruptedException e) {
                        }
                    }

                    @Override
                    protected void cancel() {
                        FuzzySelectAction.this.cancel();
                        synchronized (syncObj) {
                            syncObj.notifyAll();
                        }
                    }

                    @Override
                    protected void finish() {
                    }
                }).start();
            }
        } catch (final Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ExceptionDialogUtil.explainException(e);
                }
            });
        }
    }

    public void cancel() {
        if (running) {
            running = false;
            process.destroy();
        }
        if (Main.map == null || Main.map.mapView == null)
            return;
        Main.map.selectMapMode(oldMapMode);
    }

    @Override
    public void enterMode() {
        super.enterMode();
        Main.map.mapView.addMouseListener(this);
    }

    @Override
    public void exitMode() {
        super.exitMode();
        Main.map.mapView.removeMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (Main.map == null || Main.map.mapView == null || running) {
            cancel();
            return;
        }
        EastNorth shift = null;
        if (FuzzerPlugin.haveWmsPlugin) {
            List<WMSLayer> wmsLayers = Main.map.mapView.getLayersOfType(WMSLayer.class);
            if (!wmsLayers.isEmpty()) {
                for (WMSLayer l : wmsLayers) {
                    if (l.isVisible()) {
                        shift = new EastNorth(l.getDx(),l.getDy());
                        break;
                    }
                }
            }
        }
        Main.map.mapView.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        runfuzzer(e.getPoint(), shift);
        Main.map.selectMapMode(oldMapMode);
    }

    @Override
    public boolean layerIsSupported(Layer l) {
        return l instanceof OsmDataLayer;
    }
}
