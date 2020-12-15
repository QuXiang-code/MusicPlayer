package com.model;

import lombok.Data;

import javax.swing.*;
import java.io.File;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@Data
public class MusicInfo {
    // 文件
    private File file;
    // 音频格式
    private String format;
    // 时长(秒)
    private float duration;
    // 歌曲名称
    private String name;
    // 艺术家
    private String artist;
    // 专辑名称
    private String albumName;
    // 专辑图片
    private ImageIcon album;
}
