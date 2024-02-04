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
    private bool _initiatedFlag = true;
    public ARController(ARSession session, ARSessionOrigin sessionOrigin, CompassState compassState)
    {
      _compassState = compassState;
      _session = session;
      _sessionOrigin = sessionOrigin;

      ARSession.stateChanged += OnARStateChanged;
    }

    private void OnARStateChanged(ARSessionStateChangedEventArgs args) {
      switch(args.state)
      {
        case ARSessionState.SessionInitializing:
          if(_initiatedFlag) {
            _initiatedFlag = false;
            break;
          }

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