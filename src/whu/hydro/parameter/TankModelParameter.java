package whu.hydro.parameter;

import whu.hydro.optimize.jgap.DownUp;
import whu.hydro.optimize.jgap.Parameter;

/**
 * @ClassName TankModelParameter
 * @Description TODO
 * @Author 86187
 * @Date 2019/2/28 11:23
 * @Version 1.0
 */
public class TankModelParameter {

    private DownUp[] downUps;
//    public final int nums = 16+12;
    // 蓄满
    public double h;
    public DownUp alpha = new DownUp(0.0,0.1,0.01);
    public DownUp beta = new DownUp(0.0,0.1,0.04);
    public DownUp B = new DownUp(0.5,5,1.5);
    public DownUp WM = new DownUp(5,40,10);
    //public DownUp W0 = new DownUp(3,30,6);

//    Dunne dunne = new Dunne(B,WM, W0, h, alpha, beta);
//        dunne.init(hydros.length);
    // 一层水箱
    public double[] h1;
//    {2,6,8};
    public DownUp[] alpha1;
//    {0.02,0.03,0.04};
    public DownUp beta1 = new DownUp(0.0,0.1,0.04);
    //public DownUp x01 = new DownUp(0.0,0.1,8);
    public DownUp tankH1 = new DownUp(5,50,10);
//    Tank tank1 = new Tank(h1,alpha1,beta1,x01,tankH1);
//        tank1.init(hydros.length);

    // 二层水箱
    public double[] h2;
    public DownUp[] alpha2;
    public DownUp beta2 = new DownUp(0.0,0.1,0.04);
//    public double x02 = 8;
    public DownUp tankH2 = new DownUp(5,50,20);
//    Tank tank2 = new Tank(h2,alpha2,beta2,x02,tankH2);
//        tank2.init(hydros.length);
    // 三层水箱
    public double[] h3;
    public DownUp[] alpha3;
//            {0.025,0.018,0.036};
    public double beta3 = 0.0;
//    public double x03 = 8;
    public DownUp tankH3 = new DownUp(5,50,30);
//    Tank tank3 = new Tank(h3,alpha3,beta3,x03,tankH3);
//        tank3.init(hydros.length);

    // 地表水箱
    public double[] hb;
    public DownUp[] alphab;
//    {0.02,0.01,0.035};
    public double betab = 0.0;
//    public double x0b = 8;
    public DownUp tankHb = new DownUp(5,50,10);
//    Tank tankb = new Tank(h3,alpha3,beta3,x03,tankH3);
//        tankb.init(hydros.length);
    // 地下水箱
    public double[] hx;
    public DownUp[] alphax;
//    {0.02,0.01,0.035};
    public double betax = 0.0;
//    public double x0x = 8;
    public DownUp tankHx = new DownUp(5,50,10);
//    Tank tankx = new Tank(h3,alpha3,beta3,x03,tankH3);
//        tankx.init(hydros.length);
    // 溢水无限水箱
    public double[] hy;
    public DownUp[] alphay;
//    {0.02,0.01,0.03};
    public DownUp betay = new DownUp(0.0,0.1,0.03);
//    public double x0y = 8;
    public double tankHy = Double.MAX_VALUE;
//    Tank tanky = new Tank(h3,alpha3,beta3,x03,tankH3);
//        tanky.init(hydros.length);

    public DownUp k = new DownUp(0.000001,10,1);


    private void setAlpha(DownUp[] alpha) {
        for (int i = 0; i < alpha.length; i++) {
            alpha[i] = new DownUp(0.00001,0.1,0.03);
        }
    }

    public DownUp[] downUpsFactory () {
        return downUps;
    }

    public TankModelParameter(Parameter[] p) {
        init();
        alpha.s = p[0].getRealValue();
        beta.s = p[1].getRealValue();
        B.s = p[2].getRealValue();
        WM.s = p[3].getRealValue();

        beta1.s = p[4].getRealValue();
        tankH1.s = p[5].getRealValue();

        beta2.s = p[6].getRealValue();
        tankH2.s = p[7].getRealValue();

        tankH3.s = p[8].getRealValue();

        tankHb.s = p[9].getRealValue();

        tankHx.s = p[10].getRealValue();

        betay.s = p[11].getRealValue();


        alpha1[0].s = p[12].getRealValue();
        alpha1[1].s = p[13].getRealValue();

        alpha2[0].s = p[14].getRealValue();
        alpha2[1].s = p[15].getRealValue();
        alpha2[2].s = p[16].getRealValue();

        alpha3[0].s = p[17].getRealValue();
        alpha3[1].s = p[18].getRealValue();

        alphab[0].s = p[19].getRealValue();
        alphab[1].s = p[20].getRealValue();
        alphab[2].s = p[21].getRealValue();


        alphax[0].s = p[22].getRealValue();
        alphax[1].s = p[23].getRealValue();
        alphax[2].s = p[24].getRealValue();


        alphay[0].s = p[25].getRealValue();
        alphay[1].s = p[26].getRealValue();
        alphay[2].s = p[27].getRealValue();

        k.s = p[28].getRealValue();


    }

    private void init() {
        this.h = 0.4;
        this.h1 = new double[]{0.2,0.6};
        this.alpha1 = new DownUp[h1.length];
        double maxK = 0.1;
        setAlpha(this.alpha1);

        // 二层水箱
        this.h2 = new double[]{0.2,0.4,0.8};
        this.alpha2 = new DownUp[h2.length];
        setAlpha(this.alpha2);

        // 三层水箱
        this.h3 = new double[]{0.2,0.6};
        this.alpha3 = new DownUp[h3.length];
        setAlpha(this.alpha3);

        // 地表水箱
        this.hb = new double[] {0.2,0.5,0.8};
        this.alphab = new DownUp[hb.length];
        setAlpha(this.alphab);


        // 地下水箱
        this.hx = new double[] {0.1,0.4,0.7};
        this.alphax = new DownUp[hx.length];
        setAlpha(this.alphax);

        // 溢水无限水箱
        this.hy = new double[] {0.2,0.4,0.6};
        this.alphay = new DownUp[hy.length];
        setAlpha(this.alphay);

        downUps = new DownUp[16+12+1];



        downUps[0] = alpha;
        downUps[1] = beta;
        downUps[2] = B;
        downUps[3] = WM;

        downUps[4] = beta1;
        downUps[5] = tankH1;

        downUps[6] = beta2;
        downUps[7] = tankH2;

        downUps[8] = tankH3;

        downUps[9] = tankHb;

        downUps[10] = tankHx;

        downUps[11] = betay;


        downUps[12] = alpha1[0];
        downUps[13] = alpha1[1];

        downUps[14] = alpha2[0];
        downUps[15] = alpha2[1];
        downUps[16] = alpha2[2];

        downUps[17] = alpha3[0];
        downUps[18] = alpha3[1];

        downUps[19] = alphab[0];
        downUps[20] = alphab[1];
        downUps[21] = alphab[2];

        downUps[22] = alphax[0];
        downUps[23] = alphax[1];
        downUps[24] = alphax[2];

        downUps[25] = alphay[0];
        downUps[26] = alphay[1];
        downUps[27] = alphay[2];
        downUps[28] = k;
    }

    public TankModelParameter() {
        init();
    }
}
