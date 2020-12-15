package com.renderer;

import lombok.Data;

import javax.swing.*;
import java.awt.*;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@Data
public class TranslucentLrcListRenderer extends DefaultListCellRenderer {
    private Font defaultFont;
    private Font highlightFont;
    private Color backgroundColor;
    private int row;
    private int[] rows;

    public TranslucentLrcListRenderer() {

    }

    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        // 所有标签透明
        JLabel label = (JLabel) component;
        if(index != row) label.setOpaque(false);
        else label.setOpaque(true);
        if (rows == null) {
            // 高亮的行的样式
            if (index == row) {
                // 背景色透明的方法
                backgroundColor = new Color(
                        backgroundColor.getRed(),
                        backgroundColor.getGreen(),
                        backgroundColor.getBlue(),
                        (int) (255 * 0.6));
                setBackground(backgroundColor);
                setFont(defaultFont.deriveFont((float) (defaultFont.getSize() + 10)));
            }
            // 其他行的样式
            else {
                setFont(defaultFont);
            }
        } else {
            for (int i = 0; i < rows.length; i++) {
                if (index == rows[i]) {
                    setBackground(backgroundColor);
                    setFont(getFont().deriveFont((float) (getFont().getSize() + 8)));
                }
            }
        }
        return this;
    }
}
