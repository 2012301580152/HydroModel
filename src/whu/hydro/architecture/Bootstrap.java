package whu.hydro.architecture;

import whu.hydro.optimize.jgap.DownUp;
import whu.hydro.optimize.jgap.JGAP;
import whu.hydro.parameter.TankModelParameter;

/**
 * @ClassName Bootstrap
 * @Description TODO
 * @Author 86187
 * @Date 2019/2/28 14:05
 * @Version 1.0
 */
public class Bootstrap {
    public static void main(String[] args) {
        TankModelParameter t = new TankModelParameter();

        DownUp[] downUps = t.downUpsFactory();
        JGAP jgap = new JGAP(500, downUps);
        jgap.run();
    }
}
