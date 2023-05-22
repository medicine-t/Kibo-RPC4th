package jp.jaxa.iss.kibo.rpc.defaultapk;

import android.util.Log;

import com.stellarcoders.CheckPoints;
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

        // Move Around phase
        Map<Integer,Boolean> targetMapping = new HashMap<>();
        while(2 * 60 * 1000 <= api.getTimeRemaining().get(1)) { //remain time is [ms]
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
            moveDijkstra(pointData.points.get(targetIndex), quaternions.points.get(targetIndex));
            api.laserControl(true);
            api.takeTargetSnapshot(targetIndex + 1);
            targetMapping.put(targetIndex,true);
            Log.i("StellarCoders", String.format("Remain Time is %s",api.getTimeRemaining().get(1).toString()));
        }


        // Go to read QR-Code
        Log.i("StellarCoders","Start Moving to QRCode position");
        moveDijkstra(pointData.QRCodePos,quaternions.QRCodePos);


        // READ QR DATA
        //

        // Go to Goal
        api.notifyGoingToGoal();
        Log.i("StellarCoders","Start Moving to Goal");
        moveDijkstra(pointData.goal,quaternions.goal);
        api.reportMissionCompletion("hoge");
    }

    void moveDijkstra( Point goal) {
        moveDijkstra(goal,new Quaternion(0,0,0,1));
    }
    void moveDijkstra(Point goal, Quaternion q) {
        Log.i("StellarCoders",String.format("Current Pos %s",this.api.getRobotKinematics().getPosition().toString()));
        CheckPoints checkPoints = new CheckPoints();
        Dijkstra3D dijManager = new Dijkstra3D();
        Stack<Node> move_oder = dijManager.dijkstra(checkPoints.Point2I(api.getRobotKinematics().getPosition()), checkPoints.Point2I(goal));
        while(!move_oder.empty()){
            Node n = move_oder.pop();
            PointI p = n.p;
            while(!move_oder.empty() && move_oder.peek().dir.equals(n.dir)){
                n = move_oder.pop();
            }
            Point to = checkPoints.idx2Point(p.getX(), p.getY(), p.getZ());
            Log.i("StellarCoders", String.format("From: %s. Destination: %s. Direction: %d",api.getRobotKinematics().getPosition().toString(),to.toString(),n.dir));
            Result result = this.api.moveTo(to, q,true);
            if(!result.hasSucceeded()){
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

