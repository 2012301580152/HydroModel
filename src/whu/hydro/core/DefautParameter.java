package whu.hydro.core;

/**
 * @ClassName DefautParameter
 * @Description TODO
 * @Author 86187
 * @Date 2019/1/14 11:34
 * @Version 1.0
 */
public class DefautParameter {
    final public static long DURATION = 60*60*1000;
    final public static boolean ISCHECKED = false;
    final public static long ERRORDURATION = -1;
    final public static double ERROR = 0.0000001; // 比较误差
    final public static double CALERROR = 0.001; // 水箱水位误差
    final public static double TIMESTEP = 1.0; // 以小时为单位
    final public static double OUTK = 0.1;
}
