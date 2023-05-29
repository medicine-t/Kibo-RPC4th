package com.stellarcoders.utils;

import android.util.Log;

import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;

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
        Point a0 = a[0];
        Point a1 = a[1];
        Point a2 = a[2];

        Vector3 d = new Vector3(p2,p1).normalize();
        Vector3 e1 = new Vector3(a1,a0);
        Vector3 e2 = new Vector3(a2,a0);
        Vector3 r = new Vector3(p1,a0);

        final double eps = 1e-6;
        Vector3 alpha = d.cross(e2);
        double det = e1.dot(alpha);
        // 三角形に対して、レイが平行に入射するような場合 det = 0 となる。
        // det が小さすぎると 1/det が大きくなりすぎて数値的に不安定になるので
        // det ≈ 0 の場合は交差しないこととする。
        if (Math.abs(det) < eps) {
            return false;
        }

        double invDet = 1.0 / det;
        double u = alpha.dot(r) * invDet;
        if (u < 0.0f || u > 1.0f) {
            return false;
        }

        Vector3 beta = r.cross(e1);
        double v = d.dot(beta) * invDet;
        if (v < 0.0f || u + v > 1.0f) {
            return false;
        }

        double t = e2.dot(beta) * invDet;
        if (t < 0.0f) {
            return false;
        }

        if (Double.isNaN(t)){
            //Log.e("StellarCoders",String.format("NaN appeared. det : %e",det));
            return false;
        }
        double targetNorm = new Vector3(p2,p1).norm();
        boolean retValue = !(t - targetNorm > eps);
        //Log.i("StellarCoders",String.format("Collision t:%.3f, u:%.3f, v:%.3f",t,u,v));
        //Log.i("StellarCoders",String.format("Start %s, Destination: %s. norm is %.3f, t - targetNorm %.3f, return value: %b",Utils.Point2str(p1),Utils.Point2str(p2),targetNorm,t - targetNorm,retValue));

        return retValue;
    }

    public static Mat calibratedNavCam(KiboRpcApi api){
        double[][] camStatistics = api.getNavCamIntrinsics();
        double[] camMtx = camStatistics[0];
        double[] dist = camStatistics[1];
        Mat navCam = api.getMatNavCam();
        Mat camMtxMat = new Mat(3,3, CvType.CV_64FC1);
        camMtxMat.put(0,0,camMtx);
        Mat distMat = new Mat(1,5,CvType.CV_64FC1);
        distMat.put(0,0,dist);

        Mat undistorted = new Mat();
        Imgproc.undistort(navCam,undistorted,camMtxMat,distMat);
        return undistorted;
    }


    public static ArrayList<Integer> searchMarker(Mat img){
        ArrayList<Integer> ret = new ArrayList<>();
        List<Mat> corners = new ArrayList<>();
        Mat markerIds = new Mat();
        Dictionary arucoDict = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
        Aruco.detectMarkers(img,arucoDict,corners,markerIds);
        for (int i = 0; i < markerIds.size(0); i++) {
            ret.add((int) markerIds.get(i,0)[0]);
        }
        return ret;
    }

    public static Mat drawMarker(KiboRpcApi api, Mat img){
        ArrayList<Integer> ret = new ArrayList<>();
        List<Mat> corners = new ArrayList<>();
        Mat markerIds = new Mat();
        Dictionary arucoDict = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
        Aruco.detectMarkers(img,arucoDict,corners,markerIds);
        Aruco.drawDetectedMarkers(img,corners);
        return img;
    }


    /**
     * Pに対してQを適応する
     * @param p
     * @param q
     * @return
     * https://qiita.com/drken/items/0639cf34cce14e8d58a5#1-4-%E3%82%AF%E3%82%A9%E3%83%BC%E3%82%BF%E3%83%8B%E3%82%AA%E3%83%B3%E3%81%AE%E3%81%8B%E3%81%91%E7%AE%97
     */
    public static Quaternion quaternionProd(Quaternion p,Quaternion q){
        return new Quaternion(
                q.getW() * p.getX() - q.getZ() * p.getY() + q.getY() * p.getZ() + q.getX() * p.getW(),
                q.getZ() * p.getX() + q.getW() * p.getY() - q.getX() * p.getZ() + q.getY() * p.getW(),
                -1 * q.getY() * p.getX() + q.getX() * p.getY() + q.getW() * p.getZ() + q.getZ() * p.getW(),
                -1 * q.getX() * p.getX() - q.getY() * p.getY() - q.getZ() * p.getZ() + q.getW() * p.getW()
                );
    }

    public static String Point2str(Point p){
        return String.format("Point [%.3f, %.3f, %.3f]", p.getX(),p.getY(),p.getZ());
    }
}
