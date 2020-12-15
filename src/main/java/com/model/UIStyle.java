package com.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.*;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/12
 */
@Data
@AllArgsConstructor
public class UIStyle {
    // 风格类型
    private int styleType;
    // 决定组件是否透明
    private Boolean opaque;
    // 风格对应的图片路径
    private String styleImgPath;
    // 风格播放列表前景颜色
    private Color foreColor;
    // 风格播放列表选中颜色
    private Color selectedColor;
    // 风格歌词文字颜色
    private Color lrcColor;
    // 风格歌词高亮颜色
    private Color highlightColor;
    // 右上角标签颜色
    private Color labelColor;
    // 进度条边框和填充颜色
    private Color timeBarColor;
    // 按钮颜色
    private Color buttonColor;
    // 滚动条颜色
    private Color scrollBarColor;
    // 滑动条颜色
    private Color sliderColor;
    // 频谱颜色
    private Color spectrumColor;
}
