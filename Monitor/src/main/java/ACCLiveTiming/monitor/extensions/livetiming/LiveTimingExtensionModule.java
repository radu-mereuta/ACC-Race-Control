/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ACCLiveTiming.monitor.extensions.livetiming;

import ACCLiveTiming.ACCLiveTimingExtensionModule;
import ACCLiveTiming.monitor.extensions.AccClientExtension;
import ACCLiveTiming.monitor.visualisation.gui.LPContainer;
import javax.swing.JPanel;

/**
 *
 * @author Leonard
 */
public class LiveTimingExtensionModule
        implements ACCLiveTimingExtensionModule {

    @Override
    public String getName() {
        return "Live Timing Extension";
    }

    @Override
    public AccClientExtension getExtension() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LPContainer getExtensionPanel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JPanel getExtensionConfigurationPanel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
