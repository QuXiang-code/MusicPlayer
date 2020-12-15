package com.ui;

import javax.swing.*;
import java.awt.*;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/12
 */
public class GlobalPanel extends JPanel {
    private Image backgroundImage;

    public GlobalPanel() {

    }

    public void setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // 宽高设置为组件的宽高，observer 设置成组件就可以自适应
            g.drawImage(
                    backgroundImage,
                    0, 0,
                    getWidth(),
                    getHeight(),
                    this
            );
        }
    }
}
