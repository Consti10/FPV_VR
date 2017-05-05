//
// Created by Constantin on 29.04.2017.
//

#ifndef FPV_VR_NANOSECONDS_H
#define FPV_VR_NANOSECONDS_H

#include <stdint.h>
#include <linux/time.h>
#include <time.h>

typedef uint64_t ksNanoseconds;

static ksNanoseconds getTimeNS()
{

    static ksNanoseconds timeBase = 0;

    struct timespec ts;
    clock_gettime( CLOCK_MONOTONIC, &ts );

    if ( timeBase == 0 )
    {
        timeBase = (ksNanoseconds) ts.tv_sec * 1000ULL * 1000ULL * 1000ULL + ts.tv_nsec;
    }

    return (ksNanoseconds) ts.tv_sec * 1000ULL * 1000ULL * 1000ULL + ts.tv_nsec - timeBase;
}

static double getTimeMS(){
    return getTimeNS()*0.000001; //10^-6
}









#endif //FPV_VR_NANOSECONDS_H
