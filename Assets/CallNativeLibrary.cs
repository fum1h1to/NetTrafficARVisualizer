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

    void Start()
    {
        int x = 3;
        int y = 10;
        Debug.Log("x + y = " + AddTest(x, y));

        var devicePtr = IntPtr.Zero;
        var errorBuffer = new StringBuilder(256);
        Debug.Log(Marshal.PtrToStringAnsi(FindDevice()));
    }
}