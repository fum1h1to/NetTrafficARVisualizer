using UnityEngine;
using MainAR.Scripts;
using MainAR.Scripts.Compass;
using MainAR.Scripts.View;

namespace MainAR.Scripts.Packet
{
  public class PacketFactory 
  {
    private PacketPrefabData _packetPrefabData;
    private Camera _arCamera;
    private UIController _uiController;

    public PacketFactory(
      PacketPrefabData packetPrefabData,
      Camera arCamera,
      UIController uiController
    ) 
    {
      _packetPrefabData = packetPrefabData;
      _arCamera = arCamera;
      _uiController = uiController;
    }
    
    public void CreateInboundPacket(PacketNativeInterfaceModel packetObj)
    {
      InboundPacket inboutPacket = InboundPacket.Create(_packetPrefabData.InboundPacketObject, packetObj, _uiController, _arCamera);
    }

    public void CreateOutboundPacket(PacketNativeInterfaceModel packetObj) 
    {
      OutboundPacket outboutPacket = OutboundPacket.Create(_packetPrefabData.OutboundPacketObject, packetObj, _uiController, _arCamera);
    }
  }
}