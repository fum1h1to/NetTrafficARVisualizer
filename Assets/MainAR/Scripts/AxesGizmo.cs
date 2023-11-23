using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace MainAR.Scripts
{
    public class AxesGizmo : MonoBehaviour
    {
        private GameObject axesX;
        private GameObject axesY;
        private GameObject axesZ;
        private float axisLength = 1.0f;
        // Start is called before the first frame update
        void Start()
        {
            // X軸 (赤色)
            axesX = GameObject.CreatePrimitive(PrimitiveType.Cube);
            axesX.GetComponent<Renderer>().material.color = Color.red;
            axesX.transform.position = new Vector3(axisLength / 2, 0, 0);
            axesX.transform.localScale = new Vector3(axisLength, 0.01f, 0.01f);

            // Y軸 (緑色)
            axesY = GameObject.CreatePrimitive(PrimitiveType.Cube);
            axesY.GetComponent<Renderer>().material.color = Color.green;
            axesY.transform.position = new Vector3(0, axisLength / 2, 0);
            axesY.transform.localScale = new Vector3(0.01f, axisLength, 0.01f);

            // Z軸 (青色)
            axesZ = GameObject.CreatePrimitive(PrimitiveType.Cube);
            axesZ.GetComponent<Renderer>().material.color = Color.blue;
            axesZ.transform.position = new Vector3(0, 0, axisLength / 2);
            axesZ.transform.localScale = new Vector3(0.01f, 0.01f, axisLength);
        }

        // Update is called once per frame
        void Update()
        {
            
        }
    }
}
