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
    private CompassState _compassState;
    private UIController _uiController;

    public PacketFactory(
      PacketPrefabData packetPrefabData,
      Camera arCamera,
      CompassState compassState,
      UIController uiController
    ) 
    {
      _packetPrefabData = packetPrefabData;
      _arCamera = arCamera;
      _compassState = compassState;
      _uiController = uiController;
    }
    
    public void CreateInboundPacket(PacketNativeInterfaceModel packetObj)
    {
      InboundPacket inboutPacket = InboundPacket.Create(_packetPrefabData.InboundPacketObject, packetObj, _uiController, _arCamera, _compassState);
    }

    public void CreateOutboundPacket(PacketNativeInterfaceModel packetObj) 
    {
      // OutboundPacket outboutPacket = OutboundPacket.Create(_packetPrefabData.OutboundPacketObject, packetObj, _arCamera);
    }
  }
}