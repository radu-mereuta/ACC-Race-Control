package racecontrol.gui.app.autobroadcast;

import processing.core.PApplet;
import static racecontrol.gui.LookAndFeel.COLOR_DARK_GRAY;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.LPCheckBox;
import racecontrol.gui.lpui.LPContainer;
import racecontrol.gui.lpui.LPLabel;
import racecontrol.gui.lpui.table.LPTable;

/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
/**
 *
 * @author Leonard
 */
public class AutobroadcastPanel
        extends LPContainer {

    protected final LPCheckBox enableCheckBox = new LPCheckBox();
    private final LPLabel enableLabel = new LPLabel("Enable autopilot");

    protected final LPCheckBox sortByRatingCheckBox = new LPCheckBox();
    private final LPLabel sortByRatingLabel = new LPLabel("Sort by rating");
    protected final LPTable ratingTable = new LPTable();

    public AutobroadcastPanel() {
        enableCheckBox.setPosition(20, 10);
        addComponent(enableCheckBox);
        enableLabel.setPosition(50, 0);
        addComponent(enableLabel);

        addComponent(sortByRatingCheckBox);
        addComponent(sortByRatingLabel);
        addComponent(ratingTable);
    }

    @Override
    public void draw(PApplet applet) {
        applet.fill(COLOR_DARK_GRAY);
        applet.rect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void onResize(float w, float h) {
        ratingTable.setPosition(10, h - LINE_HEIGHT * 10 - 10);
        ratingTable.setSize(w - 20, LINE_HEIGHT * 10);

        sortByRatingCheckBox.setPosition(20, h - LINE_HEIGHT * 11);
        sortByRatingLabel.setPosition(50, h - LINE_HEIGHT * 11 - 10);
    }

}
