package whu.hydro.optimize.jgap;

/**
 * @ClassName DownUp
 * @Description TODO
 * @Author Gavin
 * @Date 2018/11/27 19:24
 * @Version 1.0
 */
public class DownUp {
    private double min;
    private double max;
    public double s;

    public DownUp(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public DownUp(double min, double max, double s) {
        this.min = min;
        this.max = max;
        this.s = s;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
