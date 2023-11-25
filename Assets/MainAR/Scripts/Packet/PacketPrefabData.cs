using UnityEngine;

namespace MainAR.Scripts.Packet
{
  [CreateAssetMenu(fileName = "PacketPrefabData", menuName = "ScriptableObject/PacketPrefabData", order = 0)]
  public class PacketPrefabData : ScriptableObject
  {
    [SerializeField] private GameObject _inboundPacketBaseObject;
    [SerializeField] private GameObject _outboundPacketBaseObject;
    [SerializeField] private GameObject _nomarlPacketObject;
    [SerializeField] private Color _normalPacketColor;
    [SerializeField] private GameObject _spamhausPacketObject;
    [SerializeField] private Color _spamhausPacketColor;

    
    public GameObject InboundPacketBaseObject => _inboundPacketBaseObject;
    public GameObject OutboundPacketBaseObject => _outboundPacketBaseObject;
    public GameObject NormalPacketObject => _nomarlPacketObject;
    public Color NormalPacketColor => _normalPacketColor;
    public GameObject SpamhausPacketObject => _spamhausPacketObject;
    public Color SpamhausPacketColor => _spamhausPacketColor;
  }
}