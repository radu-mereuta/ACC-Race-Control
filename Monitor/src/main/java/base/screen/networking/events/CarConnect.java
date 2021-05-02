/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package base.screen.networking.events;

import base.screen.eventbus.Event;
import base.screen.networking.data.CarInfo;

/**
 *
 * @author Leonard
 */
public class CarConnect extends Event {

    private CarInfo car;

    public CarConnect(CarInfo car) {
        this.car = car;
    }

    public CarInfo getCar() {
        return car;
    }

}
