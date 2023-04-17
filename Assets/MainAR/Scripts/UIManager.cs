using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

public class UIManager : MonoBehaviour
{
  [SerializeField] TextMeshProUGUI selectText;
  private AndroidJavaObject mActivity;
  void Start() {
    AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");

    mActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
  }

  public void ToggleCapture() {
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
