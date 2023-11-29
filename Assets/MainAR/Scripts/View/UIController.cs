using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using MainAR.Scripts;
using MainAR.Scripts.NativeCodeInterface;

namespace MainAR.Scripts.View
{
  public class UIController
  {
    private NativeCodeCommander _nativeCodeCommander;
    private UIManager _uiManager;
    private Camera _arCamera;
    private DebugTextModel _debugTextModel;

    public UIController(NativeCodeCommander nativeCodeCommander, UIManager uiManager, Camera arCamera)
    {
      _nativeCodeCommander = nativeCodeCommander;
      _uiManager = uiManager;
      _arCamera = arCamera;

      _debugTextModel = new DebugTextModel();
    }

    public void Test()
    {
      _nativeCodeCommander.Test();
    }

    public void ResetDebugText()
    {
      _debugTextModel = new DebugTextModel();
      _uiManager.SetDebugText(_debugTextModel);
    }

    public void SetDebugHeading(float heading)
    {
      _debugTextModel.heading = heading;
      _uiManager.SetDebugText(_debugTextModel);
    }

    public void ToggleCapture()
    {
      bool mCaptureRunning = _nativeCodeCommander.GetCaptureRunning();
      if (mCaptureRunning) {
        _nativeCodeCommander.StopCapture();
        _uiManager.SetSelectText("Start");
      } else {
        _nativeCodeCommander.StartCapture();
        _uiManager.SetSelectText("Stop");
      }
    }

    public void VisibleArrow(Vector3 generatedObjectPosition) {

      Vector3 toObject = generatedObjectPosition - _arCamera.transform.position;
      Vector3 cameraForward = _arCamera.transform.forward;

        // オブジェクトの方向を計算
      float angle = Vector3.SignedAngle(cameraForward, toObject, Vector3.up);

      if (angle > 0)
      {
        // オブジェクトが画面右側にある場合の処理
        _uiManager.VisibleArrowRight();
      }
      else
      {
        // オブジェクトが画面左側にある場合の処理
        _uiManager.VisibleArrowLeft();
      }
    }
  }

}