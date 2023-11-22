using VContainer.Unity;
using UnityEngine;
using System;
using System.Device.Location;
using MainAR.Scripts.View;

namespace MainAR.Scripts
{
    public class RootInitializer : IInitializable
    {
        private ARController _arController;
        private UIController _uiController;
        public RootInitializer(ARController arController, UIController uiController)
        {
          _arController = arController;
          _uiController = uiController;
        }

        public void Initialize()
        {
          Input.compass.enabled = true;
          Input.location.Start();

          _arController.CheckARSupport();
          _uiController.ResetDebugText();
        }
    }
}