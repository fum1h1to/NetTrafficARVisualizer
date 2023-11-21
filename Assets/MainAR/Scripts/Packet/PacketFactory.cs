using UnityEngine;
using MainAR.Scripts;

namespace MainAR.Scripts.Packet
{
  public class PacketFactory 
  {
    private PacketPrefabData _packetPrefabData;
    private Camera _arCamera;

    public PacketFactory(
      PacketPrefabData packetPrefabData,
      Camera arCamera
    ) 
    {
      _packetPrefabData = packetPrefabData;
      _arCamera = arCamera;
    }
    
    public void CreateInboundPacket(PacketNativeInterfaceModel packetObj) 
    {
      InboundPacket inboutPacket = InboundPacket.Create(_packetPrefabData.InboundPacketObject, packetObj, _arCamera);
    }

    public void CreateOutboundPacket(PacketNativeInterfaceModel packetObj) 
    {
      OutboundPacket outboutPacket = OutboundPacket.Create(_packetPrefabData.OutboundPacketObject, packetObj, _arCamera);
    }
  }
}