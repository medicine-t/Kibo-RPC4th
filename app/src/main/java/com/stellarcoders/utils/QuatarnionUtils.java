package com.stellarcoders.utils;

import gov.nasa.arc.astrobee.types.Quaternion;
public class QuatarnionUtils {
    public final Quaternion inverseQuaternion(Quaternion q){
        return new Quaternion(q.getX() * -1,q.getY() * -1, q.getZ() * -1,q.getW());
    }
}
