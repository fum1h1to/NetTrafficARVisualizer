using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.XR.ARSubsystems;
using UnityEngine.XR.ARFoundation;

public class Packet : MonoBehaviour
{
    private float packetAnimationTime = 5f;
    private Vector3 position;
    private Vector3 velocity;
    private Camera arCamera;

    // Start is called before the first frame update
    void Start()
    {
        Debug.Log("packet Start");

        GameObject mainCamObj = GameObject.Find("AR Camera");
        arCamera = mainCamObj.GetComponent<Camera>();

        position = transform.position;
    }

    // Update is called once per frame
    void Update()
    {
        var acceleration = Vector3.zero;

        var diff = arCamera.transform.position - position;
        acceleration += (diff - velocity * packetAnimationTime) * 2f / (packetAnimationTime * packetAnimationTime);

        packetAnimationTime -= Time.deltaTime;
        if (packetAnimationTime <= 0f) {
            Destroy(this.gameObject);
        }

        velocity += acceleration * Time.deltaTime;
        position += velocity * Time.deltaTime;
        transform.position = position;
    }
}
