using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using MainAR.Scripts;

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
      var cameraPosition = _arCamera.transform.position;
      var cameraRotation = _arCamera.transform.rotation;

      var cameraToObjectVector = generatedObjectPosition - cameraPosition;
      var cameraToObjectDirection = cameraRotation * cameraToObjectVector.normalized;

      var angle = Vector3.SignedAngle(cameraToObjectDirection, Vector3.forward, Vector3.up);

      if (angle < 0)
      {
        // オブジェクトが画面左側にある場合の処理
        _uiManager.VisibleArrowLeft();
      }
      else
      {
        // オブジェクトが画面右側にある場合の処理
        _uiManager.VisibleArrowRight();
      }
    }
  }

}