/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.client.events;

import racecontrol.eventbus.Event;
import racecontrol.client.data.TrackInfo;

/**
 *
 * @author Leonard
 */
public class TrackInfoEvent extends Event {

    private TrackInfo info;

    public TrackInfoEvent(TrackInfo info) {
        this.info = info;
    }

    public TrackInfo getInfo() {
        return info;
    }

}
