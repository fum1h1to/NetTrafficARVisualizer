using UnityEngine;
using MainAR.Scripts.View;

namespace MainAR.Scripts.Compass
{
  public class CompassState
  {
    private float _compassHeading;
    private UIController _uiController;

    public CompassState(UIController uiController)
    {
      _uiController = uiController;
    }

    public void UpdateCompassHeading()
    {
      _compassHeading = Input.compass.trueHeading;
      _uiController.SetDebugHeading(_compassHeading);
    }

    public float GetCompassHeading()
    {
      return _compassHeading;
    }
  }
}