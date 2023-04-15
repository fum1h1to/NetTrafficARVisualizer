using System.Collections;
using System.Collections.Generic;
using System;
using System.Text;
using UnityEngine;
using System.Runtime.InteropServices;

public class CallNativeLibrary : MonoBehaviour {
  void Start() {
    AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");

    AndroidJavaObject activ = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");

    activ.Call("startCapture");
  }
}