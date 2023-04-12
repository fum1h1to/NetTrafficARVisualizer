#include <stdio.h>
#include <pcap.h>
#include <arpa/inet.h>
#include <string.h>

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

    char str[100];
    sprintf(str, "Device: %s", device);
    return str;
}

extern "C" char* GetWlan0DeviceInformation()
{
    char *device;
    char ip[13];
    char subnet_mask[13];
    bpf_u_int32 ip_raw; /* IP address as integer */
    bpf_u_int32 subnet_mask_raw; /* Subnet mask as integer */
    int lookup_return_code;
    char error_buffer[PCAP_ERRBUF_SIZE]; /* Size defined in pcap.h */
    struct in_addr address; /* Used for both ip & subnet */

    /* Find a device */
    device = "wlan0";

    /* Get device info */
    lookup_return_code = pcap_lookupnet(
            device,
            &ip_raw,
            &subnet_mask_raw,
            error_buffer
    );
    if (lookup_return_code == -1) {
        printf("%s\n", error_buffer);
        return error_buffer;
    }

    /* Get ip in human readable form */
    address.s_addr = ip_raw;
    strcpy(ip, inet_ntoa(address));
    if (ip == NULL) {
        return "inet_ntoa";
    }

    char str[100];
    sprintf(str, "IP address: %s", ip);

    return str;
}