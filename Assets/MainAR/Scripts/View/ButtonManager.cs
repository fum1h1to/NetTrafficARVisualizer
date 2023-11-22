using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using UnityEngine.UI;
using VContainer;

namespace MainAR.Scripts.View
{
  public class ButtonManager : MonoBehaviour
  {
    private const float ARROW_VISIBLE_TIME = 2f;
    [SerializeField] Button captureToggleButton;
    [SerializeField] Button testButton;
    private UIController _uiController;
    void Start() {
      captureToggleButton.onClick.AddListener(ToggleCapture);
      testButton.onClick.AddListener(Test);
    }

    public void Test() {
      _uiController.Test();
    }

    public void ToggleCapture() {
      _uiController.ToggleCapture();
    }

    [Inject]
    public void Construct(UIController uiController) {
      _uiController = uiController;
    }
  }
}