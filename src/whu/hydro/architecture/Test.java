package whu.hydro.architecture;

import whu.hydro.optimize.jgap.DownUp;
import whu.hydro.optimize.jgap.JGAP;
import whu.hydro.parameter.TankModelParameter;

/**
 * @ClassName Test
 * @Description TODO
 * @Author 86187
 * @Date 2019/2/28 14:48
 * @Version 1.0
 */
public class Test {
    public static void main(String[] args) {
        TankModelParameter t = new TankModelParameter();

        TankModel tankModel = new TankModel("data/data", "data/output");
        tankModel.run(t);
        double[] Q = tankModel.getQ();
        System.out.println(tankModel.getDc());

//        for (int i = 0; i < Q.length; i++) {
//            System.out.println(Q[i]);
//        }

    }
}
