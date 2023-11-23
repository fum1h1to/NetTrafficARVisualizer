using System.Collections;
using UnityEngine;
using UnityEngine.XR.ARFoundation;
using MainAR.Scripts.Compass;

namespace MainAR.Scripts.AR
{
  public class ARController
  {
    private ARSession _session;
    private ARSessionOrigin _sessionOrigin;
    private CompassState _compassState;
    public ARController(ARSession session, ARSessionOrigin sessionOrigin, CompassState compassState)
    {
      _compassState = compassState;
      _session = session;
      _sessionOrigin = sessionOrigin;

      ARSession.stateChanged += OnARStateChanged;
    }

    public IEnumerator CheckARSupport() {
      if (ARSession.state == ARSessionState.None || ARSession.state == ARSessionState.CheckingAvailability)
            yield return ARSession.CheckAvailability();

        if (ARSession.state == ARSessionState.Unsupported)
            yield break;

        _session.enabled = true;
    }

    private void OnARStateChanged(ARSessionStateChangedEventArgs args) {
      switch(args.state)
      {
        case ARSessionState.SessionInitializing:
          _session.Reset();
          _sessionOrigin.transform.localRotation = Quaternion.Euler(0, 0, 0);

          Debug.Log("ARSessionState.SessionInitializing");
          break;
        
        case ARSessionState.SessionTracking:
          Debug.Log("ARSessionState.SessionTracking");
          _compassState.UpdateCompassHeading();
          _sessionOrigin.transform.localRotation = Quaternion.Euler(0, _compassState.GetCompassHeading(), 0);
          break;
      }
    }
  }
}