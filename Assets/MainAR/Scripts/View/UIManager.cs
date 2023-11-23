using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using UnityEngine.UI;
using VContainer;

namespace MainAR.Scripts.View
{
  public class UIManager : MonoBehaviour
  {
    private const float ARROW_VISIBLE_TIME = 2f;
    [SerializeField] TextMeshProUGUI selectText;
    [SerializeField] GameObject arrowRight;
    [SerializeField] GameObject arrowLeft;
    [SerializeField] TextMeshProUGUI debugText;
    private float arrowVisibleTime = ARROW_VISIBLE_TIME;
    private bool isArrowLeftVisible = false;
    private bool isArrowRightVisible = false;
    void Start() {

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

    public void SetDebugText(DebugTextModel debugTextModel) {
      debugText.text = debugTextModel.ToDebugString();
    }

    public void SetSelectText(string text) {
      selectText.text = text;
    }

    public void VisibleArrowLeft() {
      isArrowRightVisible = false;
      isArrowLeftVisible = true;
      arrowVisibleTime = ARROW_VISIBLE_TIME;
    }

    public void VisibleArrowRight() {
      isArrowLeftVisible = false;
      isArrowRightVisible = true;
      arrowVisibleTime = ARROW_VISIBLE_TIME;
    }
  }
}