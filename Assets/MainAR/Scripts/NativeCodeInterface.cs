using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class NativeCodeInterface : MonoBehaviour
{
    [SerializeField] GameObject packetObject;

    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public void OnClick() {
        CreateInboundPacketObject("test");

    }

    public void CreateInboundPacketObject(string testText) {
        Instantiate(packetObject, new Vector3(0, 0, 0), Quaternion.identity);
        Debug.Log("called CreateInboundPacketObject: " + testText);
    }
}
