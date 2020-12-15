package com.utils;

import ws.schild.jave.*;

import java.io.File;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/6
 */
public class TransCodeUtils {
    public static File transCode(File source) throws EncoderException {
        // 源文件作为媒体流
        MultimediaObject multimediaObject = new MultimediaObject(source);
        // 目标文件
        File target = new File("C:\\Users\\我\\Desktop\\1.mp3");
        AudioAttributes audio = new AudioAttributes();
        // 转码器名称
        audio.setCodec("libmp3lame");
        // 音频流的比特率值
        audio.setBitRate(128000);
        // 声道
        audio.setChannels(2);
        // 音频流的采样率
        audio.setSamplingRate(44100);
        EncodingAttributes attrs = new EncodingAttributes();
        // 编码格式
        attrs.setFormat("mp3");
        attrs.setAudioAttributes(audio);
        Encoder encoder = new Encoder();
        // 转码到目标文件
        encoder.encode(multimediaObject, target, attrs);
        return target;
    }
}
