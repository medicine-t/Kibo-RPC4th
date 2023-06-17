package jp.jaxa.iss.kibo.rpc.defaultapk;

import android.annotation.SuppressLint;
import android.util.Log;

import com.stellarcoders.Area;
import com.stellarcoders.CheckPoints;
import com.stellarcoders.ConstAreas;
import com.stellarcoders.ConstPoints;
import com.stellarcoders.ConstQuaternions;
import com.stellarcoders.utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import gov.nasa.arc.astrobee.Kinematics;
import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import gov.nasa.arc.astrobee.types.Vec3d;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import org.opencv.core.Mat;
import org.opencv.objdetect.QRCodeDetector;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {

    @SuppressLint("DefaultLocale")
    @Override
    protected void runPlan1(){
        // write your plan 1 here
        api.startMission();

        ConstPoints pointData = new ConstPoints();
        ConstQuaternions quaternions = new ConstQuaternions();

        //api.moveTo(new Point(10.5,-10.0,4.5 ),new Quaternion(0,0,0,1),true);
        //this.moveDijkstra(api,new Point(10.5,-9.6,4.8 ),new Quaternion(0,0,0,1));
        //Log.i("StellarCoders","Moved to Initial Point");

        String qrString = "";

        // Move Around phase
        Map<Integer,Boolean> targetMapping = new HashMap<>();
        // QRCode
        final int QRCODE_POSITION_TARGET = 6;
        targetMapping.put(QRCODE_POSITION_TARGET,false);
        while(5 * 60 * 1000 - (3 * 60 * 1000 + 0 * 30 * 1000) <= api.getTimeRemaining().get(1)) { //remain time is [ms]
            List<Integer> activeTargets = api.getActiveTargets();
            Point currentPos = api.getRobotKinematics().getPosition();
            Log.i("StellarCoders",String.format("Remain Targets : %s",activeTargets.toString()));
            for (int i = 0; i < activeTargets.size(); i++) {
                activeTargets.set(i, activeTargets.get(i) - 1);
                targetMapping.put(activeTargets.get(i),false);
            }

            Integer targetIndex = activeTargets.get(0);
            for(Map.Entry<Integer,Boolean> entry : targetMapping.entrySet()){
                ArrayList<Point> move_oder= (new Dijkstra3D()).dijkstra(api.getRobotKinematics().getPosition(),pointData.points.get(entry.getKey()));
                move_oder = concatPath(move_oder);
                double estimateMovingTime = Utils.calcMovingTime(move_oder);
                Log.i("StellarCoders",String.format("TargetSearch: target[%d] ,estimate %.2f",entry.getKey(),estimateMovingTime));
                boolean canAchieve = estimateMovingTime + 30 * 1000 <= api.getTimeRemaining().get(0);
                if(entry.getKey() == QRCODE_POSITION_TARGET)canAchieve = true;
                if(!entry.getValue() && canAchieve && Utils.distance3DSquare(currentPos,pointData.points.get(targetIndex)) > Utils.distance3DSquare(currentPos,pointData.points.get(entry.getKey()))){
                    targetIndex = entry.getKey();
                }
            }


            Log.i("StellarCoders", String.format("Target : %d",targetIndex));
            Log.i("StellarCoders",String.format("Target Position : %s",pointData.points.get(targetIndex).toString()));
            //移動
            Point correctedTarget = Utils.applyPoint(pointData.points.get(targetIndex),Utils.target2transpose(targetIndex,new Vector3(-0.07,0.12,0)));
            int result = moveDijkstra(correctedTarget, quaternions.points.get(targetIndex),targetIndex);
            if(result == -1){
                continue;
            }
            Log.i("StellarCoders", String.format("Move finished. Current : %s",api.getRobotKinematics().getPosition().toString()));
            Log.i("StellarCoders", String.format("Target Position was : %s",pointData.points.get(targetIndex).toString()));

            /*
            * ここで角度の調整など
            * */
            if(targetIndex == QRCODE_POSITION_TARGET){
                qrString = readQRCode(api);
                targetMapping.put(targetIndex,true);
                continue;
            }
            try{
                api.laserControl(true);
                //api.saveMatImage(Utils.drawMarker(api,Utils.calibratedNavCam(api)),String.format("Detected_Markers_%s.png",api.getTimeRemaining().get(1).toString()));
                api.saveMatImage(Utils.drawMarkerPoseEstimation(api),String.format("Detected_Markers_Position_%s.png", api.getTimeRemaining().get(1).toString()));
                for (int cnt = 0; cnt < 1; cnt++) {
                    if(api.getTimeRemaining().get(0) <= 1000 * 15)break;
                    Log.i("StellarCoders",String.format("Detected Markers: %s",Utils.searchMarker(Utils.calibratedNavCam(api))));

                    Vector3 rel = Utils.getDiffFromCam(api,targetIndex);
                    Point currentPosition = api.getRobotKinematics().getPosition();
                    Log.e("StellarCoders",String.format("relative %.3f, %.3f, %.3f",rel.getX(),rel.getY(),rel.getZ()));
                    if(rel.getX() * rel.getX() + rel.getY() * rel.getY() + rel.getZ() * rel.getZ() <= 0.01){
                        continue;
                    }
                    //rel = rel.add(new Vector3(currentPosition.getX(),currentPosition.getY(),currentPosition.getZ()));
                    api.relativeMoveTo(new Point(rel.getX(),rel.getY(),rel.getZ()),quaternions.points.get(targetIndex),true);

                    //api.saveMatImage(Utils.drawMarker(api,Utils.calibratedNavCam(api)),String.format("Detected_Markers_%s.png",api.getTimeRemaining().get(1).toString()));
                    api.saveMatImage(Utils.drawMarkerPoseEstimation(api),String.format("Detected_Markers_Position_%s.png",api.getTimeRemaining().get(1).toString()));
                }
                api.laserControl(false);
            } catch (Error e){
                Log.e("StellarCoders",e.getMessage());
            }

            //
            Result laserResult = api.laserControl(true);
            if(laserResult != null && !laserResult.hasSucceeded())Log.i("StellarCoders",String.format("Laser toggle result : %s",laserResult.getMessage()));
            api.saveMatImage(Utils.drawMarkerPoseEstimation(api),String.format("Snapshot_%d_%s.png",targetIndex + 1,api.getTimeRemaining().get(1).toString()));
            api.takeTargetSnapshot(targetIndex + 1);
            targetMapping.put(targetIndex,true);
            Log.i("StellarCoders", String.format("Remain Time is %s",api.getTimeRemaining().get(1).toString()));
        }

        // Go to Goal
        if(!targetMapping.get(QRCODE_POSITION_TARGET)){
            moveDijkstra(pointData.QRCodePos,quaternions.QRCodePos);
            readQRCode(api);
        }
        api.notifyGoingToGoal();
        Log.i("StellarCoders","Start Moving to Goal");
        moveDijkstra(pointData.goal,quaternions.goal);
        api.reportMissionCompletion(qrString);
    }

    void moveDijkstra(Point goal,Quaternion q){
        moveDijkstra(goal,q,-1);
    }

    void updateTargetInfo(){

    }
    String readQRCode(KiboRpcApi api){
        // READ QR DATA
        QRCodeDetector qrCodeDetector = new QRCodeDetector();
        api.flashlightControlFront(0.3f);
        String qrString = "";
        Mat img = Utils.calibratedNavCam(api);
        try {
            for (int i = 0; i < 4; i++) {
                qrString = qrCodeDetector.detectAndDecode(img);
                if(!qrString.equals("")) {
                    break;
                }else{
                    img = Utils.rotateImg(img);
                }
            }
            api.saveMatImage(Utils.calibratedNavCam(api),"CalibratedQrImage.png");
        }catch (Error e){
            Log.e("StellarCoders",e.getMessage());
        }


        Map<String,String> qrMap = new HashMap<String, String>() {{
            put("JEM", "STAY_AT_JEM");
            put("COLUMBUS", "GO_TO_COLUMBUS");
            put("RACK1", "CHECK_RACK_1");
            put("ASTROBEE", "I_AM_HERE");
            put("INTBALL","LOOKING_FORWARD_TO_SEE_YOU");
            put("BLANK","NO_PROBLEM");
        }};
        Log.i("StellarCoders",String.format("Read QR raw String is: %s",qrString));
        qrString = qrMap.getOrDefault(qrString,"FAILED TO GET");
        //
        Log.i("StellarCoders",String.format("Read QR String is: %s",qrString));
        Log.i("StellarCoders", String.format("Remain Time is %s",api.getTimeRemaining().get(1).toString()));
        return qrString;
    }

    ArrayList<Point> concatPath(ArrayList<Point> move_oder){
        ArrayList<Point> concatenated = new ArrayList<>();
        Area[] KOZs = new ConstAreas().KOZs;
        Point from = api.getRobotKinematics().getPosition();
        Log.i("StellarCoders",String.format("move_oder: %s",move_oder.toString()));
        for(int idx = 0;idx < move_oder.size();idx++){
            Point basePoint = new Point(from.getX(), from.getY(),from.getZ());
            int max_idx = idx;
            Point validDestination = new Point(basePoint.getX(),basePoint.getY(),basePoint.getZ());
            for(int next_idx = idx;next_idx < move_oder.size();next_idx++){
                boolean can = true;
                Point destination = new Point(move_oder.get(next_idx).getX(),move_oder.get(next_idx).getY(),move_oder.get(next_idx).getZ());
                for(Area koz: KOZs){
                    for (Point[] ps: koz.getPolys()){
                        boolean collisionCheckRet = Utils.isPolyLineCollision(basePoint,destination,ps);
                        if(collisionCheckRet) {
                            can = false;
                        }
                    }
                }
                if(can){
                    if(max_idx < next_idx) {
                        max_idx = next_idx;
                        validDestination = new Point(destination.getX(),destination.getY(),destination.getZ());
                        Log.i("StellarCoders",String.format("Valid Route Updated! FROM: %s, TO: %s",basePoint,destination));

                    }
                }
            }
            idx = max_idx;
            Point to = move_oder.get(idx);
            concatenated.add(to);
            from = new Point(to.getX(),to.getY(),to.getZ());

        }

        return  concatenated;
    }

    int moveDijkstra(Point goal, Quaternion q, int targetIndex) {
        Kinematics kine = api.getRobotKinematics();
        Point currentPos = kine.getPosition();
        Log.i("StellarCoders",String.format("Current Pos %s",kine.getPosition().toString()));
        CheckPoints checkPoints = new CheckPoints();
        Dijkstra3D dijManager = new Dijkstra3D();
        ArrayList<Point> move_oder = dijManager.dijkstra(api.getRobotKinematics().getPosition(),goal);

        ArrayList<Point> concatenated = concatPath(move_oder);
        double totalDistance = 0.0;
        for (int i = 0; i < concatenated.size() - 1; i++) {
            totalDistance += Utils.distance3DSquare(concatenated.get(i),concatenated.get(i + 1));
        }
        Log.i("StellarCoders",String.format("Concatenated: %s",concatenated.toString()));
        double dist = 0.0;
        for (int i = 0; i < concatenated.size(); i++) {
            try{
                dist += Utils.distance3DSquare(currentPos,concatenated.get(i));
                api.moveTo(concatenated.get(i),Utils.QuatSlerp(kine.getOrientation(),q,Math.min(1,dist / totalDistance)), true);
                currentPos = concatenated.get(i);
            }catch (Error e){
                Log.e("StellarCoders",e.getMessage());
                return -1;
            }
        }

        Log.i("StellarCoders","Moved to Point");
        Log.i("StellarCoders",String.format("Current Pos %s",this.api.getRobotKinematics().getPosition().toString()));
        return 0;
    }

    @Override
    protected void runPlan2(){
        // write your plan 2 here
        this.runPlan1();
    }

    @Override
    protected void runPlan3(){
        // write your plan 3 here
        this.runPlan1();
    }

}

