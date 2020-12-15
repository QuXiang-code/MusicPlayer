package com.utils;

import com.fft.Complex;
import com.fft.FFT;

import java.io.*;
import java.util.List;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/9
 */
public class FFTUtils {
    /**
     * 字节 List 转为 FFT 处理后的数组
     * @param lists
     * @return
     */
    public static Double[] listToArray(List<Double> lists) {
        //Double[] c = Stream.of(lists).map(a->a.toString()).collect(Collectors.toList()).stream().map(b->Double.parseDouble(b)).toArray(Double[]::new);
        Double[] c = new Double[lists.size()];
        synchronized (lists) {
            for (int i = 0; i < lists.size(); i++) {
                try {
                    c[i] = Double.valueOf((lists.get(i) == null ? "0.0" : lists.get(i).toString()));
                } catch (Exception e) {
                    c[i] = 0.0;
                }
            }
        }
        Complex[] x = new Complex[c.length];
        for (int i = 0; i < x.length; i++) {
            try {
                x[i] = new Complex(c[i], 0);
            } catch (Exception e) {
                x[i] = new Complex(0, 0);
            }
        }
        return FFT.array(x);
    }
}
