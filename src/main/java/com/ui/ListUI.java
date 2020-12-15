package com.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicListUI;
import java.awt.*;

/**
 * @Author yzx
 * @Description 列表自定义 UI
 * @Date 2020/12/13
 */
public class ListUI extends BasicListUI {
    private Color highlightColor;
    private int highlightIndex;

    public ListUI(Color highlightColor, int highlightIndex) {
        this.highlightColor = highlightColor;
        this.highlightIndex = highlightIndex;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
    }

    @Override
    protected void paintCell(Graphics g,
                             int row,
                             Rectangle rowBounds,
                             ListCellRenderer<Object> cellRenderer,
                             ListModel<Object> dataModel,
                             ListSelectionModel selModel,
                             int leadIndex) {
        Graphics2D g2d = (Graphics2D) g;
        if(row != highlightIndex) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        } else {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
        super.paintCell(g, row, rowBounds, cellRenderer, dataModel, selModel, leadIndex);
    }
}
