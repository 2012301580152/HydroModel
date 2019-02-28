package whu.hydro.optimize.jgap;



import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @ClassName JGAP
 * @Description TODO
 * @Author Gavin
 * @Date 2018/11/27 16:47
 * @Version 1.0
 */
public class JGAP {

    private List<Ga> gaList;

    private DownUp[] downUps;

    private int GaNum;

    public static void run(Ga ga){
//        topmodel ca = new topmodel();
//
//        ca.setT0(ga.getParameters()[0].getRealValue());
//        ca.setTd(ga.getParameters()[1].getRealValue());
//        ca.setSzm(ga.getParameters()[2].getRealValue());
//        ca.setSrmax(ga.getParameters()[3].getRealValue());
//        ca.setQb1(ga.getParameters()[4].getRealValue());
//        ca.setSR0(ga.getParameters()[5].getRealValue());
//        ca.setSuz0(ga.getParameters()[6].getRealValue());
//        ca.setKc(ga.getParameters()[7].getRealValue());
//        ca.setCr(ga.getParameters()[8].getRealValue());
//        ca.setMF(ga.getParameters()[9].getRealValue());
//        ca.setTm(ga.getParameters()[10].getRealValue());
//        ca.setB(ga.getParameters()[11].getRealValue());
//        ca.readData("data_day.txt");
//        ca.readTWI("海100类.txt");
//        ca.init(322000000);
//        ca.runoff2();
//        ca.routing();
//        ca.toTxt("模型计算结果.txt");
//
//        ga.setDc(ca.getDc());
    }






    public JGAP(int gaNum, DownUp[] downUps) {
        GaNum = gaNum;

        gaList = new ArrayList<>();
        for(int i = 0; i < gaNum; i++) {
            Ga temp = new Ga();
            Parameter[] parameters = new Parameter[downUps.length];
            for (int j = 0; j < parameters.length; j++) {
                parameters[j] = new Parameter(downUps[j].getMax(), downUps[j].getMin());
            }
            temp.setParameters(parameters);
            gaList.add(temp);
        }
    }

    private Parameter genNewParameter(Parameter fu, Parameter ma) {
        Parameter result = new Parameter(fu.getMax(), fu.getMin());
        result.setValue(fu.getValue()^ma.getValue());
        return result;
    }


    private Ga genNewGa(Ga fu, Ga ma) {
        Ga result = new Ga();
        int paraLength = fu.getParameters().length;
        Parameter[] parameters = new Parameter[paraLength];
        Parameter[] fuparameters = fu.getParameters();
        Parameter[] maparameters = ma.getParameters();
        for(int i = 0; i < paraLength; i++){
            parameters[i] = genNewParameter(fuparameters[i], maparameters[i]);
        }

        result.setParameters(parameters);

        return result;
    }

    private List<Ga> GaChange(List<Ga> gaList, double rate) {
        return gaList;
    }

    public double nextGas(){
        Collections.sort(gaList);
        List<Ga> gaListCopy = depCopy(gaList);
        gaListCopy = GaChange(gaListCopy, 0.05);


        for(int i = 2; i < gaList.size(); i++){

            int fuindex = ThreadLocalRandom.current().nextInt(0, gaList.size()/2);
            int maindex = ThreadLocalRandom.current().nextInt(0, gaList.size()/2);
            Ga fu = gaListCopy.get(fuindex);
            Ga ma = gaListCopy.get(maindex);
            gaList.set(i, genNewGa(fu, ma));
        }
        return gaList.get(0).getDc();
    }

    /***
     * 方法一对集合进行深拷贝 注意需要对泛型类进行序列化(实现Serializable)
     *
     * @param srcList
     * @param <T>
     * @return
     */
    public static <T> List<T> depCopy(List<T> srcList) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(srcList);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(byteIn);
            List<T> destList = (List<T>) inStream.readObject();
            return destList;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void run(){
        for (int n = 0; n < 200; n++){
            final CountDownLatch latch = new CountDownLatch(gaList.size());
            for(Ga ga : gaList){
                Top top = new Top(ga, "ga"+ga.getParameters(), latch);
                top.start();
                //latch.countDown();
            }

            try {
                latch.await();
                System.out.println(n+","+nextGas());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        System.out.println(gaList.get(0).getDc());

        Parameter[] parameters = gaList.get(0).getParameters();

        for (int i = 0; i < parameters.length; i++) {
            System.out.println(parameters[i].getRealValue());
        }

    }
}
