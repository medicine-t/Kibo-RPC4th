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

        // move to initial position
        //api.moveTo(new Point(10.5,-9.6,4.8 ),new Quaternion(0,0,0,1),true);
        this.moveDijkstra(new Point(10.5,-9.6,4.8 ),new Quaternion(0,0,0,1));
        ConstPoints pointData = new ConstPoints();
        ConstQuaternions quaternions = new ConstQuaternions();
        List<Integer> activeTargets = api.getActiveTargets();
        CheckPoints checkPoints = new CheckPoints();
        for (int i = 0; i < activeTargets.size(); i++) {
            activeTargets.set(i,activeTargets.get(i) - 1);
        }


        //new Point(11.0,-9.5,5.0)
        //api.moveTo(pointData.points.get(0),quaternions.points.get(0),true);
        this.moveDijkstra(new Point(11.0,-9.5,5.0));

        api.notifyGoingToGoal();
        //api.moveTo(pointData.goal,quaternions.goal,true);
        this.moveDijkstra(pointData.goal,quaternions.goal);
        api.reportMissionCompletion("hoge");
    }

    void moveDijkstra(Point goal) {
        CheckPoints checkPoints = new CheckPoints();
        Dijkstra3D dijManager = new Dijkstra3D();
        Stack<PointI> move_oder = dijManager.dijkstra(checkPoints.Point2I(api.getRobotKinematics().getPosition()), checkPoints.Point2I(goal));
        while(!move_oder.empty()){
            PointI p = move_oder.pop();
            Point to = checkPoints.idx2Point(p.getX(), p.getY(), p.getZ());
            Result result = api.moveTo(to, new Quaternion(0, 0, 0, 1),true);
        }

        Log.i("StellarCoders",String.format("Moved to Point is %.3f,%3.f,%.3f",goal.getX(),goal.getY(),goal.getZ()));
    }
    void moveDijkstra(Point goal, Quaternion q) {
        CheckPoints checkPoints = new CheckPoints();
        Dijkstra3D dijManager = new Dijkstra3D();
        Stack<PointI> move_oder = dijManager.dijkstra(checkPoints.Point2I(api.getRobotKinematics().getPosition()), checkPoints.Point2I(goal));
        while(!move_oder.empty()){
            PointI p = move_oder.pop();
            Point to = checkPoints.idx2Point(p.getX(), p.getY(), p.getZ());
            Result result = api.moveTo(to, q,true);
        }
        Log.i("StellarCoders",String.format("Moved to Point is %.3f,%3.f,%.3f",goal.getX(),goal.getY(),goal.getZ()));
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

