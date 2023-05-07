using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

public class UIManager : MonoBehaviour
{
  [SerializeField] TextMeshProUGUI selectText;
  
  private const float ARROW_VISIBLE_TIME = 2f;
  private Camera arCamera;
  private AndroidJavaObject mActivity;
  private float arrowVisibleTime = ARROW_VISIBLE_TIME;
  [SerializeField] GameObject arrowLeft;
  private bool isArrowLeftVisible = false;
  [SerializeField] GameObject arrowRight;
  private bool isArrowRightVisible = false;
  void Start() {
    AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");

    mActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");

    
    GameObject mainCamObj = GameObject.Find("AR Camera");
    arCamera = mainCamObj.GetComponent<Camera>();

    arrowLeft.SetActive(false);
    arrowRight.SetActive(false);
  }

  void Update() {
    arrowLeft.SetActive(false);
    arrowRight.SetActive(false);

    if (isArrowLeftVisible) {
      arrowVisibleTime -= Time.deltaTime;
      arrowLeft.SetActive(true);
      if (arrowVisibleTime <= 0f) {
        isArrowLeftVisible = false;
      }
    } else if (isArrowRightVisible) {
      arrowVisibleTime -= Time.deltaTime;
      arrowRight.SetActive(true);
      if (arrowVisibleTime <= 0f) {
        isArrowRightVisible = false;
      }
    }

  }

  public void ToggleCapture() {
    bool mCaptureRunning = mActivity.Call<bool>("getCaptureRunning");
    if (mCaptureRunning) {
      mActivity.Call("stopCapture");
      selectText.text = "Start";
    } else {
      mActivity.Call("startCapture");
      selectText.text = "Stop";
    }
  }

  public void VisibleArrow(Vector3 generatedObjectPosition) {
    var cameraPosition = arCamera.transform.position;
    var cameraRotation = arCamera.transform.rotation;

    var cameraToObjectVector = generatedObjectPosition - cameraPosition;
    var cameraToObjectDirection = cameraRotation * cameraToObjectVector.normalized;

    var angle = Vector3.SignedAngle(cameraToObjectDirection, Vector3.forward, Vector3.up);

    if (angle < 0)
    {
      // オブジェクトが画面左側にある場合の処理
      VisibleArrowLeft();
    }
    else
    {
      // オブジェクトが画面右側にある場合の処理
      VisibleArrowRight();
    }
  }

  private void VisibleArrowLeft() {
    isArrowRightVisible = false;
    isArrowLeftVisible = true;
    arrowVisibleTime = ARROW_VISIBLE_TIME;
  }

  private void VisibleArrowRight() {
    isArrowLeftVisible = false;
    isArrowRightVisible = true;
    arrowVisibleTime = ARROW_VISIBLE_TIME;
  }
}
