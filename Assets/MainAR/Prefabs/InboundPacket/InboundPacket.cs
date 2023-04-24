using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class InboundPacket : MonoBehaviour
{
    private float packetAnimationTime = 8f;
    private float packetAnimationAfterTime = 6f;
    private Vector3 position;
    private Vector3 velocity;
    private Camera arCamera;

    // Start is called before the first frame update
    void Start()
    {

        GameObject mainCamObj = GameObject.Find("AR Camera");
        arCamera = mainCamObj.GetComponent<Camera>();

        position = transform.position;
    }

    // Update is called once per frame
    void Update()
    {
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
}
