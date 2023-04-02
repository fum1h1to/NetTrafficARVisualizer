using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using SharpPcap;

public class network : MonoBehaviour
{
    // Start is called before the first frame update
    void Start()
    {
        Debug.Log("Hello World");
        var devices = CaptureDeviceList.Instance;

        /*If no device exists, print error */
        if (devices.Count < 1)
        {
            Debug.Log("No device found on this machine");
            return;
        }

        int i = 0;

        /* Scan the list printing every entry */
        foreach (var dev in devices)
        {
            /* Description */
            Debug.Log(i + ")" + dev.Name + " " + dev.Description);
            i++;
        }
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}
