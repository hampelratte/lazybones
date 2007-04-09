package lazybones.gui.settings.channelpanel.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public abstract class ChannelSetTransferHandler extends TransferHandler {
    
    protected abstract ChannelSet exportChannels(JComponent c);
    protected abstract void importChannels(JComponent c, ChannelSet set);
    protected abstract void cleanup(JComponent c, boolean remove);
    
    protected Transferable createTransferable(JComponent c) {
        return exportChannels(c);
    }
    
    public int getSourceActions(JComponent c) {
        return MOVE;
    }
    
    public boolean importData(JComponent c, Transferable t) {
        if (canImport(c, t.getTransferDataFlavors())) {
            try {
                ChannelSet set = (ChannelSet)t.getTransferData(ChannelSet.FLAVOR);
                importChannels(c, set);
                return true;
            } catch (UnsupportedFlavorException ufe) {
            } catch (IOException ioe) {
            }
        }

        return false;
    }
    
    protected void exportDone(JComponent c, Transferable data, int action) {
        cleanup(c, action == MOVE);
    }
    
    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        for (int i = 0; i < flavors.length; i++) {
            if (ChannelSet.FLAVOR.equals(flavors[i])) {
                return true;
            }
        }
        return false;
    }
}
