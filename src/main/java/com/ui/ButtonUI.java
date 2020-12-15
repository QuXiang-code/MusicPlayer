package com.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

/**
 * @Author yzx
 * @Description 按钮自定义 UI
 * @Date 2020/12/13
 */
public class ButtonUI extends BasicButtonUI {
    private ImageIcon icon;
    private Color borderColor;

    public ButtonUI(ImageIcon icon, Color borderColor) {
        this.icon = icon;
        this.borderColor = borderColor;
    }

//    /**
//     * 添加鼠标悬停监听器
//     *
//     * @param b
//     * @return
//     */
//    @Override
//    protected BasicButtonListener createButtonListener(AbstractButton b) {
//        b.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseMoved(MouseEvent e) {
//                JButton button = (JButton) b;
//                Graphics g = button.getGraphics();
//                g.setColor(borderColor);
//                g.drawRect(0, 0, button.getWidth() - 1, button.getHeight() - 1);
//            }
//        });
//        return super.createButtonListener(b);
//    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        JButton b = (JButton) c;
        g.setColor(borderColor);
        g.drawRect(0, 0, b.getWidth() - 1, b.getHeight() - 1);
    }

    @Override
    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
        super.paintFocus(g, b, viewRect, textRect, iconRect);
        g.setColor(borderColor);
        g.drawRect(0, 0, b.getWidth() - 1, b.getHeight() - 1);
        g.drawRect(2, 2, b.getWidth() - 5, b.getHeight() - 5);
    }

    /**
     * 按钮被按下的样式
     *
     * @param g
     * @param b
     */
    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        super.paintButtonPressed(g, b);
        g.setColor(borderColor);
        g.drawRect(0, 0, b.getWidth() - 1, b.getHeight() - 1);
        g.drawRect(1, 1, b.getWidth() - 3, b.getHeight() - 3);
        g.drawRect(2, 2, b.getWidth() - 5, b.getHeight() - 5);
    }
}
