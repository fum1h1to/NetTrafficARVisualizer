using VContainer.Unity;
using UnityEngine;
using System;
using System.Device.Location;

namespace MainAR.Scripts
{
    public class RootInitializer : IInitializable
    {
        public void Initialize()
        {
          Input.compass.enabled = true;
          Input.location.Start();
        }
    }
}