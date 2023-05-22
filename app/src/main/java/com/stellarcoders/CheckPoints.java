package com.stellarcoders;

import com.stellarcoders.utils.PointI;

import gov.nasa.arc.astrobee.types.Point;

public class CheckPoints {
    public final boolean[][][] checkPoints;
    private final float length = 11.665f - 9.5f; //2.165 // Global:x_max - Global:x_min;
    private final float height = -6.0f - (-10.5f); //4.5 //y
    private final float depth =  5.57f - 4.02f;// 1.55 //z

    private final float div_value = 0.05f;
    private final float offset = 0.05f;
    public int num_div_x = (int) Math.ceil((length - 2 * offset) / div_value);
    public int num_div_y = (int) Math.ceil((height - 2 * offset) / div_value);
    public int num_div_z = (int) Math.ceil((depth - 2 * offset) / div_value);
    public Point idx2Point(int i, int j, int k){
        return new Point(offset + i*div_value + 9.5, offset + j*div_value - 10.5, offset + k*div_value + 4.02);
    }
    public Point idx2Point(PointI pi){
        int i = pi.getX();
        int j = pi.getY();
        int k = pi.getZ();
        return new Point(offset + i*div_value + 9.5, offset + j*div_value - 10.5, offset + k*div_value + 4.02);
    }

    public Area idxArea(PointI pi){
        return new Area(pi.getX() - div_value,pi.getY() - div_value,pi.getZ() - div_value,pi.getX() + div_value,pi.getY() + div_value,pi.getZ() + div_value);
    }

    public  PointI Point2I(Point p){
        double x = p.getX() - 9.5f;
        double y = p.getY() - (-10.5f);
        double z = p.getZ() - 4.02f;

        int i = (int) Math.floor((x - this.offset) / div_value);
        int j = (int) Math.floor((y - this.offset) / div_value);
        int k = (int) Math.floor((z - this.offset) / div_value);
        return new PointI(i,j,k);
    }

    public CheckPoints() {

        this.checkPoints = new boolean[num_div_x][num_div_y][num_div_z];

        Area[] KIZs = new ConstAreas().KIZs;
        Area[] KOZs = new ConstAreas().KOZs;

        for(int i = 0;i < num_div_x;i++){
            for(int j = 0;j < num_div_y;j++){
                for (int k = 0; k < num_div_z; k++) {
                    boolean can = false;
                    for(Area kiz : KIZs){
                        can = can || kiz.isInclude(idx2Point(i,j,k));
                    }
                    for(Area koz: KOZs){
                        if(koz.isInclude(idx2Point(i,j,k)))can = false;
                        if(koz.isIntersect(idxArea(new PointI(i,j,k))))can = false;
                    }
                    checkPoints[i][j][k] = can;
                }
            }
        }

    }

}
