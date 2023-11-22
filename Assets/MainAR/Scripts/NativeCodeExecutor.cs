using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Android;
using VContainer;
using MainAR.Scripts.Packet;
 
namespace MainAR.Scripts
{
    public class NativeCodeExecutor : MonoBehaviour
    {
        private PacketFactory packetFactory;

        void Start() {
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
          Debug.Log("NativeCodeExecutor Construct");  
            this.packetFactory = packetFactory;
        }
        
    }
}