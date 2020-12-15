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
public class DefaultLrcListRenderer extends DefaultListCellRenderer {
    private Font defaultFont;
    private Font highlightFont;
    private Color backgroundColor;
    private int row;
    private int[] rows;

    public DefaultLrcListRenderer() {

    }

    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (rows == null) {
            // 高亮的行的样式
            if (index == row) {
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
