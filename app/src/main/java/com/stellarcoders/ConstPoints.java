package com.stellarcoders;
import java.util.ArrayList;

import gov.nasa.arc.astrobee.types.Point;

public class ConstPoints {
    public final Point start = new Point(9.815,-9.806,4.293);
    public final Point goal = new Point(11.143, -6.7607, 4.9654);

    // Default Value of Points
    public final ArrayList<Point> points = new ArrayList<Point>(){
        {
            add(new Point(11.2746,-9.92284,5.2988));
            add(new Point(10.612,-9.0709,4.48 ));
            add(new Point(10.71,-7.7,4.48 ));
            add(new Point(10.51,-6.7185,5.1804));
            add(new Point(11.114,-7.9756,5.3393));
            add(new Point(11.355,-8.9929 ,4.7818));
            add(new Point(11.369,-8.5518,4.48));//  QRCode
        }
    };

    //public final ArrayList<Point> points = new ArrayList<Point>(){
    //    {
    //        add(new Point(11.2625,-9.92284,5.3625));
    //        add(new Point(10.513384,-9.085172,4.48 ));
    //        add(new Point(10.6031,-7.71007,4.48 ));
    //        add(new Point(10.51,-6.673972,5.09531));
    //        add(new Point(11.102,-8.0304,5.3393));
    //        add(new Point(11.355,-8.989 ,4.8305));
    //    }
    //};

    public final ArrayList<Point> targets = new ArrayList<Point>(){
        {
            add(new Point(11.2625,-10.58,5.3625));
            add(new Point(10.513384,-9.085172,3.76203));
            add(new Point(10.6031, -7.71007 ,3.76093));
            add(new Point(9.866984,-6.673972, 5.09531));
            add(new Point(11.102 ,-8.0304 ,5.9076));
            add(new Point(12.023, -8.989, 4.8305));
        }
    };
    
    public final Point QRCodePos = new Point(11.369,-8.5518,4.48);
    public final Point QRCodeTarget = new Point(11.381944,-8.566172 ,3.76203);

}
