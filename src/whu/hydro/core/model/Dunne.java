package whu.hydro.core.model;

import whu.hydro.core.Hydro;
import whu.hydro.core.HydroSeries;

/**
 * @ClassName Dunne 蓄满产流
 * @Description TODO
 * @Author 86187
 * @Date 2019/2/27 14:43
 * @Version 1.0
 */
public class Dunne {

    // 地形参数
    double B;
    // 流域最大蓄水量
    double WM;
    // 最大蓄水水位
    double WWMM;

    // 当前流域蓄水量
    double W;
    // 当前蓄水水位
    double A;

    // 产流
    double[] R;
    // 截留
    double[] P;


    double h;
    double alpha;
    double beta;
    Tank tank;

    public double getP(int i) {
        return P[i];
    }

    public double getR(int i) {
        return R[i];
    }


    public Dunne(double B, double WM, double W0, double h, double alpha, double beta) {
        this.B = B;
        this.WM = WM;
        this.WWMM = (1+B)*WM;
        this.W = W0;
        this.A = getAByW(W0);
        this.h = h;
        this.alpha = alpha;
        this.beta = beta;
    }

    private double getWByA(double A) {
        return WM*(1-Math.pow(1-A/WWMM, 1+B));
    }

    private double getAByW(double W) {
        return WWMM *(1-Math.pow(1-W/WM, 1/(1+B)));
    }

    public void run(HydroSeries hydroSeries) {
        Hydro[] hydros = hydroSeries.getHydros();
        init(hydros.length);

        for (int i = 1; i < hydros.length; i++) {
            runStep(hydros[i].P - hydros[i].E, i);
        }
    }

    public void init(int length) {
        R = new double[length];
        P = new double[length];
        tank = new Tank(new double[]{h}, new double[]{alpha}, beta, this.W, this.WWMM);
        tank.init(length);
    }

    public void runStep(double PE, int i) {

        if (PE + this.A >= WWMM) {
            P[i] = WM - W;
            R[i] = PE - P[i];
        } else {
            P[i] = getWByA(PE+A)-W;
            R[i] = PE - P[i];
        }
        tank.runStep(P[i], i);
        W = tank.getX(i);
        R[i] += tank.getY(i) + tank.getC(i);
        P[i] = tank.getZ(i);
//        System.out.println(W);
    }

    public static void main(String[] args) {
        double h = 4;
        double alpha = 0.01;
        double beta = 0.04;

        double B = 1.5;
        double WM = 10;
        double W0 = 6;

        Dunne dunne = new Dunne(B,WM, W0, h, alpha, beta);
        double[] Q = {5,7,8,4,2,9};
        double[] E = {2,3,4,4,1,5};
        HydroSeries hydroSeries = new HydroSeries(Q, E);

        dunne.run(hydroSeries);



    }
}
