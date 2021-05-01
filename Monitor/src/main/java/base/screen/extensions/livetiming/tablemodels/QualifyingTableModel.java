/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base.screen.extensions.livetiming.tablemodels;

import base.screen.extensions.livetiming.LiveTimingEntry;
import base.screen.networking.data.CarInfo;
import base.screen.networking.data.LapInfo;
import base.screen.networking.enums.CarLocation;
import base.screen.networking.enums.LapType;
import base.screen.utility.TimeUtils;
import base.screen.visualisation.LookAndFeel;
import static base.screen.visualisation.LookAndFeel.COLOR_PURPLE;
import static base.screen.visualisation.LookAndFeel.COLOR_WHITE;
import base.screen.visualisation.gui.LPTableColumn;
import base.screen.visualisation.gui.LPTable;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;

/**
 *
 * @author Leonard
 */
public class QualifyingTableModel
        extends LiveTimingTableModel {

    /**
     * This class's logger.
     */
    private static final Logger LOG = Logger.getLogger(QualifyingTableModel.class.getName());

    @Override
    public LPTableColumn[] getColumns() {
        return new LPTableColumn[]{
            positionColumn,
            nameColumn,
            pitColumn,
            carNumberColumn,
            new LPTableColumn("Lap")
            .setCellRenderer(lapTimeRenderer),
            new LPTableColumn("Delta")
            .setCellRenderer(deltaRenderer),
            new LPTableColumn("Best")
            .setCellRenderer(bestLapRenderer),
            new LPTableColumn("Gap")
            .setCellRenderer(gapRenderer),
            new LPTableColumn("S1")
            .setCellRenderer(sectorRenderer),
            new LPTableColumn("S2")
            .setCellRenderer(sectorRenderer),
            new LPTableColumn("S3")
            .setCellRenderer(sectorRenderer),
            new LPTableColumn("Laps")
            .setCellRenderer(lapsRenderer)
        };
    }

    @Override
    public Object getValueAt(int column, int row) {
        CarInfo car = getEntry(row).getCarInfo();

        switch (column) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 11:
                return getEntry(row);
            case 8:
                return new Tuple(
                        car.getRealtime().getBestSessionLap(),
                        0
                );
            case 9:
                return new Tuple(
                        car.getRealtime().getBestSessionLap(),
                        1
                );
            case 10:
                return new Tuple(
                        car.getRealtime().getBestSessionLap(),
                        2
                );
        }
        return "-";
    }

    private class Tuple {

        public Object left;
        public Object right;

        public Tuple(Object left, Object right) {
            this.left = left;
            this.right = right;
        }
    }

    private final LPTable.CellRenderer bestLapRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        CarInfo car = ((LiveTimingEntry) object).getCarInfo();
        int bestLapTime = car.getRealtime().getBestSessionLap().getLapTimeMS();
        String text = "--";
        if (bestLapTime != Integer.MAX_VALUE) {
            text = TimeUtils.asLapTime(bestLapTime);
        }
        applet.noStroke();
        if (bestLapTime == sessionBestLap.getLapTimeMS()) {
            applet.fill(COLOR_PURPLE);
        } else {
            applet.fill(COLOR_WHITE);
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, width / 2, height / 2);
    };

    private final LPTable.CellRenderer gapRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        CarInfo car = ((LiveTimingEntry) object).getCarInfo();
        int bestLapTime = car.getRealtime().getBestSessionLap().getLapTimeMS();
        String text = "--";
        if (bestLapTime != Integer.MAX_VALUE) {
            int sessionBestLapTime = sessionBestLap.getLapTimeMS();
            int diff = bestLapTime - sessionBestLapTime;
            if (diff != 0) {
                text = TimeUtils.asDelta(diff);
            }

        }
        applet.noStroke();
        applet.fill(COLOR_WHITE);
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, width / 2, height / 2);
    };

    private final LPTable.CellRenderer lapTimeRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        CarInfo car = ((LiveTimingEntry) object).getCarInfo();
        LapInfo currentLap = car.getRealtime().getCurrentLap();
        String text = "--";
        applet.fill(COLOR_WHITE);
        if (car.getRealtime().getLocation() == CarLocation.TRACK
                && currentLap.getType() == LapType.REGULAR) {
            applet.fill(LookAndFeel.COLOR_WHITE);
            if (currentLap.isInvalid()) {
                applet.fill(LookAndFeel.COLOR_RED);
            }
            text = TimeUtils.asLapTime(currentLap.getLapTimeMS());
        } else if (car.getRealtime().getLocation() == CarLocation.TRACK) {
            text = currentLap.getType().name();
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, width / 2, height / 2);
    };

    private final LPTable.CellRenderer deltaRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        CarInfo car = ((LiveTimingEntry) object).getCarInfo();
        LapInfo currentLap = car.getRealtime().getCurrentLap();

        String text = "--";
        if (car.getRealtime().getLocation() == CarLocation.TRACK
                && currentLap.getType() == LapType.REGULAR) {

            applet.fill(LookAndFeel.COLOR_RACE);
            if (car.getRealtime().getDelta() > 0) {
                applet.fill(LookAndFeel.COLOR_RED);
            }
            text = TimeUtils.asDelta(car.getRealtime().getDelta());
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, width / 2, height / 2);
    };

    private final LPTable.CellRenderer sectorRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        Tuple input = (Tuple) object;
        int sectorIndex = (int) input.right;
        List<Integer> splits = ((LapInfo) input.left).getSplits();

        String text = "--";
        applet.fill(COLOR_WHITE);
        if (sectorIndex < splits.size()) {
            if (splits.get(sectorIndex) != Integer.MAX_VALUE) {
                text = TimeUtils.asLapTime(splits.get(sectorIndex));
                if (Objects.equals(splits.get(sectorIndex), sessionBestLap.getSplits().get(sectorIndex))) {
                    applet.fill(COLOR_PURPLE);
                }
            }
        }
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.text(text, width / 2, height / 2);
    };

    private final LPTable.CellRenderer lapsRenderer = (
            PApplet applet,
            Object object,
            boolean isSelected,
            boolean isMouseOverRow,
            boolean isMouseOverColumn,
            float width,
            float height) -> {
        CarInfo car = ((LiveTimingEntry) object).getCarInfo();
        applet.textAlign(CENTER, CENTER);
        applet.textFont(LookAndFeel.fontRegular());
        applet.fill(COLOR_WHITE);
        applet.text(String.valueOf(car.getRealtime().getLaps()), width / 2, height / 2);
    };

}