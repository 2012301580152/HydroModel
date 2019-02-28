package whu.hydro.optimize.jgap;



import whu.hydro.architecture.TankModel;
import whu.hydro.parameter.TankModelParameter;

import java.util.concurrent.CountDownLatch;

/**
 * @ClassName Top
 * @Description TODO
 * @Author Gavin
 * @Date 2018/11/27 17:09
 * @Version 1.0
 */
public class Top  implements Runnable{

    private Thread t;
    private String threadName;
    private Ga ga;
    private final CountDownLatch latch;
    TankModel tankModel;

    public Top(Ga ga, String threadName, final CountDownLatch latch) {
        this.ga = ga;
        this.threadName = threadName;
        this.latch = latch;
        this.tankModel = new TankModel("data/data", "data/output");
    }

    public void start () {
        //System.out.println("Starting " +  threadName );
//        if (t == null) {
//            t = new Thread (this, threadName);
//            t.start ();
//        }
        run();
    }

    @Override
    public void run() {
        TankModelParameter t = new TankModelParameter(ga.getParameters());
        this.tankModel.run(t);

        double dc = tankModel.getDc();
//        System.out.println(dc);
        ga.setDc(dc);


        latch.countDown();




    }
}
