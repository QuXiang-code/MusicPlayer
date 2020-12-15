package com.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@NoArgsConstructor
@AllArgsConstructor
public class Statement {
    private double time = 0.0;                                //时间, 单位为s, 精确到0.01s
    private String lyric = "";                                //歌词

    /*
     * 获取时间
     */
    public double getTime() {
        return time;
    }

    /*
     * 设置时间
     * time: 被设置成的时间
     */
    public void setTime(double time) {
        this.time = time;
    }

    /*
     * 设置时间
     * time: 被设置成的时间字符串, 格式为mm:ss.ms
     */
    public void setTime(String time) {
        String str[] = time.split(":|\\.");
        this.time = Integer.parseInt(str[0]) * 60 + Integer.parseInt(str[1]) + Integer.parseInt(str[2]) * 0.01;
    }

    /*
     * 获取歌词
     */
    public String getLyric() {
        return lyric;
    }

    /*
     * 设置歌词
     */
    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    /*
     * 打印歌词
     */
    public void printLyric() {
        System.out.println(time + ": " + lyric);
    }

    @Override
    public String toString() {
        return lyric;
    }
}
