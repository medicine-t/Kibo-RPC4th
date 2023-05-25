package com.stellarcoders.utils;

import android.util.Log;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
public class Utils {
    public static Quaternion inverseQuaternion(Quaternion q){
        return new Quaternion(q.getX() * -1,q.getY() * -1, q.getZ() * -1,q.getW());
    }

    public static Double distance3DSquare(Point p1,Point p2){
        return (p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) +  (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()) +(p1.getZ() - p2.getZ()) * (p1.getZ() - p2.getZ());
    }

    /**
     *
     * @param p1    始点
     * @param p2    終点
     * @param a   衝突判定の面の頂点[3]
     * @return 衝突時 true
     *
     * 参考: https://pheema.hatenablog.jp/entry/ray-triangle-intersection
     */
    public static Boolean isPolyLineCollision(Point p1,Point p2,Point[] a){
        Point a1 = a[0];
        Point a2 = a[1];
        Point a3 = a[2];

        Vector3 d = new Vector3(p2,p1).normalize();
        Vector3 e1 = new Vector3(a2,a1);
        Vector3 e2 = new Vector3(a3,a1);
        Vector3 r = new Vector3(p1,a1);

        final double eps = 1e-6;
        Vector3 alpha = d.prod(e2);
        double det = e1.dot(alpha);
        // 三角形に対して、レイが平行に入射するような場合 det = 0 となる。
        // det が小さすぎると 1/det が大きくなりすぎて数値的に不安定になるので
        // det ≈ 0 の場合は交差しないこととする。
        if (-eps < det && det < eps) {
            return false;
        }

        double invDet = 1.0 / det;
        double u = alpha.dot(r) * invDet;
        if (u < 0.0f || u > 1.0f) {
            return false;
        }

        Vector3 beta = r.prod(e1);
        double v = d.dot(beta) * invDet;
        if (v < 0.0f || u + v > 1.0f) {
            return false;
        }

        double t = e2.dot(beta) * invDet;
        if (t < 0.0f) {
            return false;
        }

        double targetNorm = new Vector3(p2,p1).norm();
        if(t - targetNorm > eps){
            return false;
        }

        Log.i("StellarCoders",String.format("Collision t:%e, u:%e, v:%e",t,u,v));
        Log.i("StellarCoders",String.format("Vector norm is %e, t - targetNorm",targetNorm,t - targetNorm));
        return true ;
    }
}
