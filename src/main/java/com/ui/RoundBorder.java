package com.ui;

import javax.swing.border.Border;
import java.awt.*;

/**
 * @Author yzx
 * @Description 自定义圆角边框
 * @Date 2020/12/13
 */
public class RoundBorder implements Border {
    private Color borderColor;

    public RoundBorder(Color borderColor) {
        this.borderColor = borderColor;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(borderColor);
        g2d.drawRoundRect(
                0,
                0,
                c.getWidth() - 1,
                c.getHeight() - 1,
                10,
                10);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, 0, 0);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
