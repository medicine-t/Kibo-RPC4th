package com.stellarcoders.utils;

import gov.nasa.arc.astrobee.types.Point;

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

    public double dot(Vector3 v){
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public Vector3 prod(Vector3 v){
        return new Vector3(this.y*this.z - this.z*v.y,this.z*v.x - this.x*v.z,this.x*v.y - this.y*v.x);
    }

    public double norm(){
        return Math.sqrt(x*x + y*y + z*z);
    }

    public Vector3 normalize(){
        return new Vector3(x / norm(), y/ norm(), z/norm());
    }
}
