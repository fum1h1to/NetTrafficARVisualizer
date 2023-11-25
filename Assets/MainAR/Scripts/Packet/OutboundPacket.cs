using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.XR.ARFoundation;
using System.Device.Location;
using MainAR.Scripts;
using MainAR.Scripts.Geo;
using MainAR.Scripts.View;

namespace MainAR.Scripts.Packet
{
  public class OutboundPacket : MonoBehaviour
  {
      private float packetAnimationTime = 8f;
      private float packetAnimationAfterTime = 6f;
      private Renderer _Renderer;
      private Camera arCamera;
		  private UIController uiController;
      private Vector3 endPosition;
      private Vector3 position;
      private Vector3 diffScale;
      private Vector3 velocity;
      private bool isFlagSet = false;
      private GameObject countryFlag;
      private float arrowVisibleDelay;
      private bool isArrowVisible = false;
      
      public bool IsVisible =>  _Renderer.isVisible;

      public static OutboundPacket Create(GameObject outboundPacketObject, PacketNativeInterfaceModel packetObj, UIController uiController, Camera arCamera) {
        GameObject obj= Instantiate(outboundPacketObject, new Vector3(0, 0, 0), Quaternion.identity) as GameObject;
        obj.SetActive(false);
        OutboundPacket outboundPacket = obj.GetComponent<OutboundPacket>();
			  outboundPacket.SetPacketScale(packetObj.count);
        outboundPacket.SetArCamera(arCamera);
        outboundPacket.SetUIController(uiController);
        outboundPacket.SetStartPosition();
        outboundPacket.SetEndPosition(packetObj.lat, packetObj.lng);
        outboundPacket.SetCountryCode(packetObj.countryCode);
			  outboundPacket.setObjectColor(packetObj);

        obj.SetActive(true);
        return outboundPacket;
      }

      private void SetStartPosition() {
        transform.position = arCamera.transform.position - new Vector3(0, 0.1f, 0);
      }

      private void SetPacketScale(int packetSize) {
        transform.localScale = transform.localScale * (packetSize / 10f);
      }
 
      public void SetEndPosition(float targetLatitude, float targetLongitude) {
        if (Input.location.isEnabledByUser)
        {
          if (Input.location.status == LocationServiceStatus.Running)
          {
            LocationInfo lastData = Input.location.lastData;
            GeoCoordinate currentCoordinate = new GeoCoordinate(lastData.latitude, lastData.longitude);

            GeoCoordinate targetCoordinate = new GeoCoordinate(targetLatitude, targetLongitude);

            float heading = 0;

            endPosition = GeoUtil.ConvertCoordinate(currentCoordinate, targetCoordinate, heading, 5);
          }
        }
      }

      private void setObjectColor(PacketNativeInterfaceModel packetObj) {
        if(packetObj.isSpamhaus) {
          GetComponent<Renderer>().material.color = Color.red;
        } else {
          GetComponent<Renderer>().material.color = Color.green;
        }
      }
      
      // Start is called before the first frame update
      void Start()
      {
          _Renderer = GetComponent<Renderer>();

          position = transform.position;
          diffScale = transform.localScale / packetAnimationAfterTime;

          arrowVisibleDelay = packetAnimationTime * 0.2f;
      }

      // Update is called once per frame
      void Update()
      {
          if (isFlagSet) {
              countryFlag.transform.LookAt(arCamera.transform.position);
          }
          if (packetAnimationTime >= 0f) {
              var acceleration = Vector3.zero;

              var diff = endPosition - position;
              acceleration += (diff - velocity * packetAnimationTime) * 2f / (packetAnimationTime * packetAnimationTime);

              velocity += acceleration * Time.deltaTime;
              position += velocity * Time.deltaTime;
              transform.position = position;

              packetAnimationTime -= Time.deltaTime;
          } else {
              if (packetAnimationAfterTime >= 0f) {
                  transform.localScale -= diffScale * Time.deltaTime;
                  packetAnimationAfterTime -= Time.deltaTime;
              } else {

                  Destroy(this.gameObject);
              }
          }

          if (arrowVisibleDelay >= 0f) {
              arrowVisibleDelay -= Time.deltaTime;
          } else {
              if (!isArrowVisible) {
                  if(!this.IsVisible) {
                      uiController.VisibleArrow(this.transform.position);
                  }
                  isArrowVisible = true;
              }
          }
      }

      private void SetArCamera(Camera arCamera) {
        this.arCamera = arCamera;
      }

      private void SetUIController(UIController uiController) {
        this.uiController = uiController;
      }

      public void SetCountryCode(string countryCode) {
          countryFlag = new GameObject("CountryFlag");
          SpriteRenderer countryFlagSprite = countryFlag.AddComponent<SpriteRenderer>();
          
          Texture2D image_texture = Resources.Load<Texture2D>("Images/CountryCode/" + countryCode);
          Sprite image = Sprite.Create(image_texture, new Rect(0, 0, image_texture.width, image_texture.height), Vector2.zero);
          if (image != null) {
              isFlagSet = true;
              countryFlagSprite.sprite = image;
          } else {
              return;
          }

          countryFlag.transform.parent = transform;
          float width = countryFlagSprite.bounds.size.x;
          countryFlag.transform.localPosition = new Vector3(- width / 2 * 1.5f, .5f, 0);
          countryFlag.transform.localScale = new Vector3(1.5f, 1.5f, 1.5f);

      }
  }
}
