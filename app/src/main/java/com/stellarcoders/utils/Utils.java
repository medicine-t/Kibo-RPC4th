package com.stellarcoders.utils;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
public class Utils {
    public static Quaternion inverseQuaternion(Quaternion q){
        return new Quaternion(q.getX() * -1,q.getY() * -1, q.getZ() * -1,q.getW());
    }

    public static Double distance3DSquare(Point p1,Point p2){
        return (p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) +  (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()) +(p1.getZ() - p2.getZ()) * (p1.getZ() - p2.getZ());
    }
}
