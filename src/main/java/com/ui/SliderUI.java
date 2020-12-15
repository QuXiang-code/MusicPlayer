package com.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;

/**
 * @Author yzx
 * @Description 滑动条自定义 UI
 * @Date 2020/12/13
 */
public class SliderUI extends BasicSliderUI {
    private Color thumbColor;
    private Color trackColor;

    public SliderUI(JSlider slider, Color thumbColor, Color trackColor) {
        super(slider);
        this.thumbColor = thumbColor;
        this.trackColor = trackColor;
    }

    /**
     * 自定义把手
     *
     * @param g
     */
    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(thumbColor);
        // 避免锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fillOval(
                thumbRect.x,
                thumbRect.y + 4,
                thumbRect.width,
                thumbRect.width
        );
//        g2d.fillRect(
//                thumbRect.x,
//                thumbRect.y,
//                10,
//                20
//        );
    }

    /**
     * 自定义滑道
     *
     * @param g
     */
    @Override
    public void paintTrack(Graphics g) {
        g.setColor(trackColor);
        g.fillRect(
                trackRect.x,
                trackRect.y + 9,
                trackRect.width,
                trackRect.height - 18
        );
    }
}
