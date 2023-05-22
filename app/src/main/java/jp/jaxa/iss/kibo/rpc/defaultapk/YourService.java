package jp.jaxa.iss.kibo.rpc.defaultapk;

import android.util.Log;

import com.stellarcoders.CheckPoints;
import com.stellarcoders.ConstPoints;
import com.stellarcoders.ConstQuaternions;

import java.util.List;
import java.util.Stack;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;
import com.stellarcoders.utils.*;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    @Override
    protected void runPlan1(){
        // write your plan 1 here
        api.startMission();



        api.moveTo(new Point(10.5,-10.0,4.5 ),new Quaternion(0,0,0,1),true);
        //this.moveDijkstra(api,new Point(10.5,-9.6,4.8 ),new Quaternion(0,0,0,1));
        Log.i("StellarCoders","Moved to Initial Point");

        ConstPoints pointData = new ConstPoints();
        ConstQuaternions quaternions = new ConstQuaternions();
        List<Integer> activeTargets = api.getActiveTargets();
        CheckPoints checkPoints = new CheckPoints();
        for (int i = 0; i < activeTargets.size(); i++) {
            activeTargets.set(i,activeTargets.get(i) - 1);
        }


        //new Point(11.0,-9.5,5.0)
        //api.moveTo(pointData.points.get(0),quaternions.points.get(0),true);
        //this.moveDijkstra(api,new Point(11.0,-9.5,5.0));
        Log.i("StellarCoders",String.format("Target : %d",activeTargets.get(0)));
        moveDijkstra(pointData.points.get(activeTargets.get(0)),quaternions.points.get(activeTargets.get(0)));
        api.laserControl(true);
        api.takeTargetSnapshot(activeTargets.get(0));


        api.notifyGoingToGoal();
        //api.moveTo(pointData.goal,quaternions.goal,true);
        moveDijkstra(pointData.goal,quaternions.goal);
        api.moveTo(pointData.start, quaternions.goal,true   );
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
            while(!move_oder.empty() && move_oder.firstElement().dir.equals(n.dir)){
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
    }

    @Override
    protected void runPlan3(){
        // write your plan 3 here
    }

}

