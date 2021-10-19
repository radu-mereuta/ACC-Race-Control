/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.gui.app.livetiming.timing.tablemodels;

import static java.util.stream.Collectors.toList;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import racecontrol.client.data.DriverInfo;
import static racecontrol.client.extension.statistics.CarProperties.CAR_MODEL;
import static racecontrol.client.extension.statistics.CarProperties.DRIVER_INDEX;
import static racecontrol.client.extension.statistics.CarProperties.DRIVER_LIST;
import static racecontrol.client.extension.statistics.CarProperties.DRIVER_STINT_TIME;
import static racecontrol.client.extension.statistics.CarProperties.DRIVER_STINT_TIME_ACCURATE;
import static racecontrol.client.extension.statistics.CarProperties.REALTIME_POSITION;
import racecontrol.client.extension.statistics.CarStatistics;
import racecontrol.gui.LookAndFeel;
import static racecontrol.gui.LookAndFeel.COLOR_WHITE;
import static racecontrol.gui.LookAndFeel.LINE_HEIGHT;
import racecontrol.gui.lpui.table.LPTable;
import racecontrol.gui.lpui.table.LPTableColumn;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class DriversTableModel
        extends LiveTimingTableModel {

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            positionColumn,
            nameColumn
            .setMaxWidth(LINE_HEIGHT * 7f)
            .setMinWidth(LINE_HEIGHT * 7f)
            .setPriority(1000),
            new LPTableColumn("Category")
            .setMaxWidth(100)
            .setMinWidth(100)
            .setTextAlign(LEFT)
            .setCellRenderer((applet, context) -> driverCategoryRenderer(applet, context)),
            pitColumn,
            carNumberColumn,
            new LPTableColumn("Class")
            .setMaxWidth(100)
            .setMinWidth(100)
            .setTextAlign(LEFT)
            .setCellRenderer((applet, context) -> carClassRenderer(applet, context)),
            new LPTableColumn("Car")
            .setMinWidth(100)
            .setTextAlign(LEFT)
            .setGrowthRate(2)
            .setCellRenderer((applet, context) -> carRenderer(applet, context)),
            new LPTableColumn("Stint")
            .setMinWidth(100)
            .setTextAlign(LEFT)
            .setGrowthRate(1)
            .setCellRenderer((applet, context) -> stintTimeRenderer(applet, context))
        };
    }

    @Override
    public Object getValueAt(int column, int row) {
        return getEntry(row);
    }

    @Override
    public void sort() {
        entries = entries.stream()
                .sorted((c1, c2)
                        -> c1.get(REALTIME_POSITION).compareTo(c2.get(REALTIME_POSITION))
                )
                .collect(toList());
    }

    @Override
    public String getName() {
        return "Drivers";
    }

    @Override
    public float getRowHeight(int rowIndex) {
        CarStatistics car = getEntry(rowIndex);
        if (car == null) {
            return LINE_HEIGHT;
        }
        return LINE_HEIGHT * (car.get(DRIVER_LIST).getDrivers().size() - 1) * 0.6f
                + LINE_HEIGHT;
    }

    protected void pitRenderer(PApplet applet, LPTable.RenderContext context) {
        LPTable.RenderContext newContext = new LPTable.RenderContext(
                context.object,
                context.rowIndex,
                context.columnIndex,
                context.isSelected,
                false,
                context.isMouseOverColumn,
                context.isOdd,
                context.width,
                context.height,
                context.tableWidth,
                context.tableHeight,
                context.tablePosX,
                context.tablePosY,
                context.mouseX,
                context.mouseY);
        super.pitRenderer(applet, newContext);
    }

    @Override
    protected void nameRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        int i = 0;
        for (DriverInfo info : stats.get(DRIVER_LIST).getDrivers()) {
            String name = info.getFirstName() + " " + info.getLastName();
            applet.fill(COLOR_WHITE);
            applet.textAlign(LEFT, CENTER);
            if (i == stats.get(DRIVER_INDEX)) {
                applet.textFont(LookAndFeel.fontMedium());
                if (stats.get(DRIVER_LIST).getDrivers().size() > 1) {
                    name = "<" + name + ">";
                }
            } else {
                applet.textFont(LookAndFeel.fontRegular());
            }
            applet.text(name, 10f, LINE_HEIGHT / 2f + (LINE_HEIGHT * 0.6f) * i);
            i++;
        }
    }

    private void driverCategoryRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;

        int i = 0;
        for (DriverInfo info : stats.get(DRIVER_LIST).getDrivers()) {
            String name = info.getCategory().getText();
            applet.fill(COLOR_WHITE);
            applet.textAlign(LEFT, CENTER);
            applet.textFont(LookAndFeel.fontRegular());
            applet.text(name, 10f, LINE_HEIGHT / 2f + (LINE_HEIGHT * 0.6f) * i);
            i++;
        }
    }

    private void carClassRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;

        String name = stats.get(CAR_MODEL).getCategory().getText();
        applet.fill(COLOR_WHITE);
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(name, 10f, LINE_HEIGHT / 2f);
    }

    private void carRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;

        String name = stats.get(CAR_MODEL).getName();
        applet.fill(COLOR_WHITE);
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(name, 10f, LINE_HEIGHT / 2f);
    }

    private void stintTimeRenderer(PApplet applet, LPTable.RenderContext context) {
        CarStatistics stats = (CarStatistics) context.object;
        String text = TimeUtils.asDurationShort(stats.get(DRIVER_STINT_TIME));
        text = text + (stats.get(DRIVER_STINT_TIME_ACCURATE) ? "" : "*");
        applet.fill(COLOR_WHITE);
        applet.textAlign(LEFT, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, 10f, context.height / 2f);
    }
}
