using System.Collections;
using System.Collections.Generic;
using System;
using System.Text;
using UnityEngine;
using System.Runtime.InteropServices;

public class CallNativeLibrary : MonoBehaviour {

    [DllImport("__Internal")]
    private static extern int AddTest(int x, int y);
    
    [DllImport("libpcap", EntryPoint = "pcap_findalldevs")]
    private static extern int pcap_findalldevs(
        ref IntPtr /* pcap_if_t** */ alldevs,
        StringBuilder /* char* */ errbuf
    );
    void Start()
    {
        int x = 3;
        int y = 10;
        Debug.Log("x + y = " + AddTest(x, y));
    }
}