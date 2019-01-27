package whu.hydro.core;

import whu.hydro.core.exception.HydroException;
import whu.hydro.core.exception.HydroLengthNotEqualException;
import whu.hydro.core.exception.NotEnoughLongException;
import whu.hydro.core.exception.RainfallNotExistEqualException;

import java.util.Date;

/**
 * @ClassName HydroSeries
 * @Description TODO
 * @Author 86187
 * @Date 2019/1/14 10:52
 * @Version 1.0
 */
public class HydroSeries {

    private boolean equalStep;
    private boolean isChecked = DefautParameter.ISCHECKED;
    private Hydro[] hydros;
    private long duration = DefautParameter.DURATION;



    public HydroSeries(double[] P, long duration){
        try {
            instant(null, P, null, true, duration, true);
        } catch (HydroException e) {
            // 内部错误
            e.printStackTrace();
        }
    }

    public HydroSeries(double[] P) {

        try {
            instant(null, P, null, true, this.duration, true);
        } catch (HydroException e) {
            // 内部错误
            e.printStackTrace();
        }
    }

    public HydroSeries(double[] P, double[] E) {

        try {
            instant(null, P, E, true, this.duration, true);
        } catch (HydroException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 该方法近期不实现
     * @Param: [dates, P, E, isEqualStep]
     * @return:
     * @Author: gavin
     * @Date: 2019/1/14
     */
    public HydroSeries(Date[] dates, double[] P, double[] E, boolean isEqualStep){
        try {
            long theDuration;
            if (isEqualStep) {
                if (isEqualStep(dates)) {
                    theDuration = getDuration(dates);
                }
            } else {
                theDuration = DefautParameter.ERRORDURATION;
            }

            instant(dates, P, E, isEqualStep, DefautParameter.DURATION, true);
        } catch (HydroException e) {
            // 内部错误
            e.printStackTrace();
        }
    }

    /**
    * @Description: 不建议使用 isChecked = true，
    * @Param: [dates, P, E, isEqualStep, duration, isChecked]
    * @return:
    * @Author: gavin
    * @Date: 2019/1/14
    */
    private HydroSeries(Date[] dates, double[] P, double[] E, boolean isEqualStep, long duration, boolean isChecked){
        try {
            instant(dates, P, E, isEqualStep, duration, isChecked);
        } catch (HydroException e) {
            // 内部错误
            e.printStackTrace();
        }
    }



    public Hydro[] getHydros() {
        return hydros;
    }

    private void instant(Date[] dates, double[] P, double[] E, boolean isEqualStep, long duration, boolean isChecked)
            throws HydroLengthNotEqualException, RainfallNotExistEqualException,NotEnoughLongException {
        if (dates!=null) {
            int length = dates.length;
            if (P!=null && P.length != length) {
                throw new HydroLengthNotEqualException("时间降雨序列长度不等");
            }

            if (E!=null && P.length != length) {
                throw new HydroLengthNotEqualException("时间蒸发序列长度不等");
            }
            if (!isChecked) {
                // 等时检验
                if (isEqualStep) {
                    if(isEqualStep(dates)){
                        this.equalStep = isEqualStep;
                    } else {
                        // log 警告，时间序列不等，与设定不同
                        this.equalStep = false;
                    }
                }

                // 时间步长检验
                long tempDuration = getDuration(dates);

            }
            this.isChecked = true;

        } else {
            if (E!=null) {
                if (P.length!=E.length) {
                    throw new HydroLengthNotEqualException("降雨蒸发序列长度不等");
                }
            }

            if (P==null) {
                throw new RainfallNotExistEqualException("降雨数据不存在");
            }
        }
        setHydros(dates, P, E);
    }

    private void setHydros(Date[] dates, double[] P, double[] E) {
        if (dates!=null) {
            for (int i = 0; i < dates.length; i++) {
                hydros[i].date = dates[i];
            }
        }

        if (P!=null) {
            for (int i = 0; i < P.length; i++) {
                hydros[i].P = P[i];
            }
        }

        if (E!=null) {
            for (int i = 0; i < E.length; i++) {
                hydros[i].E = E[i];
            }
        }
    }

//    private void setDefaultHydros(Date[] dates, double[] P, double[] E)



    private boolean isEqualStep(Date[] dates) throws NotEnoughLongException {

        if (dates!=null && dates.length>=2) {
            long theDuration = dates[1].getTime() - dates[0].getTime();

            for (int i = 1; i < dates.length-1; i++) {
                if (!(theDuration==dates[i+1].getTime()-dates[i].getTime())) {
                    return false;
                }
            }
            return true;
        } else {
            throw new NotEnoughLongException("时间序列不存在，或者太短");
        }


    }

    private long getDuration(Date[] dates) throws NotEnoughLongException {
        if (dates!=null && dates.length>=2) {
            long theDuration = dates[1].getTime() - dates[0].getTime();

            if (isChecked) {
                if (equalStep) {
                    return theDuration;
                }
            } else {
                if (isEqualStep(dates)){
                    return theDuration;
                }
            }
        }

        return DefautParameter.ERRORDURATION;
    }

    //    public boolean is
}
