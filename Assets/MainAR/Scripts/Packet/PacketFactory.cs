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
      InboundPacket inboutPacket = InboundPacket.Create(
        _packetPrefabData.InboundPacketBaseObject,
        getDecoratePacketObject(packetObj),
        getPacketColor(packetObj),
        packetObj,
        _uiController,
        _arCamera);
    }

    public void CreateOutboundPacket(PacketNativeInterfaceModel packetObj) 
    {
      OutboundPacket outboutPacket = OutboundPacket.Create(_packetPrefabData.OutboundPacketBaseObject, packetObj, _uiController, _arCamera);
    }

    private GameObject getDecoratePacketObject(PacketNativeInterfaceModel packetObj) 
    {
      if (packetObj.isSpamhaus) {
        return _packetPrefabData.SpamhausPacketObject;
      } else {
        return _packetPrefabData.NormalPacketObject;
      }
    }

    private Color getPacketColor(PacketNativeInterfaceModel packetObj) 
    {
      if (packetObj.isSpamhaus) {
        return _packetPrefabData.SpamhausPacketColor;
      } else {
        return _packetPrefabData.NormalPacketColor;
      }
    }
  }
}