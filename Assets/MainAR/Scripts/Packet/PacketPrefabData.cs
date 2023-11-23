using UnityEngine;

namespace MainAR.Scripts.Packet
{
  [CreateAssetMenu(fileName = "PacketPrefabData", menuName = "ScriptableObject/PacketPrefabData", order = 0)]
  public class PacketPrefabData : ScriptableObject
  {
    [SerializeField] private GameObject _inboundPacketObject;
    [SerializeField] private GameObject _outboundPacketObject;
    
    public GameObject InboundPacketObject => _inboundPacketObject;
    public GameObject OutboundPacketObject => _outboundPacketObject;
  }
}