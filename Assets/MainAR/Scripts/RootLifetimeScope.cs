using VContainer;
using VContainer.Unity;
using UnityEngine;
using UnityEngine.XR.ARFoundation;

using MainAR.Scripts.Packet;
using MainAR.Scripts.Compass;
using MainAR.Scripts.View;
using MainAR.Scripts.NativeCodeInterface;
using MainAR.Scripts.AR;

namespace MainAR.Scripts
{
    public class RootLifetimeScope : LifetimeScope
    {
        [SerializeField] PacketPrefabData _packetPrefabData;
        [SerializeField] GameObject _arCameraObject;
        [SerializeField] ARSession _session;
        [SerializeField] ARSessionOrigin _sessionOrigin;
        [SerializeField] NativeCodeExecutor _nativeCodeExecutor;
        [SerializeField] UIManager _uiManager;
        [SerializeField] ButtonManager _buttonManager;

        protected override void Configure(IContainerBuilder builder)
        {
            builder.RegisterEntryPoint<RootInitializer>();
            builder.Register<ARController>(Lifetime.Singleton);
            builder.Register<CompassState>(Lifetime.Singleton);
            builder.Register<PacketFactory>(Lifetime.Singleton);
            builder.Register<UIController>(Lifetime.Singleton);
            builder.Register<NativeCodeCommander>(Lifetime.Singleton);
            builder.RegisterInstance<PacketPrefabData>(_packetPrefabData);
            builder.RegisterInstance<Camera>(_arCameraObject.GetComponent<Camera>());
            builder.RegisterInstance<ARSession>(_session);
            builder.RegisterInstance<ARSessionOrigin>(_sessionOrigin);
            builder.RegisterInstance<UIManager>(_uiManager);
            builder.RegisterComponent(_nativeCodeExecutor);
            builder.RegisterComponent(_buttonManager);

        }
    }

}
