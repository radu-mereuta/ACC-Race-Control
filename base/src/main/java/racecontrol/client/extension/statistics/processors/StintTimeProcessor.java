/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.extension.statistics.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import racecontrol.client.data.BroadcastingEvent;
import racecontrol.client.data.RealtimeInfo;
import static racecontrol.client.data.enums.BroadcastingEventType.PENALTYCOMMMSG;
import racecontrol.client.data.enums.CarLocation;
import static racecontrol.client.data.enums.CarLocation.PITLANE;
import static racecontrol.client.data.enums.SessionPhase.SESSION;
import racecontrol.client.events.BroadcastingEventEvent;
import racecontrol.client.events.RealtimeCarUpdateEvent;
import racecontrol.client.events.SessionPhaseChangedEvent;
import static racecontrol.client.extension.statistics.CarProperties.CAR_ID;
import static racecontrol.client.extension.statistics.CarProperties.CAR_LOCATION;
import static racecontrol.client.extension.statistics.CarProperties.CAR_NUMBER;
import static racecontrol.client.extension.statistics.CarProperties.DRIVER_STINT_TIME;
import static racecontrol.client.extension.statistics.CarProperties.DRIVER_STINT_TIME_ACCURATE;
import racecontrol.client.extension.statistics.StatisticsProcessor;
import racecontrol.client.extension.statistics.WritableCarStatistics;
import racecontrol.eventbus.Event;
import racecontrol.utility.TimeUtils;

/**
 *
 * @author Leonard
 */
public class StintTimeProcessor
        extends StatisticsProcessor {

    private static final Logger LOG = Logger.getLogger(StintTimeProcessor.class.getName());
    /**
     * Maps carId's to the timestamp for when their stint timer starts.
     */
    private final Map<Integer, Long> stintStartTimestamp = new HashMap<>();
    /**
     * Maps carId's to their location of the previous update.
     */
    private final Map<Integer, CarLocation> prevCarLocation = new HashMap<>();
    /**
     * Holds carId's that served a penalty in the pits.
     */
    private final List<Integer> servedPenalty = new ArrayList<>();

    public StintTimeProcessor(Map<Integer, WritableCarStatistics> cars) {
        super(cars);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof SessionPhaseChangedEvent) {
            sessionPhaseChanged((SessionPhaseChangedEvent) e);
        } else if (e instanceof RealtimeCarUpdateEvent) {
            realtimeCarUpdate(((RealtimeCarUpdateEvent) e).getInfo());
        } else if (e instanceof BroadcastingEventEvent) {
            broadcastingEvent(((BroadcastingEventEvent) e).getEvent());
        }
    }

    private void sessionPhaseChanged(SessionPhaseChangedEvent event) {
        // reset all stint timers, set timestamps and set accurate flag.
        if (event.getSessionInfo().getPhase() == SESSION) {
            long now = System.currentTimeMillis();
            getCars().values().forEach(carStat -> {
                stintStartTimestamp.put(carStat.get(CAR_ID), now);
                carStat.put(DRIVER_STINT_TIME, 0);
                carStat.put(DRIVER_STINT_TIME_ACCURATE, !event.isInitialisation());
            });
        }
    }

    private void realtimeCarUpdate(RealtimeInfo info) {
        WritableCarStatistics carStats = getCars().get(info.getCarId());
        long now = System.currentTimeMillis();
        if (!stintStartTimestamp.containsKey(info.getCarId())) {
            stintStartTimestamp.put(info.getCarId(), now);
            carStats.put(DRIVER_STINT_TIME_ACCURATE, false);
        }
        if (!prevCarLocation.containsKey(info.getCarId())) {
            prevCarLocation.put(info.getCarId(), info.getLocation());
        }

        if (info.getLocation() != PITLANE) {

            // reset stint time when exiting pit lane
            if (prevCarLocation.get(info.getCarId()) == PITLANE) {
                // only reset if we didnt serve a penalty.
                if (!servedPenalty.contains(info.getCarId())) {
                    stintStartTimestamp.put(info.getCarId(), now);
                    carStats.put(DRIVER_STINT_TIME_ACCURATE, true);
                } else {
                    servedPenalty.remove(servedPenalty.indexOf(info.getCarId()));
                }
            }
            // set stint time
            carStats.put(DRIVER_STINT_TIME,
                    (int) (now - stintStartTimestamp.get(info.getCarId())));
        }

        // save car location.
        prevCarLocation.put(info.getCarId(), info.getLocation());
    }

    private void broadcastingEvent(BroadcastingEvent event) {
        if (event.getType() == PENALTYCOMMMSG) {
            WritableCarStatistics stats = getCars().get(event.getCarId());
            LOG.info(event.getMessage()
                    + "\t" + stats.get(CAR_NUMBER)
                    + "\t" + TimeUtils.asDurationShort(stats.get(DRIVER_STINT_TIME))
                    + "\t" + stats.get(CAR_LOCATION)
            );

            if (!servedPenalty.contains(event.getCarId())) {
                servedPenalty.add(event.getCarId());
            }
        }
    }
}
