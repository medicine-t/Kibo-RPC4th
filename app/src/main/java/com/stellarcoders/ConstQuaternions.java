package com.stellarcoders;

import java.util.ArrayList;

import gov.nasa.arc.astrobee.types.Quaternion;

public class ConstQuaternions {
    final Quaternion start = new Quaternion(1,0,0,0);
    final Quaternion goal = new Quaternion(0, 0 ,-0.707f ,0.707f);

    final ArrayList<Quaternion> points = new ArrayList<Quaternion>(){
        {
            add(new Quaternion(0, 0 ,-0.707f, 0.707f));
            add(new Quaternion(0.5f, 0.5f, -0.5f, 0.5f));
            add(new Quaternion(0f,0.707f,0f,0.707f));
            add(new Quaternion(0, 0 ,-1,0));
            add(new Quaternion(-0.5f,-0.5f,-0.5f,0.5f));
            add(new Quaternion(0,0,0,1));
            add(new Quaternion(0,0.707f,0,0.707f));
        }
    };

    final ArrayList<Quaternion> targets = new ArrayList<Quaternion>(){
        {
            add(new Quaternion(0.707f, 0,0,0.707f));
            add(new Quaternion(0,0,0,1));
            add(new Quaternion(0.707f,0,0,0.707f));
            add(new Quaternion(-0.5f, 0.5f,-0.5f,0.5f));
            add(new Quaternion(1, 0,0,0));
            add(new Quaternion(0.5f,0.5f,-0.5f,-0.5f));
        }
    };

    final Quaternion QRCodePos = new Quaternion(0,0,0,1);
}
