package whu.hydro.architecture;

import whu.hydro.base.io.In;
import whu.hydro.core.Hydro;
import whu.hydro.core.HydroSeries;
import whu.hydro.core.model.Dunne;
import whu.hydro.core.model.Tank;
import whu.hydro.optimize.jgap.DownUp;
import whu.hydro.optimize.jgap.JGAP;
import whu.hydro.parameter.TankModelParameter;

/**
 * @ClassName TankModel
 * @Description TODO
 * @Author 86187
 * @Date 2019/2/28 11:19
 * @Version 1.0
 */
public class TankModel {
    private final String inputFilePath;
    private final String outputFilePath;

    private HydroSeries hydroSeries;
    private double[] Q;

    private double dc;

    public double[] getQ() {
        return Q;
    }

    public double getDc() {
        return dc;
    }

    // "data/data"
    public TankModel(String inputFilePath, String outputFilePath) {
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;

        In in = new In(inputFilePath);
        String[] lines = in.readAllLines();

        double[] P = new double[lines.length];
        this.Q = new double[lines.length];

        for (int i = 1; i < lines.length; i++) {
            String[] rows = lines[i].split("\t");
            P[i] = Double.valueOf(rows[0]);
            Q[i] = Double.valueOf(rows[1]);
        }

        this.hydroSeries = new HydroSeries(P);
    }

    public void run(TankModelParameter t) {

        Hydro[] hydros = hydroSeries.getHydros();

        // 蓄满
        Dunne dunne = new Dunne(t.B.s,t.WM.s, 0.5*t.WM.s, t.h*t.WM.s, t.alpha.s, t.beta.s);
        dunne.init(hydros.length);
        // 一层水箱
        double[] h = new double[t.h1.length];
        double[] alpha = new double[t.h1.length];
        double x0 = 0.5*t.tankH1.s;
        for (int i = 0; i < t.h1.length; i++) {
            h[i] = t.h1[i] * t.tankH1.s;
            alpha[i] = t.alpha1[i].s;
        }
        Tank tank1 = new Tank(h,alpha,t.beta1.s,x0,t.tankH1.s);
        tank1.init(hydros.length);

        // 二层水箱
        h = new double[t.h2.length];
        alpha = new double[t.h2.length];
        x0 = 0.5*t.tankH2.s;
        for (int i = 0; i < t.h2.length; i++) {
            h[i] = t.h2[i] * t.tankH2.s;
            alpha[i] = t.alpha2[i].s;
        }
        Tank tank2 = new Tank(h,alpha,t.beta2.s,x0,t.tankH2.s);
        tank2.init(hydros.length);

        h = new double[t.h3.length];
        alpha = new double[t.h3.length];
        x0 = 0.5*t.tankH3.s;
        for (int i = 0; i < t.h3.length; i++) {
            h[i] = t.h3[i] * t.tankH3.s;
            alpha[i] = t.alpha3[i].s;
        }
        // 三层水箱
        Tank tank3 = new Tank(h,alpha,t.beta3,x0,t.tankH3.s);
        tank3.init(hydros.length);

        h = new double[t.hb.length];
        alpha = new double[t.hb.length];
        x0 = 0.5*t.tankHb.s;
        for (int i = 0; i < t.hb.length; i++) {
            h[i] = t.hb[i] * t.tankHb.s;
            alpha[i] = t.alphab[i].s;
        }
        // 地表水箱
        Tank tankb = new Tank(h,alpha,t.betab,x0,t.tankHb.s);
        tankb.init(hydros.length);

        h = new double[t.hx.length];
        alpha = new double[t.hx.length];
        x0 = 0.5*t.tankHx.s;
        for (int i = 0; i < t.hx.length; i++) {
            h[i] = t.hx[i] * t.tankHb.s;
            alpha[i] = t.alphax[i].s;
        }
        // 地下水箱
        Tank tankx = new Tank(h,alpha,t.betax,x0,t.tankHx.s);
        tankx.init(hydros.length);

        h = new double[t.hy.length];
        alpha = new double[t.hy.length];
        x0 = 0.5*10;
        for (int i = 0; i < t.hy.length; i++) {
            h[i] = t.hy[i] * 10;
            alpha[i] = t.alphay[i].s;
        }
        // 溢水无限水箱
        Tank tanky = new Tank(h,alpha,t.betay.s,x0,t.tankHy);
        tanky.init(hydros.length);


        double[] Qout = new double[hydros.length];

        for (int i = 1; i < hydros.length; i++) {
            dunne.runStep(hydros[i].P - hydros[i].E, i);
            tank1.runStep(dunne.getP(i), i);
            tank2.runStep(tank1.getZ(i), i);
            tank3.runStep(tank2.getZ(i), i);

            tankb.runStep(dunne.getR(i), i);
//            System.out.println(i);

            tankx.runStep(tank1.getY(i)+tank2.getY(i)+tank3.getY(i), i);

            tanky.runStep(tank1.getC(i)+tank2.getC(i)+tank3.getC(i)+tankb.getC(i)+tankx.getC(i), i);

            Qout[i] = (tankb.getY(i) + tankx.getY(i) + tanky.getY(i))*t.k.s;

        }

        this.dc = nash(Qout, this.Q);

//        System.out.println("==============================");
//
//        for (int i = 0; i < Qout.length; i++) {
//            System.out.println(Qout[i]+"\t"+Q[i]);
//        }
    }

    /**
     * 纳什效率系数计算
     */
    private double nash(double[] Qobs, double[] Qsim) {

        double meanF = 0.0;

        for (int i = 0; i < this.Q.length; i++) {

            meanF = meanF + Qobs[i];
        }
        meanF = meanF / this.Q.length;

        double VsimCumu = 0.0;
        double VobsCumu = 0.0;
        for (int i = 0; i < this.Q.length; i++) {
            VsimCumu = VsimCumu + (Qsim[i] - Qobs[i]) * (Qsim[i] - Qobs[i]);
            VobsCumu = VobsCumu + (Qobs[i] - meanF) * (Qobs[i] - meanF);
        }

		/*for (int i = 0; i < this.N; i++) {
			meanF += Math.abs(Qsim[i] - Qobs[i]) / Qobs[i];
		}*/

        return 1 - VsimCumu / VobsCumu;
        //return 1.0 / meanF;
    }



    public static void main(String[] args) {




    }
}
