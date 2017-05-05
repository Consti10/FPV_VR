//
// Created by Constantin on 01.11.2016.
//

#include "telemetry.h"


#include <unistd.h>
#include <sys/mman.h>
#include <sys/stat.h>        /* For mode constants */
#include <fcntl.h>           /* For O_* constants */
#include <stdio.h>
#include <stdlib.h>
#include "telemetry.h"


void telemetry_init(telemetry_data_t *td) {
    td->voltage = 0;
    td->ampere = 0;
    td->altitude = 0;
    td->baro_altitude=0;
    td->longitude =0;
    td->latitude =0;
    td->speed = 0;
    //only for frsky
    td->ew = 0;
    td->ns = 0;
    //end
    td->roll = 10;
	td->pitch = 10;
    td->yaw = 10;
    //only for ltm
	td->airspeed = 0;
	td->sats = 0;
	td->fix = 0;
    //end
    td->rssi = 0; //only from rc copter receiver
    td->WBrssi=0;  //for wifibraodcast video stream receiver

    //mavlink
    td->heading=0;
}


