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
	public class InboundPacket : MonoBehaviour
	{
		private float packetAnimationTime = 8f;
		private float packetAnimationAfterTime = 6f;
		private Renderer _Renderer;
		private Vector3 position;
		private Vector3 velocity;
		private Camera arCamera;
		private UIController uiController;
		private bool isFlagSet = false;
		private GameObject countryFlag;

		public bool IsVisible =>  _Renderer.isVisible;

		public static InboundPacket Create(
			GameObject inboundPacketBaseObject,
			GameObject decoratePacketObject,
			Color packetColor,
			PacketNativeInterfaceModel packetObj,
			UIController uiController,
			Camera arCamera
		) {
			GameObject baseObj= Instantiate(inboundPacketBaseObject, new Vector3(0, 0, 0), Quaternion.identity) as GameObject;
			baseObj.SetActive(false);
			GameObject decorateObj = Instantiate(decoratePacketObject, new Vector3(0, 0, 0), Quaternion.identity, baseObj.transform) as GameObject;
			
			InboundPacket inboundPacket = baseObj.GetComponent<InboundPacket>();
			inboundPacket.SetPacketScale(packetObj.count);
			inboundPacket.SetUIController(uiController);
			inboundPacket.SetArCamera(arCamera);
			inboundPacket.SetStartPosition(packetObj.lat, packetObj.lng);
			inboundPacket.SetCountryCode(packetObj.countryCode);
			inboundPacket.setTrailColor(packetColor);

			baseObj.SetActive(true);
			return inboundPacket;
		}

		private void SetStartPosition(float targetLatitude, float targetLongitude) {
			if (Input.location.isEnabledByUser)
			{
				if (Input.location.status == LocationServiceStatus.Running)
				{
					LocationInfo lastData = Input.location.lastData;
					GeoCoordinate currentCoordinate = new GeoCoordinate(lastData.latitude, lastData.longitude);

					GeoCoordinate targetCoordinate = new GeoCoordinate(targetLatitude, targetLongitude);

					float heading = 0;

					transform.position = GeoUtil.ConvertCoordinate(currentCoordinate, targetCoordinate, heading, 5);
				}
			}
		}

		private void SetPacketScale(int packetSize) {
			transform.localScale = transform.localScale * (packetSize / 10f);
		}

		private void setTrailColor(Color packetColor) {
			GetComponent<TrailRenderer>().material.color = packetColor;
		}
		
		// Start is called before the first frame update
		void Start()
		{
			_Renderer = GetComponent<Renderer>();

			position = transform.position;

			if(!this.IsVisible) {
				uiController.VisibleArrow(this.transform.position);
			}
		}

		// Update is called once per frame
		void Update()
		{
			if (isFlagSet) {
				countryFlag.transform.LookAt(arCamera.transform.position);
			}
			if (packetAnimationTime >= 0f) {
				var acceleration = Vector3.zero;

				var diff = (arCamera.transform.position - new Vector3(0, 0.1f, 0)) - position;
				acceleration += (diff - velocity * packetAnimationTime) * 2f / (packetAnimationTime * packetAnimationTime);

				velocity += acceleration * Time.deltaTime;
				position += velocity * Time.deltaTime;
				transform.position = position;

				packetAnimationTime -= Time.deltaTime;
			} else {
				if (packetAnimationAfterTime >= 0f) {
					transform.localScale = Vector3.zero;
					packetAnimationAfterTime -= Time.deltaTime;
				} else {

					Destroy(this.gameObject);
				}
			}
		}

		private void SetArCamera(Camera arCamera) {
			this.arCamera = arCamera;
		}

		private void SetUIController(UIController uiController) {
			this.uiController = uiController;
		}

		private void SetCountryCode(string countryCode) {
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
			countryFlag.transform.localPosition = new Vector3(- width / 2 * 1.5f, 1f, 0);
			countryFlag.transform.localScale = new Vector3(1.5f, 1.5f, 1.5f);

		}

	}
}