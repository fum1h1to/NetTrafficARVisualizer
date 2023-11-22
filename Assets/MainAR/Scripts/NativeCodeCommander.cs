using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Android;
using VContainer;
using MainAR.Scripts.Packet;
 
namespace MainAR.Scripts
{
    public class NativeCodeCommander
    {
        private AndroidJavaObject mActivity;

        public NativeCodeCommander() {
            AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");

            mActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
        }

        public void Test() {
            mActivity.Call("test");
        }

        public void StartCapture() {
            mActivity.Call("startCapture");
        }

        public void StopCapture() {
            mActivity.Call("stopCapture");
        }

        public bool GetCaptureRunning() {
            return mActivity.Call<bool>("getCaptureRunning");
        }
    }
}