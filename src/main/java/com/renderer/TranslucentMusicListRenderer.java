package com.renderer;

import com.constant.SimplePath;
import com.utils.ImageUtils;
import com.model.MusicPlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslucentMusicListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont;
    // 前景色
    private Color foreColor;
    // 选中的颜色
    private Color selectedColor;

    private MusicPlayer player;
    private ImageIcon musicIcon = new ImageIcon(SimplePath.ICON_PATH + "music.png");
    private ImageIcon playingIcon = new ImageIcon(SimplePath.ICON_PATH + "playing.png");

    public TranslucentMusicListRenderer(Font font, MusicPlayer player) {
        this.customFont = font;
        this.player = player;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        File file = (File) value;
        this.setText(file.getName());
        // 播放中的文件图标不同
        if (!player.isPlayingFile(file)) {
            this.setIcon(ImageUtils.dye(musicIcon, foreColor));
        }
        else this.setIcon(ImageUtils.dye(playingIcon, selectedColor));
        this.setFont(customFont);
        this.setForeground(foreColor);
        // 所有标签透明
        JLabel label = (JLabel) component;
        label.setOpaque(false);
        if (isSelected) {
            label.setForeground(selectedColor);
        }
        return this;
    }
}
