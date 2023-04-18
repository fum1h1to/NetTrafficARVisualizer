using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Android;

public class NativeCodeInterface : MonoBehaviour
{
    [SerializeField] GameObject packetObject;
    private AndroidJavaObject mActivity;

    // Start is called before the first frame update
    void Start() {
        AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");

        mActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
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
        Instantiate(packetObject, new Vector3(json.x, json.y, json.z), Quaternion.identity);

    }
}

class NativeCodeJson {
    public float x;
    public float y;
    public float z;
    public string srcAddr;
    public string dstAddr;
    public string protocol;
}