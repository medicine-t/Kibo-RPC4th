package com.stellarcoders;
import gov.nasa.arc.astrobee.types.Point;
public class Area {
    public final float x_min;
    public final float y_min;
    public final float z_min;

    public final float x_max;
    public final float y_max;
    public final float z_max;

    Area(float x_min,float y_min,float z_min,float x_max,float y_max, float z_max){
        this.x_min = x_min;
        this.y_min = y_min;
        this.z_min = z_min;
        this.x_max = x_max;
        this.y_max = y_max;
        this.z_max = z_max;
    }

    public final Boolean isInclude(Point point){
        if(x_min <= point.getX() && point.getX() <= x_max){
            if(y_min <= point.getY() && point.getY() <= y_max){
                if(z_min <= point.getZ() && point.getZ() <= z_max){
                    return true;
                }
            }
        }
        return false;
    }

}
