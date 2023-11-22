using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.XR.ARFoundation;

namespace MainAR.Scripts.Packet
{
  public class OutboundPacket : MonoBehaviour
  {
      // private float packetAnimationTime = 8f;
      // private float packetAnimationAfterTime = 6f;
      // private Renderer _Renderer;
      // private Vector3 endPosition;
      // private Vector3 position;
      // private Vector3 diffScale;
      // private Vector3 velocity;
      // private Camera arCamera;
      // private bool isFlagSet = false;
      // private GameObject countryFlag;
      // private float arrowVisibleDelay;
      // private bool isArrowVisible = false;
      
      // public bool IsVisible =>  _Renderer.isVisible;

      // public static OutboundPacket Create(GameObject outboundPacketObject, PacketNativeInterfaceModel packetObj, Camera arCamera) {
      //   GameObject obj= Instantiate(outboundPacketObject, new Vector3(0, 0, 0), Quaternion.identity) as GameObject;
      //   obj.SetActive(false);
      //   OutboundPacket outboundPacket = obj.GetComponent<OutboundPacket>();
      //   outboundPacket.SetArCamera(arCamera);
      //   outboundPacket.SetCountryCode(packetObj.countryCode);

      //   obj.SetActive(true);
      //   return outboundPacket;
      // }
      
      // // Start is called before the first frame update
      // void Start()
      // {
      //     _Renderer = GetComponent<Renderer>();

      //     position = transform.position;
      //     diffScale = transform.localScale / packetAnimationAfterTime;

      //     arrowVisibleDelay = packetAnimationTime * 0.2f;
      // }

      // // Update is called once per frame
      // void Update()
      // {
      //     if (isFlagSet) {
      //         countryFlag.transform.LookAt(arCamera.transform.position);
      //     }
      //     if (packetAnimationTime >= 0f) {
      //         var acceleration = Vector3.zero;

      //         var diff = endPosition - position;
      //         acceleration += (diff - velocity * packetAnimationTime) * 2f / (packetAnimationTime * packetAnimationTime);

      //         velocity += acceleration * Time.deltaTime;
      //         position += velocity * Time.deltaTime;
      //         transform.position = position;

      //         packetAnimationTime -= Time.deltaTime;
      //     } else {
      //         if (packetAnimationAfterTime >= 0f) {
      //             transform.localScale -= diffScale * Time.deltaTime;
      //             packetAnimationAfterTime -= Time.deltaTime;
      //         } else {

      //             Destroy(this.gameObject);
      //         }
      //     }

      //     if (arrowVisibleDelay >= 0f) {
      //         arrowVisibleDelay -= Time.deltaTime;
      //     } else {
      //         if (!isArrowVisible) {
      //             var MainUI = FindObjectOfType<UIManager>();
      //             if(!this.IsVisible) {
      //                 MainUI.GetComponent<UIManager>().VisibleArrow(this.transform.position);
      //             }
      //             isArrowVisible = true;
      //         }
      //     }
      // }

      // public void SetEndPosition(float x, float y, float z) {
      //     endPosition = new Vector3(x, y, z);
      // }

      // private void SetArCamera(Camera arCamera) {
      //   this.arCamera = arCamera;
      // }

      // public void SetCountryCode(string countryCode) {
      //     countryFlag = new GameObject("CountryFlag");
      //     SpriteRenderer countryFlagSprite = countryFlag.AddComponent<SpriteRenderer>();
          
      //     Texture2D image_texture = Resources.Load<Texture2D>("Images/CountryCode/" + countryCode);
      //     Sprite image = Sprite.Create(image_texture, new Rect(0, 0, image_texture.width, image_texture.height), Vector2.zero);
      //     if (image != null) {
      //         isFlagSet = true;
      //         countryFlagSprite.sprite = image;
      //     } else {
      //         return;
      //     }

      //     countryFlag.transform.parent = transform;
      //     float width = countryFlagSprite.bounds.size.x;
      //     countryFlag.transform.localPosition = new Vector3(- width / 2 * 1.5f, .5f, 0);
      //     countryFlag.transform.localScale = new Vector3(1.5f, 1.5f, 1.5f);

      // }
  }
}
