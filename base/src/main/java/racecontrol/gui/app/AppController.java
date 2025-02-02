/**
 * Copyright (c) 2021 Leonard Sch�ngel
 *
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app;

import racecontrol.gui.app.settings.SettingsPage;
import racecontrol.gui.app.statuspanel.StatusPanelManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import racecontrol.Main;
import racecontrol.gui.app.logging.LoggingPanel;
import racecontrol.gui.app.racecontrol.RaceControlController;
import racecontrol.client.AccBroadcastingClient;
import racecontrol.client.events.ConnectionClosedEvent;
import racecontrol.client.events.ConnectionOpenedEvent;
import racecontrol.client.events.RealtimeUpdateEvent;
import racecontrol.client.events.RegistrationResultEvent;
import racecontrol.eventbus.Event;
import racecontrol.eventbus.EventBus;
import racecontrol.eventbus.EventListener;
import racecontrol.gui.RaceControlApplet;
import racecontrol.gui.app.livetiming.LiveTimingController;
import racecontrol.gui.lpui.LPComponent;
import racecontrol.gui.lpui.LPContainer;

/**
 *
 * @author Leonard
 */
public class AppController
        implements EventListener {

    /**
     * Singelton instance.
     */
    private static AppController instance;

    /**
     * The GUI component.
     */
    private AppPanel appPanel;
    /**
     * Settings page.
     */
    private SettingsPage settingsPage;
    /**
     * Live timing page controller.
     */
    private LiveTimingController liveTimingController;
    /**
     * Placeholder panel for a detached page.
     */
    private DetachedPlaceholderPanel detachedPlaceholderPanel;
    /**
     * Manages the status panels.
     */
    private StatusPanelManager statusPanelManager;
    /**
     * Is initialised.
     */
    private boolean initialised;
    /**
     * Map of components to their window applet.
     */
    private final Map<LPComponent, PanelWindowApplet> windowPanels = new HashMap<>();

    public static AppController getInstance() {
        if (instance == null) {
            instance = new AppController();
            instance.initialise();
        }
        return instance;
    }

    private AppController() {
    }

    private void initialise() {
        if (initialised) {
            return;
        }
        initialised = true;

        EventBus.register(this);

        appPanel = new AppPanel();
        settingsPage = new SettingsPage();
        liveTimingController = new LiveTimingController();
        detachedPlaceholderPanel = new DetachedPlaceholderPanel();

        statusPanelManager = StatusPanelManager.getInstance();
        statusPanelManager.setTargetPanel(appPanel);

        setupMenu();
    }

    private void setupMenu() {
        List<PageController> pageControllers = new ArrayList<>();
        pageControllers.add(liveTimingController);
        pageControllers.add(RaceControlController.getInstance());
        pageControllers.add(new LoggingPanel());
        //pageControllers.add(new TestPanel());
        //pageControllers.add(new DangerDetectionController());
        //pageControllers.add(new TrackDataController());

        // Add page controllers from extension modules.
        Main.getModules().forEach(module -> {
            var page = module.getPageController();
            if (page != null) {
                pageControllers.add(page);
            }
        });

        // set menu items
        for (var page : pageControllers) {
            appPanel.menu.addMenuItem(page.getMenuItem());
            page.getMenuItem().setClickAction(button -> menuItemClicked(page, button));
        }
        appPanel.menu.addMenuItemBottom(settingsPage.getMenuItem());
        settingsPage.getMenuItem().setClickAction(b -> menuItemClicked(settingsPage, b));

        appPanel.setActivePage(settingsPage);
        appPanel.menu.setSelectedMenuItem(settingsPage.getMenuItem());
    }

    public LPComponent getGUIComponent() {
        return appPanel;
    }

    private void menuItemClicked(PageController page, int button) {
        LPContainer panel = page.getPanel();
        if (panel != null) {
            if (button == LEFT) {
                if (windowPanels.containsKey(panel)) {
                    // panel is detached so we grab the focus instead.
                    windowPanels.get(panel).grabFocus();
                    detachedPlaceholderPanel.setPanelName(panel.getName());
                    panel = detachedPlaceholderPanel;
                }
                appPanel.setActivePage(panel);
                appPanel.updateComponents();
                appPanel.invalidate();
            }

            if (button == CENTER
                    && panel != settingsPage
                    && appPanel.menu.getSelectedItem() == page.getMenuItem()) {
                detachPage(page);
            }
        }
    }

    private void detachPage(PageController page) {
        detachedPlaceholderPanel.setPanelName(page.getPanel().getName());
        detachedPlaceholderPanel.invalidate();
        appPanel.removeComponent(page.getPanel());
        appPanel.setActivePage(detachedPlaceholderPanel);
        appPanel.updateComponents();
        appPanel.invalidate();

        PanelWindowApplet applet = launchNewWindow(page.getPanel(), true);
        applet.addCloseAction(() -> {
            if (appPanel.menu.getSelectedItem() == page.getMenuItem()) {
                appPanel.setActivePage(page.getPanel());
                appPanel.updateComponents();
                appPanel.invalidate();
            }
        });
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof RealtimeUpdateEvent) {
            RaceControlApplet.runLater(() -> {
                appPanel.header.invalidate();
            });
        } else if (e instanceof ConnectionOpenedEvent) {

        } else if (e instanceof RegistrationResultEvent) {
            if (((RegistrationResultEvent) e).isSuccess()) {
                RaceControlApplet.runLater(() -> {
                    appPanel.setActivePage(liveTimingController.getPanel());
                    appPanel.menu.setSelectedMenuItem(appPanel.liveTimingMenuItem);
                    appPanel.updateComponents();
                    appPanel.invalidate();
                });
            }
            if (((RegistrationResultEvent) e).isReadOnly()) {
                RaceControlApplet.runLater(() -> {
                    statusPanelManager.addStatusPanel(new ConnectionReadOnlyStatusPanel());
                });
            }
        } else if (e instanceof ConnectionClosedEvent) {
            RaceControlApplet.runLater(() -> {
                ConnectionClosedEvent event = (ConnectionClosedEvent) e;
                if (event.getExitState() == AccBroadcastingClient.ExitState.NORMAL) {
                    appPanel.setActivePage(settingsPage);
                    appPanel.updateComponents();
                } else if (event.getExitState() == AccBroadcastingClient.ExitState.TIMEOUT) {
                    statusPanelManager.addStatusPanel(new ConnectionTimeoutStatusPanel());
                }
                appPanel.invalidate();
            });
        }
    }

    /**
     * Creates a new window for a panel. Does not create a new window if the
     * panel already is assigned to a window.
     *
     * @param panel The panel to create a window for.
     * @param resizeable Is that panel resizable.
     * @return The applet for that panel.
     */
    public PanelWindowApplet launchNewWindow(LPComponent panel, boolean resizeable) {
        // only create a window if that panel doesnt already have one.
        if (!windowPanels.containsKey(panel)) {
            panel.setPosition(0, 0);
            panel.setVisible(true);
            panel.setEnabled(true);
            PanelWindowApplet applet = new PanelWindowApplet(panel, resizeable);
            applet.addCloseAction(() -> {
                windowPanels.remove(panel);
            });
            windowPanels.put(panel, applet);
        } else {
            windowPanels.get(panel).grabFocus();
        }
        return windowPanels.get(panel);
    }

}
