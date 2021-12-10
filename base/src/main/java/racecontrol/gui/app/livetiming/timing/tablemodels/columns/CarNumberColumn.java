/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels.columns;

import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CLOSE;
import racecontrol.client.data.enums.CarCategory;
import static racecontrol.client.data.enums.CarCategory.CUP;
import static racecontrol.client.data.enums.CarCategory.GT3;
import static racecontrol.client.data.enums.CarCategory.ST;
import static racecontrol.client.extension.statistics.CarProperties.CAR_MODEL;
import static racecontrol.client.extension.statistics.CarProperties.CAR_NUMBER;
import static racecontrol.client.extension.statistics.CarProperties.CATEGORY;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_GT4;
import static racecontrol.gui.LookAndFeel.COLOR_SUPER_TROFEO;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;

/**
 *
 * @author Leonard
 */
public class CarNumberColumn
        extends LPTableColumn {

    public CarNumberColumn() {
        super("#");
        setMinWidth(LINE_HEIGHT * 1.5f);
        setMaxWidth(LINE_HEIGHT * 1.5f);
        setPriority(1000);
        setCellRenderer(this::carNumberRenderer);
    }

    protected void carNumberRenderer(PApplet applet, LPTable.RenderContext context) {
        if (!(context.object instanceof CarStatistics)) {
            return;
        }
        CarStatistics stats = (CarStatistics) context.object;

        int backColor = 0;
        int frontColor = 0;
        switch (stats.get(CATEGORY)) {
            case BRONZE:
                backColor = LookAndFeel.COLOR_RED;
                frontColor = LookAndFeel.COLOR_BLACK;
                break;
            case SILVER:
                backColor = LookAndFeel.COLOR_GRAY;
                frontColor = LookAndFeel.COLOR_WHITE;
                break;
            case GOLD:
            case PLATINUM:
                backColor = LookAndFeel.COLOR_WHITE;
                frontColor = LookAndFeel.COLOR_BLACK;
                break;
        }
        applet.noStroke();
        applet.fill(backColor);
        applet.rect(1, 1, context.width - 2, context.height - 2);

        //render GT4 / Cup / Super trofeo corners.
        CarCategory cat = stats.get(CAR_MODEL).getCategory();
        if (cat != GT3) {
            applet.fill(COLOR_WHITE);
            applet.beginShape();
            applet.vertex(context.width - 1, context.height - 1);
            applet.vertex(context.width - 1, context.height - LINE_HEIGHT * 0.55f);
            applet.vertex(context.width - LINE_HEIGHT * 0.55f, context.height - 1);
            applet.endShape(CLOSE);
            applet.stroke(0, 0, 0, 50);
            applet.line(context.width - 1, context.height - LINE_HEIGHT * 0.55f,
                    context.width - LINE_HEIGHT * 0.55f, context.height);
            applet.noStroke();
            if (cat == ST) {
                applet.fill(COLOR_SUPER_TROFEO);
            } else if (cat == CUP) {
                applet.fill(LookAndFeel.COLOR_PORSCHE_CUP);
            } else {
                applet.fill(COLOR_GT4);
            }
            applet.beginShape();
            applet.vertex(context.width - 1, context.height - 1);
            applet.vertex(context.width - 1, context.height - LINE_HEIGHT * 0.4f);
            applet.vertex(context.width - LINE_HEIGHT * 0.4f, context.height - 1);
            applet.endShape(CLOSE);
        }

        applet.fill(frontColor);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontMedium());
        applet.text(String.valueOf(stats.get(CAR_NUMBER)),
                context.width / 2f, context.height / 2f);
    }

}