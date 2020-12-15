package com.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;

/**
 * @Author yzx
 * @Description 进度条自定义 UI
 * @Date 2020/12/13
 */
public class ProcessBarUI extends BasicProgressBarUI {
    private int xOffset = 5;
    private int yOffset = 3;
    private Color foreColor;

    public ProcessBarUI(Color foreColor) {
        this.foreColor = foreColor;
    }

    /**
     * 画确定进度的进度条
     *
     * @param g
     * @param c
     */
    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        JProgressBar pb = (JProgressBar) c;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(foreColor);
        float radio = (float) pb.getValue() / pb.getMaximum();
        int width = pb.getWidth(), height = pb.getHeight();
        g2d.fillRoundRect(
                xOffset,
                yOffset,
                (int) ((width - 2 * xOffset) * radio),
                height - 2 * yOffset,
                10,
                10
        );
    }
}
