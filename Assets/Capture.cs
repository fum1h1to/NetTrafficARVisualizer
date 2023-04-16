using System.Collections;
using System.Collections.Generic;
using System;
using System.Text;
using UnityEngine;
using TMPro;

public class Capture : MonoBehaviour {
  [SerializeField] TextMeshProUGUI selectText;
  private AndroidJavaObject mActivity;
  void Start() {
    AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");

    mActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
  }

  void CreateInboundPacketObject(string testText) {
    Debug.Log("called CreateInboundPacketObject: " + testText);
  }

  public void OnClick() {
    bool mCaptureRunning = mActivity.Call<bool>("getCaptureRunning");
    if (mCaptureRunning) {
      mActivity.Call("stopCapture");
      selectText.text = "Start";
    } else {
      mActivity.Call("startCapture");
      selectText.text = "Stop";
    }
  }
}