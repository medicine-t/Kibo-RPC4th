package com.stellarcoders.utils;

import gov.nasa.arc.astrobee.types.Point;
import com.stellarcoders.CheckPoints;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class Dijkstra3D {
    private final Point[][][] points;
    private CheckPoints meta = new CheckPoints();
    Dijkstra3D(){

        points = new Point[meta.num_div_x][meta.num_div_y][meta.num_div_z];
    }

    private int[] dx = {
            -1, 0, 1,
            -1, 0, 1,
            -1, 0, 1,
            -1, 0, 1,
            -1, /*0,*/ 1,
            -1, 0, 1,
            -1, 0, 1,
            -1, 0, 1,
            -1, 0, 1};
    private int[] dy = {
            -1,-1,-1,
             0, 0, 0,
             1, 1, 1,
            -1,-1,-1,
             0, /*0,*/ 0,
             1, 1, 1,
            -1,-1,-1,
             0, 0, 0,
             1, 1, 1};

    private int[] dz = {
            -1,-1,-1,
            -1,-1,-1,
            -1,-1,-1,
             0, 0, 0,
             0, /*0,*/ 0,
             0, 0, 0,
             1, 1, 1,
             1, 1, 1,
             1, 1, 1};

    class Node {
        Point p;
        Point prev;
        int d;
    }

    class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node obj1, Node obj2) {
            int d1 = obj1.d;
            int d2 = obj2.d;

            //昇順
            if(d1 > d2){
                return -1;
            }else if(d1 == d2){
                return 0;
            }else{
                return 1;
            }
        }
    }

    Queue<Point> dijkstra(Point start,Point goal){

        PriorityQueue<Node> que = new PriorityQueue<Node>(new NodeComparator());
        int[][][] dist = new int[meta.num_div_x][meta.num_div_y][meta.num_div_z];
        for (int i = 0; i < meta.num_div_x; i++) {
            for (int j = 0; j < meta.num_div_y; j++) {
                for (int k = 0; k < meta.num_div_z; k++) {
                    dist[i][j][k] = -1;
                }
            }
        }

        Node s = new Node();


    }

}
