package com.stellarcoders;

import com.stellarcoders.Area;
import com.stellarcoders.ConstAreas;

import java.util.ArrayList;

import gov.nasa.arc.astrobee.types.Point;

public class CheckPoints {
    public final boolean[][][] checkPoints;
    private final float length = 11.665f - 9.5f; // Global:x_max - Global:x_min;
    private final float height = -6.0f - (-10.5f); //y
    private final float depth =  5.57f - 4.02f;//z

    private final float div_value = 0.05f;
    private final float offset = 0.05f;

    public Point idx2Point(int i, int j, int k){
        return new Point(offset + i*div_value, offset + j*div_value, offset + k*div_value);
    }

    CheckPoints() {
        int num_div_x = (int) Math.ceil((length - 2 * offset) / div_value);
        int num_div_y = (int) Math.ceil((height - 2 * offset) / div_value);
        int num_div_z = (int) Math.ceil((depth - 2 * offset) / div_value);
        this.checkPoints = new boolean[num_div_x][num_div_y][num_div_z];

        Area[] KIZs = new ConstAreas().KIZs;
        Area[] KOZs = new ConstAreas().KOZs;

        for(int i = 0;i < num_div_x;i++){
            for(int j = 0;j < num_div_y;j++){
                for (int k = 0; k < num_div_z; k++) {
                    boolean can = true;
                    for(Area kiz : KIZs){
                        if(!kiz.isInclude(idx2Point(i,j,k)))can = false;
                    }
                    for(Area koz: KOZs){
                        if(koz.isInclude(idx2Point(i,j,k)))can = false;
                    }
                    checkPoints[i][j][k] = can;
                }
            }
        }

    }

}
