using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class OutboundPacket : MonoBehaviour
{
    private float packetAnimationTime = 8f;
    private float packetAnimationAfterTime = 6f;
    private Vector3 endPosition;
    private Vector3 position;
    private Vector3 diffScale;
    private Vector3 velocity;

    // Start is called before the first frame update
    void Start()
    {
        position = transform.position;
        diffScale = transform.localScale / packetAnimationAfterTime;
    }

    // Update is called once per frame
    void Update()
    {
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
    }

    public void SetEndPosition(float x, float y, float z) {
        endPosition = new Vector3(x, y, z);
    }
}
