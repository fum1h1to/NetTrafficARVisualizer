using System.Collections;
using System.Collections.Generic;
using System;
using System.Text;
using UnityEngine;
using System.Runtime.InteropServices;

public class CallNativeLibrary : MonoBehaviour {

    [DllImport("libnativelib")]
    private static extern int AddTest(int x, int y);
    
    [DllImport("libnativelib", CharSet = CharSet.Ansi)]
    private static extern IntPtr FindDevice();

    [DllImport("libnativelib", CharSet = CharSet.Ansi)]
    private static extern IntPtr GetWlan0DeviceInformation();

    void Start()
    {
        int x = 3;
        int y = 10;
        Debug.Log("x + y = " + AddTest(x, y));

        Debug.Log(Marshal.PtrToStringAnsi(FindDevice()));
        Debug.Log(Marshal.PtrToStringAnsi(GetWlan0DeviceInformation()));
    }
}