package jp.jaxa.iss.kibo.rpc.defaultapk;

import android.util.Log;

import com.stellarcoders.Area;
import com.stellarcoders.CheckPoints;
import com.stellarcoders.ConstAreas;
import com.stellarcoders.ConstPoints;
import com.stellarcoders.ConstQuaternions;
import com.stellarcoders.utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import org.opencv.objdetect.QRCodeDetector;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {

    @Override
    protected void runPlan1(){
        // write your plan 1 here
        api.startMission();

        ConstPoints pointData = new ConstPoints();
        ConstQuaternions quaternions = new ConstQuaternions();

        api.moveTo(new Point(10.5,-10.0,4.5 ),new Quaternion(0,0,0,1),true);
        //this.moveDijkstra(api,new Point(10.5,-9.6,4.8 ),new Quaternion(0,0,0,1));
        Log.i("StellarCoders","Moved to Initial Point");

        // Go to read QR-Code
        Log.i("StellarCoders","Start Moving to QRCode position");
        moveDijkstra(pointData.QRCodePos,quaternions.QRCodePos);

        // READ QR DATA
        QRCodeDetector qrCodeDetector = new QRCodeDetector();
        api.flashlightControlFront(0.05f);
        String qrString = qrCodeDetector.detectAndDecode(api.getMatNavCam());

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

        // Move Around phase
        Map<Integer,Boolean> targetMapping = new HashMap<>();
        while(2 * 60 * 1000 + 30 * 1000 <= api.getTimeRemaining().get(1)) { //remain time is [ms]
            List<Integer> activeTargets = api.getActiveTargets();
            CheckPoints checkPoints = new CheckPoints();
            Point currentPos = api.getRobotKinematics().getPosition();
            for (int i = 0; i < activeTargets.size(); i++) {
                activeTargets.set(i, activeTargets.get(i) - 1);
                targetMapping.put(activeTargets.get(i),false);
            }

            Integer targetIndex = activeTargets.get(0);
            for(Map.Entry<Integer,Boolean> entry : targetMapping.entrySet()){
                if(!entry.getValue() && Utils.distance3DSquare(currentPos,pointData.points.get(targetIndex)) > Utils.distance3DSquare(currentPos,pointData.points.get(entry.getKey()))){
                    targetIndex = entry.getKey();
                }
            }


            Log.i("StellarCoders", String.format("Target : %d",targetIndex));
            //移動
            moveDijkstra(pointData.points.get(targetIndex), quaternions.points.get(targetIndex));

            /*
            * ここで角度の調整など
            * */
            Result laserResult = api.laserControl(true);
            if(laserResult != null && !laserResult.hasSucceeded())Log.i("StellarCoders",String.format("Laser toggle result : %s",laserResult.getMessage()));
            api.takeTargetSnapshot(targetIndex + 1);
            targetMapping.put(targetIndex,true);
            Log.i("StellarCoders", String.format("Remain Time is %s",api.getTimeRemaining().get(1).toString()));
        }


        // Go to Goal
        api.notifyGoingToGoal();
        Log.i("StellarCoders","Start Moving to Goal");
        moveDijkstra(pointData.goal,quaternions.goal);
        api.reportMissionCompletion(qrString);
    }

    void moveDijkstra( Point goal) {
        moveDijkstra(goal,new Quaternion(0,0,0,1));
    }
    void moveDijkstra(Point goal, Quaternion q) {
        Log.i("StellarCoders",String.format("Current Pos %s",this.api.getRobotKinematics().getPosition().toString()));
        CheckPoints checkPoints = new CheckPoints();
        Area[] KOZs = new ConstAreas().KOZs;
        Dijkstra3D dijManager = new Dijkstra3D();
        Stack<Node> move_oder = dijManager.dijkstra(checkPoints.Point2I(api.getRobotKinematics().getPosition()), checkPoints.Point2I(goal));
        while(!move_oder.empty()){
            Node n = move_oder.pop();
            PointI p = n.p;
            Point basePoint = api.getRobotKinematics().getPosition();//checkPoints.idx2Point(p);
            boolean can = true;
            int D_cnt = 0;
            while(!move_oder.empty() && can){
                Node tmp_n = move_oder.peek();
                Point destination = checkPoints.idx2Point(tmp_n.p);
                for(Area koz: KOZs){
                    for (Point[] ps: koz.getPolys()){
                        if(Utils.isPolyLineCollision(basePoint,destination,ps)){
                            can = false;
                            break;
                        }
                    }
                    if (!can)break;
                }
                if(can){
                    n = move_oder.pop();
                    D_cnt++;
                }else{
                    break;
                }
            }
            Point to = checkPoints.idx2Point(n.p.getX(), n.p.getY(), n.p.getZ());
            Log.i("StellarCoders", String.format("From: %s. Destination: %s. CombinedArea: %d",api.getRobotKinematics().getPosition().toString(),to.toString(),D_cnt));
            Result result = this.api.moveTo(to, q,true);
            if(result != null && !result.hasSucceeded()){
                Log.i("StellarCoders", result.getMessage());
            }
        }

        api.moveTo(goal,q,true); //厳密はポジションではないのでそこまで移動
        Log.i("StellarCoders","Moved to Point");
        Log.i("StellarCoders",String.format("Current Pos %s",this.api.getRobotKinematics().getPosition().toString()));
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

