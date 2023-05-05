package jp.jaxa.iss.kibo.rpc.defaultapk;

import com.stellarcoders.ConstPoints;
import com.stellarcoders.ConstQuaternions;

import java.util.List;

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

        // move to initial position
        api.moveTo(new Point(10.5,-9.6,4.8 ),new Quaternion(0,0,0,1),true);
        ConstPoints pointData = new ConstPoints();
        ConstQuaternions quaternions = new ConstQuaternions();
        List<Integer> activeTargets = api.getActiveTargets();
        for (int i = 0; i < activeTargets.size(); i++) {
            activeTargets.set(i,activeTargets.get(i) - 1);
        }
        api.moveTo(pointData.points.get(0),quaternions.points.get(0),true);

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

