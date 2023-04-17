using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Android;

public class NativeCodeInterface : MonoBehaviour
{
    [SerializeField] GameObject packetObject;

    // Start is called before the first frame update
    void Start() {

    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public void OnClick() {
        CreateInboundPacketObject(0, 0, 0, "192.168.1.1", "172.18.1.1", "TCP");

    }


    public void CreateInboundPacketObject(float x, float y, float z, string srcAddr, string dstAddr, string protcol) {
        Instantiate(packetObject, new Vector3(x, y, z), Quaternion.identity);

    }
}
