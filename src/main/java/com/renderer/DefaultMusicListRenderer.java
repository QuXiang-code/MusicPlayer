package com.renderer;

import com.constant.SimplePath;
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
public class DefaultMusicListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont;

    private MusicPlayer player;
    private ImageIcon musicIcon = new ImageIcon(SimplePath.ICON_PATH + "music.png");
    private ImageIcon playingIcon = new ImageIcon(SimplePath.ICON_PATH + "playing.png");

    public DefaultMusicListRenderer(Font customFont, MusicPlayer player) {
        this.customFont = customFont;
        this.player = player;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        File file = (File) value;
        this.setText(file.getName());
        // 播放中的文件图标不同
        if(!player.isPlayingFile(file)) this.setIcon(musicIcon);
        else this.setIcon(playingIcon);
        this.setFont(customFont);
        return this;
    }
}
