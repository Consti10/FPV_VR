//
// Created by Constantin on 01.11.2016.
//

#ifndef FPV_VR_TELEMETRY_H
#define FPV_VR_TELEMETRY_H
#endif //FPV_VR_TELEMETRY_H

#pragma once

#include <stdint.h>
#include <time.h>

typedef struct {
    float voltage;
    float ampere;
    float baro_altitude;
    float altitude;
    double longitude;
    double latitude;
    float speed;
    int16_t ew, ns;  //?maybe day,time
    int16_t roll, pitch,yaw;
	uint8_t rssi;
	float WBrssi;
	uint8_t airspeed;
	uint8_t sats;
	uint8_t fix;
	//mavlink
	float heading;
} telemetry_data_t;

void telemetry_init(telemetry_data_t *td);


