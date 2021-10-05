/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.racecontrol.googlesheetsapi;

import java.util.logging.Logger;
import racecontrol.gui.app.AppController;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsAPIExtension;
import racecontrol.client.extension.googlesheetsapi.GoogleSheetsConfiguration;
import racecontrol.gui.lpui.LPTabPanel;

/**
 *
 * @author Leonard
 */
public class GoogleSheetsAPIConfigurationController {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(GoogleSheetsAPIConfigurationController.class.getName());
    /**
     * Reference to the app controller.
     */
    private final AppController appController;

    private final LPTabPanel tabPanel = new LPTabPanel();

    private final GoogleSheetsAPIConfigurationPanel panel;

    private final GoogleSheetsAPIExtension sheetsAPI;

    public GoogleSheetsAPIConfigurationController() {
        appController = AppController.getInstance();
        panel = new GoogleSheetsAPIConfigurationPanel();
        panel.connectButton.setAction(() -> connectButton());
        sheetsAPI = GoogleSheetsAPIExtension.getInstance();

        tabPanel.addTab(panel);
        tabPanel.setSize(660, 460);
        tabPanel.setName("Google Sheets API");
    }

    private void connectButton() {
        if (!sheetsAPI.isRunning()) {
            //enable spreadsheet service.
            sheetsAPI.start(new GoogleSheetsConfiguration(
                    panel.spreadSheetLinkTextField.getValue(),
                    panel.credentialsFileTextField.getValue(),
                    panel.findRowRangeTextField.getValue(),
                    panel.replayOffsetTextField.getValue(),
                    panel.sessionColumnTextField.getValue(),
                    panel.carColumnTextField.getValue()
            ));
            tabPanel.addTab(sheetsAPI.getPanel());
            panel.allowInput = false;
        } else {
            //disable spreadsheet service.
            sheetsAPI.stop();
            tabPanel.removeTab(sheetsAPI.getPanel());
            panel.allowInput = true;
        }
        panel.updateComponents();
        panel.invalidate();
    }

    public void openSettingsPanel() {
        appController.launchNewWindow(tabPanel, false);
    }

}
