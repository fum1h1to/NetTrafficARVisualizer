#include <stdio.h>
#include <pcap.h>

extern "C" int AddTest(int x, int  y)
{
    return x + y;
}

extern "C" char* FindDevice()
{
    char *device; /* Name of device (e.g. eth0, wlan0) */
    char error_buffer[PCAP_ERRBUF_SIZE]; /* Size defined in pcap.h */

    /* Find a device */
    device = pcap_lookupdev(error_buffer);
    if (device == NULL) {
        printf("Error finding device: %s\n", error_buffer);
        return error_buffer;
    }

    printf("Network device found: %s\n", device);
    return device;
}