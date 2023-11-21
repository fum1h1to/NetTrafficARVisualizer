using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Android;
using VContainer;
using MainAR.Scripts.Packet;

namespace MainAR.Scripts
{
    public class NativeCodeInterface : MonoBehaviour
    {
        private AndroidJavaObject mActivity;
        private PacketFactory packetFactory;

        void Start() {
            AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");

            mActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
        }

        public void OnClick() {
            mActivity.Call("test");
        }

        public void CreateInboundPacket(string message) {
            PacketNativeInterfaceModel json = JsonUtility.FromJson<PacketNativeInterfaceModel>(message);
            if (packetFactory != null) {
                packetFactory.CreateInboundPacket(json);
            }
        }

        public void CreateOutboundPacket(string message) {
            PacketNativeInterfaceModel json = JsonUtility.FromJson<PacketNativeInterfaceModel>(message);
            if (packetFactory != null) {
                packetFactory.CreateOutboundPacket(json);
            }
        }

        [Inject]
        public void Construct(PacketFactory packetFactory) {
            this.packetFactory = packetFactory;
        }
        
    }
}