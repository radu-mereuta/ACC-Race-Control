/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.extensions.incidents;

import base.screen.extensions.AccClientExtension;
import base.screen.visualisation.gui.LPContainer;

/**
 *
 * @author Leonard
 */
public class IncidentExtensionFactory
        implements base.ACCLiveTimingExtensionFactory {

    private IncidentExtension extension;

    @Override
    public String getName() {
        return "Incident extension";
    }

    @Override
    public void createExtension() {
        removeExtension();
        extension = new IncidentExtension();
    }

    @Override
    public LPContainer getExtensionConfigurationPanel() {
        return null;
    }

    @Override
    public void removeExtension() {
        if (extension != null) {
            extension.removeExtension();
            extension = null;
        }
    }

    @Override
    public AccClientExtension getExtension() {
        return extension;
    }

}
