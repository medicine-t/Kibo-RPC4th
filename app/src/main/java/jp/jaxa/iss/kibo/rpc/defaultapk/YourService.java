package jp.jaxa.iss.kibo.rpc.defaultapk;

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
        api.moveTo(new Point(10.5,-9.6,4.8 ),new Quaternion(0,0,0,1),true);
        ConstPoints pointData = new ConstPoints();
        ConstQuaternions quaternions = new ConstQuaternions();
        List<Integer> activeTargets = api.getActiveTargets();
        CheckPoints checkPoints = new CheckPoints();
        for (int i = 0; i < activeTargets.size(); i++) {
            activeTargets.set(i,activeTargets.get(i) - 1);
        }

        Dijkstra3D dijManager = new Dijkstra3D();
        Stack<PointI> move_oder = dijManager.dijkstra(checkPoints.Point2I(api.getRobotKinematics().getPosition()), checkPoints.Point2I(new Point(11.0,-9.5,5.0)));
        while(!move_oder.empty()){
            PointI p = move_oder.pop();
            Point to = checkPoints.idx2Point(p.getX(), p.getY(), p.getZ());
            Result result = api.moveTo(to, new Quaternion(0, 0, 0, 1),true);
        }
        //api.moveTo(pointData.points.get(0),quaternions.points.get(0),true);

        api.notifyGoingToGoal();
        api.moveTo(pointData.goal,quaternions.goal,true);
        api.reportMissionCompletion("hoge");
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

