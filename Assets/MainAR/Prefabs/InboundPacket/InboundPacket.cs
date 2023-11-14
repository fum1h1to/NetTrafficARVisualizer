using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.XR.ARFoundation;

public class InboundPacket : MonoBehaviour
{
    private float packetAnimationTime = 8f;
    private float packetAnimationAfterTime = 6f;
    private Renderer _Renderer;
    private Vector3 position;
    private Vector3 velocity;
    private ARCameraManager arCamera;
    private bool isFlagSet = false;
    private GameObject countryFlag;

    public bool IsVisible =>  _Renderer.isVisible;
    
    // Start is called before the first frame update
    void Start()
    {
        _Renderer = GetComponent<Renderer>();
        arCamera = FindObjectOfType<ARCameraManager>();

        position = transform.position;

        var mainUI = FindObjectOfType<UIManager>();
        if(!this.IsVisible) {
            mainUI.VisibleArrow(this.transform.position);
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
