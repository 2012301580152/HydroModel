package whu.hydro.core.model;

import whu.hydro.base.io.In;
import whu.hydro.core.Hydro;
import whu.hydro.core.HydroSeries;

/**
 * @ClassName BootStrap
 * @Description TODO
 * @Author 86187
 * @Date 2019/2/27 21:35
 * @Version 1.0
 */
public class BootStrap {
    public static void main(String[] args) {

        In in = new In("data/data");
        String[] lines = in.readAllLines();

        double[] P = new double[lines.length];
        double[] Q = new double[lines.length];

        for (int i = 1; i < lines.length; i++) {
            String[] rows = lines[i].split("\t");
            P[i] = Double.valueOf(rows[0]);
            Q[i] = Double.valueOf(rows[1]);
        }

        HydroSeries hydroSeries = new HydroSeries(P);
        Hydro[] hydros = hydroSeries.getHydros();

        // 蓄满
        double h = 4;
        double alpha = 0.01;
        double beta = 0.04;
        double B = 1.5;
        double WM = 10;
        double W0 = 6;
        Dunne dunne = new Dunne(B,WM, W0, h, alpha, beta);
        dunne.init(hydros.length);
        // 一层水箱
        double[] h1 = new double[] {2,6,8};
        double[] alpha1 = {0.02,0.03,0.04};
        double beta1 = 0.04;
        double x01 = 8;
        double tankH1 = 10;
        Tank tank1 = new Tank(h1,alpha1,beta1,x01,tankH1);
        tank1.init(hydros.length);

        // 二层水箱
        double[] h2 = new double[] {2,8,15};
        double[] alpha2 = {0.02,0.036,0.028};
        double beta2 = 0.04;
        double x02 = 8;
        double tankH2 = 20;
        Tank tank2 = new Tank(h2,alpha2,beta2,x02,tankH2);
        tank2.init(hydros.length);
        // 三层水箱
        double[] h3 = new double[] {0,7,12};
        double[] alpha3 = {0.025,0.018,0.036};
        double beta3 = 0.0;
        double x03 = 8;
        double tankH3 = 30;
        Tank tank3 = new Tank(h3,alpha3,beta3,x03,tankH3);
        tank3.init(hydros.length);

        // 地表水箱
        double[] hb = new double[] {0,9,26};
        double[] alphab = {0.02,0.01,0.035};
        double betab = 0.0;
        double x0b = 8;
        double tankHb = 10;
        Tank tankb = new Tank(h3,alpha3,beta3,x03,tankH3);
        tankb.init(hydros.length);
        // 地下水箱
        double[] hx = new double[] {0,14,36};
        double[] alphax = {0.02,0.01,0.035};
        double betax = 0.0;
        double x0x = 8;
        double tankHx = 10;
        Tank tankx = new Tank(h3,alpha3,beta3,x03,tankH3);
        tankx.init(hydros.length);
        // 溢水无限水箱
        double[] hy = new double[] {2,4,6};
        double[] alphay = {0.02,0.01,0.03};
        double betay = 0.03;
        double x0y = 8;
        double tankHy = Double.MAX_VALUE;
        Tank tanky = new Tank(h3,alpha3,beta3,x03,tankH3);
        tanky.init(hydros.length);


        double[] Qout = new double[hydros.length];

        for (int i = 1; i < hydros.length; i++) {
            dunne.runStep(hydros[i].P - hydros[i].E, i);
            tank1.runStep(dunne.getP(i), i);
            tank2.runStep(tank1.getZ(i), i);
            tank3.runStep(tank2.getZ(i), i);

            tankb.runStep(dunne.getR(i), i);
            System.out.println(i);

            tankx.runStep(tank1.getY(i)+tank2.getY(i)+tank3.getY(i), i);

            tanky.runStep(tank1.getC(i)+tank2.getC(i)+tank3.getC(i)+tankb.getC(i)+tankx.getC(i), i);

            Qout[i] = tankb.getY(i) + tankx.getY(i) + tanky.getY(i);

        }

        System.out.println("==============================");

        for (int i = 0; i < Qout.length; i++) {
            System.out.println(Qout[i]+"\t"+Q[i]);
        }










    }
}
