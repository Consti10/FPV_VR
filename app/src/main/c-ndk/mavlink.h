//
// Created by Constantin on 12.12.2016.
//

#ifndef FPV_VR_MAVLINK_H
#define FPV_VR_MAVLINK_H

#endif //FPV_VR_MAVLINK_H

#include "telemetry.h"
#include "mavlink/mavlink.h"

int mavlink_read(telemetry_data_t *td, uint8_t *buf, int buflen);

