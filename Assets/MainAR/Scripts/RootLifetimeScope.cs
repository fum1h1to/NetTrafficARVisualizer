using VContainer;
using VContainer.Unity;
using UnityEngine;

using MainAR.Scripts.Packet;

namespace MainAR.Scripts
{
    public class RootLifetimeScope : LifetimeScope
    {
        [SerializeField] PacketPrefabData _packetPrefabData;
        [SerializeField] GameObject _arCameraObject;
        [SerializeField] NativeCodeInterface _nativeCodeInterface;

        protected override void Configure(IContainerBuilder builder)
        {
            builder.RegisterEntryPoint<RootInitializer>();
            builder.RegisterInstance<PacketPrefabData>(_packetPrefabData);
            builder.RegisterInstance<Camera>(_arCameraObject.GetComponent<Camera>());
            builder.Register<PacketFactory>(Lifetime.Singleton);
            builder.RegisterComponent(_nativeCodeInterface);

        }
    }

}
