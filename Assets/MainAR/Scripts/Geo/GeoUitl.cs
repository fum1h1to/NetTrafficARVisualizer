using UnityEngine;
using System;
using System.Device.Location;

namespace MainAR.Scripts.Geo {
  public class GeoUtil {
    public static Vector3 ConvertCoordinate(GeoCoordinate currentCoordinate, GeoCoordinate targetCoordinate, float currentHeading, double distance)
    {
        double altitude = 1;
        double bearing = CalculateBearing(currentCoordinate, targetCoordinate);

        // tan(90)
        if (Mathf.Approximately(currentHeading, (float)bearing))
        {
            return new Vector3(0, (float)altitude, (float)distance);
        // tan(-90)
        } else if (Mathf.Approximately(currentHeading, (float)-bearing))
        {
            return new Vector3(0, (float)altitude, (float)-distance);
        } else
        {
            double angleInRadian = ToRadian(bearing - currentHeading);
            return new Vector3(
                (float)(Math.Sin(angleInRadian) * distance),
                (float)altitude,
                (float)(Math.Cos(angleInRadian) * distance)
            );
        }        
    }

    // https://www.movable-type.co.uk/scripts/latlong.html
    public static double CalculateBearing(GeoCoordinate origin, GeoCoordinate target)
    {
        double φ1 = ToRadian(origin.Latitude);
        double φ2 = ToRadian(target.Latitude);
        double λ1 = ToRadian(origin.Longitude);
        double λ2 = ToRadian(target.Longitude);

        double y = Math.Sin(λ2 - λ1) * Math.Cos(φ2);
        double x = Math.Cos(φ1) * Math.Sin(φ2) - Math.Sin(φ1) * Math.Cos(φ2) * Math.Cos(λ2 - λ1);
        double θ = Math.Atan2(y, x);
        double bearing = (ToDegree(θ) + 360) % 360;

        return bearing;
    }

    public static double ToRadian(double degree)
    {
        return degree * Math.PI / 180;
    }

    public static double ToDegree(double radian)
    {
        return radian * 180 / Math.PI;
    }
  }
}