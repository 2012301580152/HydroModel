package whu.hydro.core.model;

import whu.hydro.core.DefautParameter;
import whu.hydro.core.Hydro;
import whu.hydro.core.HydroSeries;
import whu.hydro.core.exception.TankException;

import java.awt.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @ClassName Tank
 * @Description TODO
 * @Author 86187
 * @Date 2019/2/20 21:38
 * @Version 1.0
 */
public class Tank {

    private final double tankH; // 水箱高度
    // 边孔高度h
    private final double[] h; // 其中 h[0] 为底孔高度，即h[0]=0
    // 边孔出流系数
    private final double[] alpha; // 其中 alpha[0] 为底孔出流系数
    // 底孔出流系数
//    private final double beta;

    // 初始蓄水深度
    private final double x0;

    // 蓄水深度
    private double[] x;
    // 时段径流
    private double[] y;
    // 时段下渗量
    private double[] z;
    // 溢出流量
    private double[] c;

    private class Cache {

        public double t;
        public int n;
        double h;
        Cache(double t, int n, double h) {
            this.t = t;
            this.n = n;
            this.h = h;
        }
    }

    public double getX(int i) {
        return x[i];
    }

    public double getY(int i) {
        return y[i];
    }

    public double getZ(int i) {
        return z[i];
    }

    public double getC(int i) {
        return c[i];
    }

    // 时间间隔， 边孔个数
    private Queue<Cache> cache = new LinkedBlockingQueue<>();

    //
    /**
    * @Description: 构造函数
    * @Param: [h：侧边孔高度, alpha：侧边孔出流系数, beta：底孔出流系数, x0：初始水位, tankH：水箱高度]
    * 默认h已经排序，由小到大
    * @return:
    * @Author: gavin
    * @Date: 2019/2/27
    */
    public Tank(double[] h, double[] alpha, double beta, double x0, double tankH) {
        this.h = new double[h.length + 1];
        this.alpha = new double[h.length + 1];
        this.h[0] = 0;
        this.alpha[0] = beta;

        for (int i = 1; i < h.length + 1; i++) {
            this.h[i] = h[i-1];
            this.alpha[i] = alpha[i-1];
        }
        this.x0 = x0;
        this.tankH = tankH;
    }


    /**
    * @Description: 计算底孔出流分量，底孔出流计算公式参照具体推理过程
    * @Param: [n:第n个侧边孔以上的水面过程, t：出流时间（保证水位大于等this.h[n]）, h：计算初始水位]
    * @return: double 返回底孔出流流量
    * @Author: gavin
    * @Date: 2019/2/27
    */
    private double getBiankongK_(int n, double t, double h) {
        double ret = 0;
        double a = getA(n);
        double b = getB(n);
        double beta = this.alpha[0];

        ret += b*beta*t/a;
        ret += beta*(h-b/a)*(1-Math.exp(-a*t))/a;
        return ret;
    }


    /**
    * @Description: 出流结构
    * @Param:
    * @return:
    * @Author: gavin
    * @Date: 2019/2/27
    */
    private static class Effluent {
        double dikong;
        double biankong;
    }



    // 按侧边孔分段计算底孔出流分量
    /**
    * @Description: 计算底孔和侧边孔的出流量， 通过cache中存储的出流过程计算
    * @Param: [h：计算时段末水位]
    * @return: whu.hydro.core.model.Tank.Effluent
    * @Author: gavin
    * @Date: 2019/2/27
    */
    private Effluent getEffluent(double h) {
        Effluent effluent = new Effluent();
        Iterator<Cache> iter = cache.iterator();
        double maxH = Double.MIN_VALUE;
        while (iter.hasNext()) {
            Cache next = iter.next();
            effluent.dikong += getBiankongK_(next.n, next.t, next.h);
            maxH = maxH > next.h?maxH:next.h;
            if (next.h < this.h[next.n]) {
                System.out.println("cuowu");
            }
        }
        effluent.biankong = (maxH-h) - effluent.dikong;
        return effluent;
    }

    public void init(int length) {
        x = new double[length];
        y = new double[length];
        z = new double[length];
        c = new double[length];

        x[0] = x0;
    }



    public void run(HydroSeries hydroSeries) {
        Hydro[] hydros = hydroSeries.getHydros();
        init(hydros.length);
        for (int i = 1; i < hydros.length; i++) {
            runStep(hydros[i].P, i);
//            x[i] = getHByT(x[i-1], DefautParameter.TIMESTEP);
//            double dx = x[i] - x[i-1];
//            Effluent effluent = getEffluent(x[i]);
//            System.out.println(x[i]);
//            System.out.println(effluent.biankong+effluent.dikong);
//
//            y[i] = effluent.biankong;
//            z[i] = effluent.dikong;
//            x[i] += dx + hydros[i].P;
        }
    }

    public void runStep(double input, int i) {
        x[i] = getHByT(x[i-1], DefautParameter.TIMESTEP);
        double dx = x[i] - x[i-1];
        Effluent effluent = getEffluent(x[i]);
//        System.out.println(x[i]);
//        System.out.println(effluent.biankong+effluent.dikong);

        y[i] = effluent.biankong;
        z[i] = effluent.dikong;
        x[i] += dx + input;
        if (x[i] > tankH) {
            c[i] = x[i] - tankH;
            x[i] = tankH;
        }

    }


    /**
    * @Description: 根据当前水位，和出流时间，计算时段末水箱水位
    * @Param: [h0:初始水位, t：出流时间]
    * @return: double 返回计算时段末水箱水位
    * @Author: gavin
    * @Date: 2019/2/27
    */
    private double getHByT(double h0, double t) {
        cache.clear();
        double nextH = 0.0;
        int n = -1;
        for (int i = this.h.length -1 ; i >= 0; i--) {
            if (h0 > this.h[i]+ DefautParameter.CALERROR) {
                n = i;
                break;
            }
        }
        nextH = h0;
        if (n >= 0) {

            while (t > DefautParameter.CALERROR) {
                double tempT = 0;
                try {
                    tempT = getTByH_(nextH, h[n], n);
                } catch (TankException e) {
                    e.printStackTrace();
                }
                if (t > tempT) {
                    cache.add(new Cache(tempT,n, nextH));
                    t -= tempT;
                } else {
                    cache.add(new Cache(t,n, nextH));
                    nextH = getHByT_(nextH, t, n--);
                    break;
                }
                nextH = h[n--];
            }
        } else {
            return nextH;
        }
        return nextH;
    }


    // 按边孔位置计算出流时间
    /**
    * @Description: 保证不跳跃边孔的条件下，通过时段初，时段末水位，及对应边孔位置计算出流时间
    * @Param: [h0：时段初水位, h1：时段末水位, n：对应侧边孔]
    * @return: double 计算时长超过单步长，则输出单步长
    * @Author: gavin
    * @Date: 2019/2/27
    */
    private double getTByH_(double h0, double h1, int n) throws TankException {
        if (h0 < h1) throw new TankException("h0设置太小");
        if(!(h0>h[n]&&h1>=h[n])) throw new TankException("临近边孔设置错误");
        if (h0 < h1+DefautParameter.CALERROR) return 0.0;

        double leftT = 0.0;
        double rightT = DefautParameter.TIMESTEP;
        double nextH = getHByT_(h0, rightT, n);
        if (nextH > h1) return DefautParameter.TIMESTEP;
        double t = (leftT+rightT)/2.0;
        nextH = getHByT_(h0, t, n);
        while (Math.abs(nextH-h1) > DefautParameter.CALERROR) {
            if (nextH>h1) {
                leftT = t;
                t = (leftT+rightT)/2.0;
            } else {
                rightT = t;
                t = (leftT+rightT)/2.0;
            }
            nextH = getHByT_(h0, t, n);
        }
        return t;
    }

    /** 
    * @Description: 获取参数a 
    * @Param: [n] 
    * @return: double 
    * @Author: gavin 
    * @Date: 2019/2/27 
    */ 
    private double getA(int n) {
        double ret = 0;
        for (int i = 0; i <= n ; i++) {
            ret += this.alpha[i];
        }
        return ret;
    }

    /** 
    * @Description: 获取参数b
    * @Param: [n] 
    * @return: double 
    * @Author: gavin 
    * @Date: 2019/2/27 
    */ 
    private double getB(int n) {
        double ret = 0;
        for (int i = 0; i <= n ; i++) {
            ret += this.alpha[i] * this.h[i];
        }
        return ret;
    }


    /**
    * @Description: 保证不跳跃边孔的条件下，通过时段初水位，出流时间计算时段末水位，具体公式见推导过程
    * @Param: [h0：时段初水位, t：出流时间, n：对应侧边孔]
    * @return: double
    * @Author: gavin
    * @Date: 2019/2/27
    */
    private double getHByT_(double h0, double t, int n) {
        double a = getA(n);
        double b = getB(n);
        return b/a + (h0-b/a)*Math.exp(-a*t);
    }

    /** 
    * @Description: 测试方法 
    * @Param: [args] 
    * @return: void 
    * @Author: gavin 
    * @Date: 2019/2/27 
    */ 
    public static void main(String[] args) {
        double[] h = new double[] {2,4,6};
        double[] alpha = {0.02,0.01,0.03};
        double beta = 0.04;
        double x0 = 8;
        double tankH = 10;

        Tank tank = new Tank(h,alpha,beta,x0,tankH);

        double[] Q = {5,7,8,4,2,9};
        double[] E = {2,3,4,4,1,5};
        HydroSeries hydroSeries = new HydroSeries(Q, E);
        tank.run(hydroSeries);
    }
}
