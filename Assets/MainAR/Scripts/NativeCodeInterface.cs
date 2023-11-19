using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Android;

public class NativeCodeInterface : MonoBehaviour
{
    [SerializeField] GameObject inboundPacketObject;
    [SerializeField] GameObject outboundPacketObject;
    private AndroidJavaObject mActivity;
    private Camera arCamera;

    // Start is called before the first frame update
    void Start() {
        AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");

        mActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");

        GameObject mainCamObj = GameObject.Find("AR Camera");
        arCamera = mainCamObj.GetComponent<Camera>();
    }

    // Update is called once per frame
    void Update()
    {

    }

    public void OnClick() {
        mActivity.Call("test");
    }

    public void CreateInboundPacketObject(string message) {
        NativeCodeJson json = JsonUtility.FromJson<NativeCodeJson>(message);
        InboundPacket inboutPacket = (Instantiate(inboundPacketObject, new Vector3(json.x, json.y, json.z), Quaternion.identity) as GameObject).GetComponent<InboundPacket>();
        inboutPacket.SetCountryCode(json.countryCode);
    }  

    public void CreateOutboundPacketObject(string message) {
        NativeCodeJson json = JsonUtility.FromJson<NativeCodeJson>(message);
        OutboundPacket outboutPacket = (Instantiate(outboundPacketObject , arCamera.transform.position - new Vector3(0, 0.1f, 0), Quaternion.identity) as GameObject).GetComponent<OutboundPacket>();
        outboutPacket.SetEndPosition(json.x, json.y, json.z);
        outboutPacket.SetCountryCode(json.countryCode);
    }
}

class NativeCodeJson {
    public float lat;
    public float lng;
    public string srcAddr;
    public string dstAddr;
    public string protocol;
    public string countryCode;
}