using System.Collections;
using System.Collections.Generic;
using System;
using System.Text;
using UnityEngine;
using System.Runtime.InteropServices;

public class Capture : MonoBehaviour {
  void Start() {
    AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");

    AndroidJavaObject active = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");

    active.Call("startCapture");
  }

  void CreateInboundPacketObject(string testText) {
    Debug.Log("called CreateInboundPacketObject: " + testText);
  }
}