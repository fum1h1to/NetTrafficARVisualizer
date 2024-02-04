using UnityEngine;
using System.Collections;
using System;
using UnityEngine.Android;

namespace MainAR.Scripts
{
    public class PermissionManager : MonoBehaviour
    {
        IEnumerator Start()
        {
            Debug.Log("Permission Start");
            if (!Permission.HasUserAuthorizedPermission (Permission.FineLocation)) {
                yield return RequestUserPermission (Permission.FineLocation);
                Input.compass.enabled = true;
                Input.location.Start();
            }

            yield break;
        }

        bool isRequesting;

        IEnumerator OnApplicationFocus(bool hasFocus)
        {
            yield return null;

            if (isRequesting && hasFocus)
            {
                isRequesting = false;
            }
        }

        IEnumerator RequestUserPermission(string permission)
        {
            isRequesting = true;
            Permission.RequestUserPermission(permission);
            
            float timeElapsed = 0;
            while (isRequesting)
            {
                if (timeElapsed > 0.5f){
                    isRequesting = false;
                    yield break;
                }
                timeElapsed += Time.deltaTime;

                yield return null;
            }
            yield break;
        }
    }
}