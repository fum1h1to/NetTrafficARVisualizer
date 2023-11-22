using UnityEngine;

namespace MainAR.Scripts.Compass
{
  public class CompassState
  {
    private float _compassHeading;

    public void UpdateCompassHeading()
    {
      _compassHeading = Input.compass.trueHeading;
    }

    public float GetCompassHeading()
    {
      return _compassHeading;
    }
  }
}