package com.utils;

import com.constant.Format;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Author yzx
 * @Description 音乐工具类
 * @Date 2020/12/11
 */
public class MusicUtils {
    /**
     * 获取音频文件时长
     * @param source
     * @return
     * @throws TagException
     * @throws ReadOnlyFileException
     * @throws CannotReadException
     * @throws InvalidAudioFrameException
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public static double getDuration(File source) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException, UnsupportedAudioFileException {
        // 取消 jaudiotagger 的日志显示
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);

        if(source.getName().endsWith(Format.MP3)) {
            MP3File f = (MP3File) AudioFileIO.read(source);
            MP3AudioHeader audioHeader = (MP3AudioHeader) f.getAudioHeader();
            return audioHeader.getTrackLength();
        } else {
            AudioInputStream stream = AudioSystem.getAudioInputStream(source);
            AudioFormat format = stream.getFormat();
            return stream.getFrameLength() / format.getSampleRate();
        }
    }
}
