package com.stellarcoders.utils;

import gov.nasa.arc.astrobee.types.Point;
import com.stellarcoders.CheckPoints;
import com.stellarcoders.utils.PointI;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import android.util.Log;

public class Dijkstra3D {
    private final Point[][][] points;
    private final Double INF = Double.MAX_VALUE;
    private CheckPoints meta = new CheckPoints();
    public Dijkstra3D(){
        points = new Point[meta.num_div_x][meta.num_div_y][meta.num_div_z];
    }

    private final int[] dx = {
            -1, 0, 1,
            -1, 0, 1,
            -1, 0, 1,
            -1, 0, 1,
            -1, /*0,*/ 1,
            -1, 0, 1,
            -1, 0, 1,
            -1, 0, 1,
            -1, 0, 1};
    private final int[] dy = {
            -1,-1,-1,
             0, 0, 0,
             1, 1, 1,
            -1,-1,-1,
             0, /*0,*/ 0,
             1, 1, 1,
            -1,-1,-1,
             0, 0, 0,
             1, 1, 1};

    private final int[] dz = {
            -1,-1,-1,
            -1,-1,-1,
            -1,-1,-1,
             0, 0, 0,
             0, /*0,*/ 0,
             0, 0, 0,
             1, 1, 1,
             1, 1, 1,
             1, 1, 1};

    private final double[] cost = {
            1.7,1.4,1.7,
            1.4,1,1.4,
            1.7,1.4,1.7,

            1.4, 1, 1.4,
            1, /*0,*/ 1,
            1.4, 1, 1.4,

            1.7, 1.4, 1.7,
            1.4, 1, 1.4,
            1.7, 1.4, 1.7
    };

    class Node {
        PointI p;
        double d;

        Node() {
            p = new PointI();
            d = 0;
        }
    }

    class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node obj1, Node obj2) {
            double d1 = obj1.d;
            double d2 = obj2.d;

            //昇順
            return Double.compare(d2, d1);
        }
    }

    public Stack<PointI> dijkstra(PointI start, PointI goal){
        Log.i("StellarCoders",String.format("Dijkstra Called"));
        Log.i("StellarCoders",String.format("Check point size = %d,%d,%d",meta.num_div_x,meta.num_div_y,meta.num_div_z));
        PriorityQueue<Node> que = new PriorityQueue<Node>(new NodeComparator());
        double[][][] dist = new double[meta.num_div_x + 10][meta.num_div_y + 10][meta.num_div_z + 10];
        Node[][][] prev = new Node[meta.num_div_x + 10][meta.num_div_y + 10][meta.num_div_z + 10];
        for (int i = 0; i < meta.num_div_x + 10; i++) {
            for (int j = 0; j < meta.num_div_y + 10; j++) {
                for (int k = 0; k < meta.num_div_z + 10; k++) {
                    dist[i][j][k] = INF;
                }
            }
        }
        Log.i("StellarCoders",String.format("Dijkstra Start"));
        Log.i("StellarCoders",String.format("Start Point is %d,%d,%d",start.getX(),start.getY(),start.getZ()));
        dist[start.getX()][start.getY()][start.getZ()] = 0;
        Node s = new Node();
        s.p = start;

        que.add(s);
        Node trace = new Node();
        while(!que.isEmpty()){
            Node q = que.poll();
            PointI p = q.p;
            int x = p.getX();
            int y = p.getY();
            int z = p.getZ();
            if(p == goal){
                trace = q;
            }
            double distance = q.d;
            for(int d = 0;d < 26;d++){
                //border check
                if(Math.min(x + dx[d],Math.min(y + dy[d],z + dz[d])) < 0)continue;
                if(x + dx[d] >= meta.num_div_x)continue;
                if(y + dy[d] >= meta.num_div_y)continue;
                if(z + dz[d] >= meta.num_div_z)continue;
                if(!meta.checkPoints[x + dx[d]][y + dy[d]][z + dz[d]])continue;
                if(dist[x + dx[d]][y + dy[d]][z + dz[d]] > distance + cost[d]){
                    Node nxt = new Node();
                    nxt.p = new PointI(x + dx[d],y + dy[d],z + dz[d]);
                    nxt.d = distance + cost[d];
                    dist[x + dx[d]][y + dy[d]][z + dz[d]] = distance + cost[d];
                    prev[x + dx[d]][y + dy[d]][z + dz[d]] = q;
                    que.add(nxt);
                }
            }
        }

        Log.i("StellarCoders","Dijkstra finish. Start construct path");
        // re-construct path
        Stack<PointI> path = new Stack<>();
        while(!trace.p.isNan()){
            path.add(trace.p);
            trace = prev[trace.p.getX()][trace.p.getY()][trace.p.getZ()];
        }
        Log.i("StellarCoders","Dijkstra path construct finished");
        return path;
    }

}
