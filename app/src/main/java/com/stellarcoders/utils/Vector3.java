package com.stellarcoders.utils;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

public class Vector3 {
    private double x;
    private double y;
    private double z;


    public Vector3(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Point p1, Point p2){
        this.x = p1.getX() - p2.getX();
        this.y = p1.getY() - p2.getY();
        this.z = p1.getZ() - p2.getZ();
    }

    public Vector3 subtract(Vector3 v){
        return new Vector3(this.x - v.x,this.y - v.y,this.z - v.y);
    }

    public Vector3 add(Vector3 v){
        return new Vector3(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    public Vector3 prod(double d){
        return new Vector3(d * this.x,d * this.y,d * this.z);
    }


    public double dot(Vector3 v){
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public Vector3 cross(Vector3 v){
        return new Vector3(this.y * v.z - this.z * v.y,this.z * v.x - this.x * v.z,this.x * v.y - this.y * v.x);
    }

    public double norm(){
        return Math.sqrt(x*x + y*y + z*z);
    }

    public Vector3 normalize(){
        return new Vector3(x / norm(), y/ norm(), z/norm());
    }

    public Vector3 rotate(Quaternion q){
        Quaternion p = new Quaternion((float) this.x,(float) this.y,(float) this.z,0);
        Quaternion rotated = Utils.quaternionProd(p,q);
        return new Vector3(rotated.getX(),rotated.getY(),rotated.getZ());
    }

}
