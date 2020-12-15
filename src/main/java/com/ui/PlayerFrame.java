package com.ui;

import com.component.ExtensionFileFilter;
import com.component.MySemaphore;
import com.constant.*;
import com.model.LrcData;
import com.model.MusicPlayer;
import com.model.Statement;
import com.model.UIStyle;
import com.renderer.DefaultLrcListRenderer;
import com.renderer.DefaultMusicListRenderer;
import com.renderer.TranslucentLrcListRenderer;
import com.renderer.TranslucentMusicListRenderer;
import com.utils.*;
import javazoom.jl.decoder.JavaLayerException;
import net.coobird.thumbnailator.Thumbnails;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

public class PlayerFrame extends JFrame {
    private final String TITLE = "音乐播放器";
    private final String CURR_AUDIO_FILE_LABEL = "当前文件：";
    private final String SONG_NAME_LABEL = "曲名：";
    private final String ARTIST_LABEL = "艺术家：";
    private final String ALBUM_NAME_LABEL = "专辑：";
    // 专辑图片宽高
    private final int ALBUM_IMAGE_WIDTH = 150;
    private final int ALBUM_IMAGE_HEIGHT = 150;
    // 进度条最小值
    private final int TIME_BAR_MIN = 0;
    // 进度条最大值
    private final int TIME_BAR_MAX = 1000000;
    // 默认音量
    private final int DEFAULT_VOLUME = 90;
    // 最大音量
    private final int MAX_VOLUME = 100;
    // 默认快进/快退时间
    private final int DEFAULT_FORWARD_OR_BACKWARD_TIME = 10;
    // 默认倍速
    private final float DEFAULT_RATE = 1f;
    // 歌词高亮位置
    private final int LRC_INDEX = 7;
    // 无歌词或歌词损坏提示
    private final String NO_LRC_MSG = "尽情享受音乐的美好";
    // 询问是否隐藏到托盘
    private final String ASK_DISPOSE_MSG = "你希望隐藏到托盘还是退出程序？";
    private final String[] EXIT_OPTIONS = {"隐藏到托盘", "退出程序", "取消"};
    // 询问是否清空播放列表的提示
    private final String ASK_CLEAR_MUSIC_LIST_MSG = "播放列表已存在歌曲，您希望保留播放列表的歌曲吗？(选择“否”将清空原有的歌曲列表)";
    // 询问是否删除不存在的文件的提示
    private final String ASK_REMOVE_FIFE_NOT_FOUND_MSG = "该歌曲已被移动或删除，是否从播放列表中删除？";
    // 询问是否右键删除
    private final String ASK_REMOVE_FIFE_MSG = "是否从播放列表中删除选中的歌曲？";
    // 询问是否覆盖
    private final String ASK_OVERWRITE_FIFE_MSG = "文件已存在，是否覆盖？";
    // 帮助信息
    private final String HELP_MSG = "这里没有指南\n\n让音乐作为你的北极星吧\n\n戴上耳机，闭上眼睛，沉醉在美妙的旋律里感受生活......";
    private final String DEFAULT_TIME = "0:0";
    //    private final String PLAY_BUTTON_TEXT = "播放";
//    private final String LAST_BUTTON_TEXT = "上一曲";
//    private final String NEXT_BUTTON_TEXT = "下一曲";
//    private final String PAUSE_BUTTON_TEXT = "暂停";
    private final String FORWARD_BUTTON_TEXT = "快进";
    private final String BACKWARD_BUTTON_TEXT = "快退";
    // 右键菜单播放文字
    private final String PLAY_MENU_ITEM_TEXT = "播放        ";
    // 右键菜单删除文字
    private final String REMOVE_MENU_ITEM_TEXT = "从播放列表删除        ";
    // 右键菜单保存专辑图片文字
    private final String SAVE_ALBUM_IMAGE_TEXT = "保存专辑图片        ";

    // 主界面标题图标
    private ImageIcon titleIcon = new ImageIcon(SimplePath.ICON_PATH + "favicon.png");
    // 播放图标
    private ImageIcon playIcon = new ImageIcon(SimplePath.ICON_PATH + "play.png");
    // 暂停图标
    private ImageIcon pauseIcon = new ImageIcon(SimplePath.ICON_PATH + "pause.png");
    // 上一曲图标
    private ImageIcon lastIcon = new ImageIcon(SimplePath.ICON_PATH + "last.png");
    // 下一曲图标
    private ImageIcon nextIcon = new ImageIcon(SimplePath.ICON_PATH + "next.png");
    // 单曲循环图标
    private ImageIcon singleIcon = new ImageIcon(SimplePath.ICON_PATH + "single.png");
    // 顺序播放图标
    private ImageIcon sequenceIcon = new ImageIcon(SimplePath.ICON_PATH + "sequence.png");
    // 随机播放图标
    private ImageIcon shuffleIcon = new ImageIcon(SimplePath.ICON_PATH + "shuffle.png");
    // 添加歌曲图标
    private ImageIcon addIcon = new ImageIcon(SimplePath.ICON_PATH + "add.png");
    // 删除歌曲图标
    private ImageIcon removeIcon = new ImageIcon(SimplePath.ICON_PATH + "remove.png");
    // 排序图标
    private ImageIcon sortIcon = new ImageIcon(SimplePath.ICON_PATH + "sort.png");
    // 换肤图标
    private ImageIcon styleIcon = new ImageIcon(SimplePath.ICON_PATH + "style.png");

    // 托盘图标
    TrayIcon trayIcon = new TrayIcon(titleIcon.getImage(), TITLE);

    // 悬浮帮助提示
    private final String PLAY_TIP = "播放";
    private final String PAUSE_TIP = "暂停";
    private final String LAST_TIP = "上一首";
    private final String NEXT_TIP = "下一首";
    private final String SINGLE_TIP = "单曲循环";
    private final String SEQUENCE_TIP = "顺序播放";
    private final String SHUFFLE_TIP = "随机播放";
    private final String ADD_TIP = "添加歌曲";
    private final String REMOVE_TIP = "删除歌曲";
    private final String SORT_TIP = "排序";
    private final String STYLE_TIP = "换肤";

    // 各种界面风格
    private UIStyle[] styles = {
            // 默认
            new UIStyle(UIStyleConstants.NORMAL, true, null,
                    Color.BLACK, Color.WHITE, Color.GRAY, Color.ORANGE, Color.BLACK,
                    null, Colors.THEME, null, null, Colors.THEME),
            // 夜晚
            new UIStyle(UIStyleConstants.NIGHT, false, SimplePath.STYLE_IMG_PATH + "nightStyle.jpg",
                    Colors.THEME, Colors.GOLD, Colors.GOLD3, Colors.THEME, Colors.THEME,
                    Colors.THEME, Colors.THEME, Colors.STEEL_BLUE_2, Colors.THEME, Colors.THEME),
            // 粉色回忆
            new UIStyle(UIStyleConstants.PINK, false, SimplePath.STYLE_IMG_PATH + "pinkStyle.jpg",
                    Colors.DEEP_PINK, Colors.MAROON, Colors.INDIAN_RED_3, Colors.LIGHT_SALMON_1, Colors.DEEP_PINK,
                    Colors.DEEP_PINK, Colors.DEEP_PINK, Colors.DEEP_PINK, Colors.DEEP_PINK, Colors.DEEP_PINK),
            // 她
            new UIStyle(UIStyleConstants.SHE, false, SimplePath.STYLE_IMG_PATH + "sheStyle.jpg",
                    Colors.BISQUE_1, Colors.BISQUE_3, Colors.BISQUE_1, Colors.BISQUE_3, Colors.BISQUE_1,
                    Colors.BISQUE_1, Colors.BISQUE_1, Colors.BISQUE_1, Colors.BISQUE_1, Colors.BISQUE_1),
            // 鹿鸣
            new UIStyle(UIStyleConstants.LUMING, false, SimplePath.STYLE_IMG_PATH + "lumingStyle.jpg",
                    Colors.CYAN_4, Colors.FOREST_GREEN, Colors.AQUAMARINE_4, Colors.AQUAMARINE_3, Colors.AQUAMARINE_4,
                    Colors.AQUAMARINE_4, Colors.AQUAMARINE_4, Colors.AQUAMARINE_4, Colors.AQUAMARINE_4, Colors.AQUAMARINE_4),
            // 你的名字
            new UIStyle(UIStyleConstants.YOUR_NAME, false, SimplePath.STYLE_IMG_PATH + "yourNameStyle.jpg",
                    Colors.DARK_ORANGE_1, Colors.CHOCOLATE_3, Colors.DARK_ORCHID_1, Colors.DODGER_BLUE_3, Colors.SKY_BLUE_1,
                    Colors.SKY_BLUE_1, Colors.SKY_BLUE_1, Colors.SKY_BLUE_1, Colors.SKY_BLUE_1, Colors.SKY_BLUE_1),
            // 深海
            new UIStyle(UIStyleConstants.DEEP_SEA, false, SimplePath.STYLE_IMG_PATH + "deepSeaStyle.jpg",
                    Colors.DEEP_SKY_BLUE_1, Colors.DEEP_SKY_BLUE_3, Colors.DEEP_SKY_BLUE_1, Colors.DEEP_SKY_BLUE_2, Colors.ROYAL_BLUE_3,
                    Colors.DEEP_SKY_BLUE_1, Colors.DEEP_SKY_BLUE_1, Colors.DEEP_SKY_BLUE_1, Colors.DEEP_SKY_BLUE_1, Colors.DEEP_SKY_BLUE_1)
    };

    private String currAudioFilePath = "无";
    private long forwardOrBackwardTime = DEFAULT_FORWARD_OR_BACKWARD_TIME;
    private long currVolume = DEFAULT_VOLUME;
    private float currRate = DEFAULT_RATE;
    // 当前播放曲目的索引
    private int currSong = -1;
    // 随机播放序列
    private List<Integer> shuffleList = new LinkedList<>();
    // 当前随机播放索引
    private int shuffleIndex;
    // 全局字体
    private Font globalFont = new Font("微软雅黑", Font.PLAIN, 15);
    // 主界面风格
    private UIStyle currUIStyle = null;

    private MusicPlayer player = new MusicPlayer();

    private JMenuBar menuBar = new JMenuBar();
    private JMenu fileMenu = new JMenu(" 文件(F) ");
    private JMenuItem open = new JMenuItem("打开歌曲文件      ");
    private JMenuItem openDir = new JMenuItem("载入歌曲文件夹...      ");
    private JMenuItem exit = new JMenuItem("退出      ");
    private JMenu playMenu = new JMenu(" 播放(P) ");
    private JMenu forwardOrBackward = new JMenu("快进/快退时间      ");
    private ButtonGroup forwardOrBackwardButtonGroup = new ButtonGroup();
    private JRadioButtonMenuItem s5 = new JRadioButtonMenuItem("5秒      ");
    private JRadioButtonMenuItem s10 = new JRadioButtonMenuItem("10秒      ", true);
    private JRadioButtonMenuItem s30 = new JRadioButtonMenuItem("30秒      ");
    private JMenu playMode = new JMenu("播放模式      ");
    private ButtonGroup playModeButtonGroup = new ButtonGroup();
    private JRadioButtonMenuItem single = new JRadioButtonMenuItem("单曲循环      ");
    private JRadioButtonMenuItem sequence = new JRadioButtonMenuItem("顺序播放      ", true);
    private JRadioButtonMenuItem shuffle = new JRadioButtonMenuItem("随机播放      ");
    private JMenu playRate = new JMenu("倍速      ");
    private ButtonGroup playRateButtonGroup = new ButtonGroup();
    private JRadioButtonMenuItem rate1 = new JRadioButtonMenuItem("0.5x      ");
    private JRadioButtonMenuItem rate2 = new JRadioButtonMenuItem("1x      ", true);
    private JRadioButtonMenuItem rate3 = new JRadioButtonMenuItem("1.5x      ");
    private JRadioButtonMenuItem rate4 = new JRadioButtonMenuItem("2x      ");
    private JMenu viewMenu = new JMenu("视图(V) ");
    private JCheckBoxMenuItem spectrumMenuItem = new JCheckBoxMenuItem("显示频谱      ", true);
    private JMenu individuationMenu = new JMenu("个性化(I) ");
    private JMenu styleMenu = new JMenu("界面风格      ");
    private ButtonGroup styleButtonGroup = new ButtonGroup();
    private JRadioButtonMenuItem[] styleMenuItems = {
            new JRadioButtonMenuItem("默认      ", true),
            new JRadioButtonMenuItem("夜晚      "),
            new JRadioButtonMenuItem("粉色回忆      "),
            new JRadioButtonMenuItem("她      "),
            new JRadioButtonMenuItem("鹿鸣      "),
            new JRadioButtonMenuItem("你的名字      "),
            new JRadioButtonMenuItem("深海      "),
    };
    private JMenu helpMenu = new JMenu("帮助(H) ");
    private JMenuItem helpMenuItem = new JMenuItem("指南      ");

    private JLabel currAudioFileLabel = new JLabel(CURR_AUDIO_FILE_LABEL + currAudioFilePath);
    // 歌名
    private JLabel songNameLabel = new JLabel();
    // 艺术家
    private JLabel artistLabel = new JLabel();
    // 专辑
    private JLabel albumLabel = new JLabel();
    // 专辑图片
    private JLabel albumImage = new JLabel();
    // 专辑图片右键弹出菜单
    private JPopupMenu albumImagePopupMenu = new JPopupMenu();
    // 右键菜单：保存专辑图片
    private JMenuItem saveAlbumImageMenuItem = new JMenuItem(SAVE_ALBUM_IMAGE_TEXT);

    JProgressBar timeBar = new JProgressBar(JProgressBar.HORIZONTAL);
    private JLabel currTimeLabel = new JLabel(DEFAULT_TIME);
    private JLabel durationLabel = new JLabel(DEFAULT_TIME);

    // 播放列表工具栏
    private JToolBar musicToolBar = new JToolBar();
    // 添加按钮
    private JButton addToolButton = new JButton(addIcon);
    // 添加按钮弹出菜单
    private JPopupMenu addPopupMenu = new JPopupMenu();
    private JMenuItem addFileMenuItem = new JMenuItem("添加歌曲");
    private JMenuItem addDirMenuItem = new JMenuItem("添加歌曲文件夹");
    // 删除按钮
    private JButton removeToolButton = new JButton(removeIcon);
    // 排序按钮
    private JButton sortToolButton = new JButton(sortIcon);
    // 排序按钮弹出菜单
    private JPopupMenu sortPopupMenu = new JPopupMenu();
    private ButtonGroup sortButtonGroup = new ButtonGroup();
    private JRadioButtonMenuItem sortByNameMenuItem = new JRadioButtonMenuItem("按名称", true);
    private JRadioButtonMenuItem sortByTimeMenuItem = new JRadioButtonMenuItem("按时长");
    private JRadioButtonMenuItem sortBySizeMenuItem = new JRadioButtonMenuItem("按大小");
    // 换肤按钮
    private JButton styleToolButton = new JButton(styleIcon);
    // 换肤按钮弹出菜单
    private JPopupMenu stylePopupMenu = new JPopupMenu();
    private ButtonGroup stylePopupMenuButtonGroup = new ButtonGroup();
    private JRadioButtonMenuItem[] stylePopupMenuItems = {
            new JRadioButtonMenuItem("默认      ", true),
            new JRadioButtonMenuItem("夜晚      "),
            new JRadioButtonMenuItem("粉色回忆      "),
            new JRadioButtonMenuItem("她      "),
            new JRadioButtonMenuItem("鹿鸣      "),
            new JRadioButtonMenuItem("你的名字      "),
            new JRadioButtonMenuItem("深海      "),
    };

    // 播放列表
    private JList<File> musicList = new JList<>();
    private JScrollPane musicScrollPane = new JScrollPane(musicList);
    private DefaultListModel musicListModel = new DefaultListModel<>();
    // 音乐右键弹出菜单
    private JPopupMenu musicPopupMenu = new JPopupMenu();
    // 右键菜单：播放
    private JMenuItem playMenuItem = new JMenuItem(PLAY_MENU_ITEM_TEXT);
    // 右键菜单：删除
    private JMenuItem removeMenuItem = new JMenuItem(REMOVE_MENU_ITEM_TEXT);

    // 歌词列表
    private JList<Statement> lrcList = new JList<>();
    private JScrollPane lrcScrollPane = new JScrollPane(lrcList);
    private DefaultListModel lrcListModel = new DefaultListModel<>();

    private JButton playOrPauseButton = new JButton(playIcon);
    private JButton lastButton = new JButton(lastIcon);
    private JButton nextButton = new JButton(nextIcon);
    // 播放模式切换按钮
    private JButton playModeButton = new JButton(sequenceIcon);
    private JButton forwardButton = new JButton(FORWARD_BUTTON_TEXT);
    private JButton backwardButton = new JButton(BACKWARD_BUTTON_TEXT);
    private JSlider volumeSlider = new JSlider();

    // 全局 Panel
    private GlobalPanel globalPanel = new GlobalPanel();
    // 控制面板 Panel
    private JPanel controlPanel = new JPanel();
    // 进度条 Panel
    private JPanel processPanel = new JPanel();

    // 左部工具条和播放列表盒子
    private Box leftBox = new Box(BoxLayout.Y_AXIS);
    // 底部进度条和控制面板盒子
    private Box bottomBox = new Box(BoxLayout.Y_AXIS);
    // 右部专辑和歌词盒子
    private Box rightBox = new Box(BoxLayout.Y_AXIS);
    // 右上专辑和标签盒子
    private Box rightTopBox = new Box(BoxLayout.X_AXIS);
    // 右右上标签纵向排列盒子
    private Box rightRightTopBox = new Box(BoxLayout.Y_AXIS);

    // 歌词渲染线程
    private Thread lrcThread;
    // 监控音乐播放的线程
    private Thread playerStatusMonitorThread;
    // 频谱线程
    private Thread spectrumThread;

    public void initUI() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, AWTException, IOException {
        // 主界面
        setTitle(TITLE);
        globalPanel.setLayout(new BorderLayout());
        setIconImage(titleIcon.getImage());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);  // 设置关闭窗口不作任何事情，自行处理
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 询问隐藏到托盘还是退出程序
                int response = JOptionPane.showOptionDialog(
                        null,
                        ASK_DISPOSE_MSG,
                        TITLE,
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        EXIT_OPTIONS,
                        EXIT_OPTIONS[0]);
                if (response == JOptionPane.YES_OPTION) {
                    dispose();
                } else if (response == JOptionPane.NO_OPTION) {
                    // 移除托盘图标并退出
                    SystemTray.getSystemTray().remove(trayIcon);
                    System.exit(0);
                }
            }
        });
        setBounds(150, 100, 960, 600);

        // 初始化托盘
        trayInit();

        // 初始化菜单栏
        menuBarInit();

        // 初始化标签
        labelInit();

        // 初始化工具条
        toolBarInit();

        // 初始化歌单
        musicListInit();

        // 初始化歌词列表
        lrcListInit();

        // 初始化进度条
        barInit();

        // 初始化控制面板
        controlPanelInit();

        // 开启监控播放器状态
        loadMonitor();

        // 更新 LAF
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.updateComponentTreeUI(this);
        SwingUtilities.updateComponentTreeUI(globalPanel);

        // 初始化风格，一定要在更新 LAF 后，避免被其覆盖！
        changeUIStyle(styles[0]);

        add(globalPanel);
        setVisible(true);
    }

    // 初始化托盘
    void trayInit() throws AWTException {
        SystemTray systemTray = SystemTray.getSystemTray();
        // 显示图片必须设置
        trayIcon.setImageAutoSize(true);
        // 注意托盘菜单必须使用 awt 的
        PopupMenu trayPopupMenu = new PopupMenu();
        MenuItem openMainFrameMenuItem = new MenuItem("打开主界面");
        MenuItem exitMenuItem = new MenuItem("退出");
        openMainFrameMenuItem.addActionListener(e -> {
            // 从托盘还原窗口
            setExtendedState(NORMAL);
            setVisible(true);
        });
        exitMenuItem.addActionListener(e -> {
            // 移除托盘图标并退出
            SystemTray.getSystemTray().remove(trayIcon);
            System.exit(0);
        });
        trayPopupMenu.add(openMainFrameMenuItem);
        trayPopupMenu.addSeparator();
        trayPopupMenu.add(exitMenuItem);
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // 从托盘还原窗口
                    setExtendedState(NORMAL);
                    setVisible(true);
                }
            }
        });
        trayIcon.setPopupMenu(trayPopupMenu);
        systemTray.add(trayIcon);
    }

    // 初始化菜单栏
    void menuBarInit() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        fileMenuInit();
        playMenuInit();
        viewMenuInit();
        individuationMenuInit();
        helpMenuInit();
        menuBar.add(fileMenu);
        menuBar.add(playMenu);
        menuBar.add(viewMenu);
        menuBar.add(individuationMenu);
        menuBar.add(helpMenu);
        add(menuBar, BorderLayout.NORTH);
    }

    // 初始化“文件”菜单
    void fileMenuInit() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        fileMenu.setMnemonic('F');      // Alt + F

        // 初始化“打开文件”
        openFileInit();
        // 初始化“打开文件夹”
        openDirInit();
        // 初始化“退出”
        exitInit();

        fileMenu.add(open);
        fileMenu.add(openDir);
        fileMenu.addSeparator();
        fileMenu.add(exit);
    }

    // 打开文件
    void openFileInit() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        // Ctrl + O
        open.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_MASK));
        JFileChooser fileChooser = new JFileChooser();
        ExtensionFileFilter filter = new ExtensionFileFilter();
        String allSuffix = "";
        for (String suffix : Format.SUPPORTED) {
            filter.addExtension(suffix);
            allSuffix += "*." + suffix + ",";
        }
        filter.setDescription("音频文件("
                + allSuffix.substring(0, allSuffix.lastIndexOf(',')) + ")");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);          // 禁止 “所有文件”
        fileChooser.setMultiSelectionEnabled(true);             // 允许选择多个
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());    // 更新文件选择框的 LAF
        SwingUtilities.updateComponentTreeUI(fileChooser);
        open.addActionListener(e -> {
            int code = fileChooser.showDialog(globalPanel, "选择歌曲文件");
            if (code == JFileChooser.APPROVE_OPTION) {
                // 添加选中的多个不重复的文件
                File[] audioFiles = fileChooser.getSelectedFiles();
                for (File audioFile : audioFiles) {
                    if (!isDuplicate(audioFile) && audioFile.exists()) musicListModel.addElement(audioFile);
                }
                musicList.setModel(musicListModel);
            }
        });
        // 添加歌曲菜单项也是同一个监听器
        addFileMenuItem.addActionListener(open.getActionListeners()[0]);
    }

    // 打开歌曲文件夹
    void openDirInit() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        // Ctrl + F
        openDir.setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK));
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);        // 只允许选择文件夹
        fileChooser.setAcceptAllFileFilterUsed(false);
        ExtensionFileFilter filter = new ExtensionFileFilter();
        filter.setDescription("所有文件夹");
        fileChooser.setFileFilter(filter);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());    // 更新文件选择框的 LAF
        SwingUtilities.updateComponentTreeUI(fileChooser);
        openDir.addActionListener(e -> {
            int code = fileChooser.showDialog(globalPanel, "选择文件夹");
            if (code == JFileChooser.APPROVE_OPTION) {
                File dir = fileChooser.getSelectedFile();
                // 文件夹不存在直接跳出
                if (!dir.exists()) return;
                // 播放列表不为空时，询问是否保留原播放列表
                if (musicListModel.getSize() != 0) {
                    int response = JOptionPane.showConfirmDialog(globalPanel,
                            ASK_CLEAR_MUSIC_LIST_MSG,
                            TITLE,
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (response == JOptionPane.NO_OPTION) {
                        // 先卸载当前文件，再清空播放列表
                        if (player.getStatus() != PlayerStatus.EMPTY) unload();
                        musicListModel.clear();
                    }
                }
                File[] files = dir.listFiles();
                int count = 0, audioFileCount = 0;
                for (File file : files) {
                    // 支持这种文件格式并且不重复才添加
                    if (player.support(file.getName().substring(file.getName().lastIndexOf('.') + 1))) {
                        audioFileCount++;
                        if (!isDuplicate(file)) {
                            musicListModel.addElement(file);
                            count++;
                        }
                    }
                }
                String msg = "成功添加 " + count + " 个文件";
                if (count < audioFileCount) msg += "，有 " + (audioFileCount - count) + " 个文件重复";
                JOptionPane.showMessageDialog(globalPanel,
                        msg,
                        TITLE,
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        // 添加歌曲文件夹菜单项也是同一个监听器
        addDirMenuItem.addActionListener(openDir.getActionListeners()[0]);
    }

    // 初始化退出
    void exitInit() {
        exit.setAccelerator(KeyStroke.getKeyStroke('W', InputEvent.CTRL_MASK)); // Ctrl + W
        exit.addActionListener(e -> {
            System.exit(0);
        });
    }

    // 初始化播放菜单
    void playMenuInit() {
        playMenu.setMnemonic('P');      // Alt + P
        playMenu.add(forwardOrBackward);
        playMenu.addSeparator();
        forwardOrBackwardButtonGroup.add(s5);
        forwardOrBackwardButtonGroup.add(s10);
        forwardOrBackwardButtonGroup.add(s30);
        forwardOrBackward.add(s5);
        forwardOrBackward.add(s10);
        forwardOrBackward.add(s30);
        s5.addActionListener(e -> forwardOrBackwardTime = 5);
        s10.addActionListener(e -> forwardOrBackwardTime = 10);
        s30.addActionListener(e -> forwardOrBackwardTime = 30);

        playMenu.add(playMode);
        playMenu.addSeparator();
        playModeButtonGroup.add(single);
        playModeButtonGroup.add(sequence);
        playModeButtonGroup.add(shuffle);
        playMode.add(single);
        playMode.add(sequence);
        playMode.add(shuffle);
        // 菜单切换播放模式，按钮图标切换
        single.addActionListener(e -> changeToSingle());
        sequence.addActionListener(e -> changeToSequence());
        shuffle.addActionListener(e -> changeToShuffle());

        playMenu.add(playRate);
        playMenu.addSeparator();
        playRateButtonGroup.add(rate1);
        playRateButtonGroup.add(rate2);
        playRateButtonGroup.add(rate3);
        playRateButtonGroup.add(rate4);
        playRate.add(rate1);
        playRate.add(rate2);
        playRate.add(rate3);
        playRate.add(rate4);
//        rate1.addActionListener(e -> audioPlayer.setRate(currRate = 0.5f));
//        rate2.addActionListener(e -> audioPlayer.setRate(currRate = 1f));
//        rate3.addActionListener(e -> audioPlayer.setRate(currRate = 1.5f));
//        rate4.addActionListener(e -> audioPlayer.setRate(currRate = 2f));
    }

    // 初始化视图菜单
    void viewMenuInit() {
        viewMenu.setMnemonic('V');      // Alt + V
        spectrumMenuItem.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK));
        spectrumMenuItem.addActionListener(e -> {
            if (spectrumMenuItem.isSelected()) {
                int status = player.getStatus();
                if (status != PlayerStatus.EMPTY
                        && status != PlayerStatus.LOADED
                        && (spectrumThread == null
                        || !spectrumThread.isAlive())) {
                    // 启动频谱线程
                    spectrumThread = new Thread(() -> {
                        updateSpectrum();
                        lrcList.repaint();
                    });
                    spectrumThread.start();
                }
            } else {
                if (spectrumThread != null) {
                    spectrumThread.interrupt();
                }
            }
        });
        viewMenu.add(spectrumMenuItem);
    }

    // 初始化个性化菜单
    void individuationMenuInit() {
        individuationMenu.setMnemonic('I');     // Alt + I
        for (int i = 0; i < styleMenuItems.length; i++) {
            int finalI = i;
            styleMenuItems[i].addActionListener(e -> {
                try {
                    changeUIStyle(styles[finalI]);
                    stylePopupMenuItems[finalI].setSelected(true);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (IllegalAccessException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                } catch (InstantiationException instantiationException) {
                    instantiationException.printStackTrace();
                } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                    unsupportedLookAndFeelException.printStackTrace();
                } catch (ClassNotFoundException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                }
            });
            styleButtonGroup.add(styleMenuItems[i]);
            styleMenu.add(styleMenuItems[i]);
        }
        individuationMenu.add(styleMenu);
    }

    // 初始化帮助菜单
    void helpMenuInit() {
        helpMenu.setMnemonic('H');      // Alt + H
        helpMenuItem.setAccelerator(KeyStroke.getKeyStroke('H', InputEvent.CTRL_MASK));     // Ctrl + H
        helpMenuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    globalPanel,
                    HELP_MSG,
                    TITLE,
                    JOptionPane.INFORMATION_MESSAGE);
        });
        helpMenu.add(helpMenuItem);
    }

    // 初始化标签
    void labelInit() {
        // 保存专辑图片事件
        saveAlbumImageMenuItem.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setAcceptAllFileFilterUsed(false);
            // 添加可保存的图片格式
            for (String suffix : Format.IMAGE_TYPE_SUPPORTED) {
                ExtensionFileFilter filter = new ExtensionFileFilter();
                filter.addExtension(suffix);
                filter.setDescription("." + suffix);
                chooser.addChoosableFileFilter(filter);
            }
            int code = chooser.showSaveDialog(globalPanel);
            if (code == JFileChooser.APPROVE_OPTION) {
                String suffix = chooser.getFileFilter().getDescription();
                String path = chooser.getSelectedFile().getPath();
                // 去除多余的后缀名
                int point = path.lastIndexOf('.');
                if (point != -1) path = path.substring(0, point);
                File outputFile = new File(path + suffix);
                // 文件已存在，询问是否覆盖
                if (outputFile.exists()) {
                    int response = JOptionPane.showConfirmDialog(
                            globalPanel,
                            ASK_OVERWRITE_FIFE_MSG,
                            TITLE,
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    // 不覆盖就跳出
                    if (response == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
                // 取出 ImageIcon 转为 BufferedImage 输出(不需要保留透明度，否则 JPEG JPG 格式输出不了)
                ImageIcon albumIcon = player.getMusicInfo().getAlbum();
                BufferedImage bufferedImage = ImageUtils.castImageIconToBuffedImage(albumIcon);
                try {
                    ImageIO.write(bufferedImage, suffix.substring(1), outputFile);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        albumImagePopupMenu.add(saveAlbumImageMenuItem);
        albumImage.add(albumImagePopupMenu);
        albumImage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    ImageIcon albumIcon = player.getMusicInfo().getAlbum();
                    if (albumIcon != null) {
                        albumImagePopupMenu.show(albumImage, e.getX(), e.getY());
                    }
                }
            }
        });
        songNameLabel.setFont(globalFont);
        artistLabel.setFont(globalFont);
        albumLabel.setFont(globalFont);
        // 添加右上的标签
        rightRightTopBox.add(Box.createVerticalGlue());
        rightRightTopBox.add(songNameLabel);
        rightRightTopBox.add(Box.createVerticalGlue());
        rightRightTopBox.add(artistLabel);
        rightRightTopBox.add(Box.createVerticalGlue());
        rightRightTopBox.add(albumLabel);
        rightRightTopBox.add(Box.createVerticalGlue());
        // 专辑图片和右边所有标签整体
        rightTopBox.add(Box.createHorizontalGlue());        // 创建胶水使其填充位置(居中的方法)
        rightTopBox.add(albumImage);
        rightTopBox.add(Box.createHorizontalGlue());
        rightTopBox.add(rightRightTopBox);
        rightTopBox.add(Box.createHorizontalGlue());
        // 歌词上面整体
        rightBox.add(rightTopBox);
    }

    // 初始化工具栏
    void toolBarInit() {
        // 按钮去掉周围的虚线框
        addToolButton.setFocusPainted(false);
        removeToolButton.setFocusPainted(false);
        sortToolButton.setFocusPainted(false);
        styleToolButton.setFocusPainted(false);
        addPopupMenu.add(addFileMenuItem);
        addPopupMenu.add(addDirMenuItem);
        // 按钮绑定右键菜单，不过右键也会弹出
        addToolButton.setComponentPopupMenu(addPopupMenu);
        // 点击添加按钮事件
        addToolButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    addPopupMenu.show(musicToolBar, e.getX(), e.getY());
                }
            }
        });
        // 点击删除按钮事件
        removeToolButton.addActionListener(e -> {
            List<File> selectedFiles = musicList.getSelectedValuesList();
            if (selectedFiles.size() != 0) {
                int response = JOptionPane.showConfirmDialog(
                        globalPanel,
                        ASK_REMOVE_FIFE_MSG,
                        TITLE,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                // 删除选中的文件
                if (response == JOptionPane.YES_OPTION) {
                    for (File file : selectedFiles) {
                        if (player.isPlayingFile(file)) unload();
                        musicListModel.removeElement(file);
                    }
                }
            }
        });
        // 按名称排序
        sortByNameMenuItem.addActionListener(e -> {
            sortFiles(SortMethod.BY_NAME);
        });
        // 按时长排序
        sortByTimeMenuItem.addActionListener(e -> {
            sortFiles(SortMethod.BY_TIME);
        });
        // 按大小排序
        sortBySizeMenuItem.addActionListener(e -> {
            sortFiles(SortMethod.BY_SIZE);
        });
        sortButtonGroup.add(sortByNameMenuItem);
        sortButtonGroup.add(sortByTimeMenuItem);
        sortButtonGroup.add(sortBySizeMenuItem);
        sortPopupMenu.add(sortByNameMenuItem);
        sortPopupMenu.add(sortByTimeMenuItem);
        sortPopupMenu.add(sortBySizeMenuItem);
        // 按钮绑定右键菜单，不过右键也会弹出
        sortToolButton.setComponentPopupMenu(sortPopupMenu);
        // 点击排序按钮事件
        sortToolButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    sortPopupMenu.show(musicToolBar, e.getX(), e.getY());
                }
            }
        });
        for (int i = 0; i < stylePopupMenuItems.length; i++) {
            int finalI = i;
            stylePopupMenuItems[i].addActionListener(e -> {
                try {
                    changeUIStyle(styles[finalI]);
                    styleMenuItems[finalI].setSelected(true);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (IllegalAccessException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                } catch (InstantiationException instantiationException) {
                    instantiationException.printStackTrace();
                } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
                    unsupportedLookAndFeelException.printStackTrace();
                } catch (ClassNotFoundException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                }
            });
            stylePopupMenuButtonGroup.add(stylePopupMenuItems[i]);
            stylePopupMenu.add(stylePopupMenuItems[i]);
        }
        styleToolButton.setComponentPopupMenu(stylePopupMenu);
        // 换肤按钮事件
        styleToolButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    stylePopupMenu.show(musicToolBar, e.getX(), e.getY());
                }
            }
        });
        // 帮助提示
        addToolButton.setToolTipText(ADD_TIP);
        removeToolButton.setToolTipText(REMOVE_TIP);
        sortToolButton.setToolTipText(SORT_TIP);
        styleToolButton.setToolTipText(STYLE_TIP);
        // 绘制工具栏边框
        musicToolBar.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        // 不可浮动
        musicToolBar.setFloatable(false);
        musicToolBar.add(addToolButton);
        musicToolBar.add(removeToolButton);
        musicToolBar.add(sortToolButton);
        // 加胶水让工具栏左对齐
        musicToolBar.add(Box.createHorizontalGlue());
        // 换肤按钮在最右边
        musicToolBar.add(styleToolButton);
        leftBox.add(musicToolBar);
    }

    // 初始化歌单
    void musicListInit() {
        // 设置音乐列表渲染样式：默认
//        DefaultMusicListRenderer musicListRenderer = new DefaultMusicListRenderer(globalFont);
//        musicList.setCellRenderer(musicListRenderer);
        musicList.setModel(musicListModel);
        // 只能单选
        musicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        musicList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 鼠标左键双击播放
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    playSelected();
                }
                // 鼠标右键弹出菜单
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    // 得到鼠标光标所在的选项并选中
                    int index = musicList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        musicList.setSelectedIndex(index);
                        musicPopupMenu.show(musicList, e.getX(), e.getY());
                    }
                }
            }
        });
        // 右键菜单播放
        playMenuItem.addActionListener(e -> {
            playSelected();
        });
        // 右键菜单删除
        removeMenuItem.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(globalPanel,
                    ASK_REMOVE_FIFE_MSG,
                    TITLE,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                // 如果该文件正在播放，先卸载当前文件
                if (player.isPlayingFile(musicList.getSelectedValue())) unload();
                int index = musicList.getSelectedIndex();
                // 删除文件后，当前歌曲索引调整
                if (index == currSong) currSong = -1;
                else if (index < currSong) currSong -= 1;
                musicListModel.remove(index);
            }
        });
        musicPopupMenu.add(playMenuItem);
        musicPopupMenu.addSeparator();
        musicPopupMenu.add(removeMenuItem);
        musicList.add(musicPopupMenu);
        // 滚动条
        musicScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        musicScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        // 歌单最佳大小
        musicScrollPane.setPreferredSize(new Dimension(260, 200));
        leftBox.add(musicScrollPane);
        globalPanel.add(leftBox, BorderLayout.WEST);
    }

    // 初始化歌词列表
    void lrcListInit() {
        // 设置歌词列表渲染样式：默认
//        DefaultLrcListRenderer lrcListRenderer = new DefaultLrcListRenderer();
//        lrcListRenderer.setBackgroundColor(currUIStyle.getHighlightColor());
//        lrcListRenderer.setDefaultFont(globalFont);
//        lrcListRenderer.setHorizontalAlignment(SwingConstants.CENTER);
//        lrcList.setCellRenderer(lrcListRenderer);
        // 不能选择
        lrcList.setEnabled(false);
        // 绑定数据 Model
        lrcList.setModel(lrcListModel);
        // 滚动条(不显示滚动条)
        lrcScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        lrcScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        // 歌词面板最佳大小(JList 需要加到 JScrollPane 中才能调整大小！)
        lrcScrollPane.setPreferredSize(new Dimension(460, 300));
        rightBox.add(lrcScrollPane);
        globalPanel.add(rightBox, BorderLayout.CENTER);
    }

    // 初始化进度条
    void barInit() {
        currTimeLabel.setFont(globalFont);
        durationLabel.setFont(globalFont);
        Box processBox = new Box(BoxLayout.X_AXIS);
        processBox.add(currTimeLabel);
        processBox.add(timeBar);
        processBox.add(durationLabel);
        timeBar.setMinimum(TIME_BAR_MIN);
        timeBar.setMaximum(TIME_BAR_MAX);
        timeBar.setValue(TIME_BAR_MIN);
        // 设置进度条最佳大小
        timeBar.setPreferredSize(new Dimension(500, 30));
        new Thread(() -> {
            while (true) {
                // 随着播放，设置进度条和时间标签的值
                try {
                    timeBar.setValue((int) (player.getCurrScale() * TIME_BAR_MAX));
                    currTimeLabel.setText(player.getCurrTimeString());
                } catch (NullPointerException e) {

                }
//                if (audioPlayer != null) {
//                    timeBar.setValue((int) (audioPlayer.getCurrScale() * TIME_BAR_MAX));
//                    currTimeLabel.setText(audioPlayer.getCurrTime());
//                }
            }
        }).start();
        processPanel.add(processBox);
        bottomBox.add(processPanel);
    }

    // 初始化控制面板
    void controlPanelInit() {
        playOrPauseButton.addActionListener(e -> {
            switch (player.getStatus()) {
                // 空状态，载入选择的音乐并播放
                case PlayerStatus.EMPTY:
                    playSelected();
                    break;
                // 就绪状态
                case PlayerStatus.LOADED:
                    player.play();
                    playOrPauseButton.setIcon(ImageUtils.dye(pauseIcon, currUIStyle.getButtonColor()));
                    playOrPauseButton.setToolTipText(PAUSE_TIP);
                    break;
                // 暂停状态
                case PlayerStatus.PAUSING:
                    player.continuePlay();
                    playOrPauseButton.setIcon(ImageUtils.dye(pauseIcon, currUIStyle.getButtonColor()));
                    playOrPauseButton.setToolTipText(PAUSE_TIP);
                    break;
                // 播放状态
                case PlayerStatus.PLAYING:
                    try {
                        player.pause();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    playOrPauseButton.setIcon(ImageUtils.dye(playIcon, currUIStyle.getButtonColor()));
                    playOrPauseButton.setToolTipText(PLAY_TIP);
                    break;
            }
        });
        lastButton.setToolTipText(LAST_TIP);
        lastButton.addActionListener(e -> {
            playLast();
        });
        nextButton.setToolTipText(NEXT_TIP);
        nextButton.addActionListener(e -> {
            playNext();
        });
        // 播放模式切换事件
        playModeButton.addActionListener(e -> {
            if (sequence.isSelected()) {
                changeToShuffle();
            } else if (shuffle.isSelected()) {
                changeToSingle();
            } else if (single.isSelected()) {
                changeToSequence();
            }
        });
        // 默认提示语为“顺序播放”
        playModeButton.setToolTipText(SEQUENCE_TIP);
//        backwardButton.addActionListener(e -> {
//            switch (audioPlayer.getStatus()) {
//                case Player.Prefetched:
//                case Player.Started:
//                    audioPlayer.backward(forwardOrBackwardTime);
//            }
//        });
//        forwardButton.addActionListener(e -> {
//            try {
//                if (s5.isSelected()) {
//                    player.forward(5);
//                } else if (s10.isSelected()) {
//                    player.forward(10);
//                } else if (s30.isSelected()) {
//                    player.forward(30);
//                }
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        });
        // 音量调节滑动条
        volumeSlider.setValue(DEFAULT_VOLUME);
        volumeSlider.addChangeListener(e -> {
            currVolume = volumeSlider.getValue();
            player.setVolume((float) currVolume / MAX_VOLUME);
        });
        lastButton.setFont(globalFont);
        playOrPauseButton.setFont(globalFont);
        nextButton.setFont(globalFont);
        backwardButton.setFont(globalFont);
        forwardButton.setFont(globalFont);
        controlPanel.add(lastButton);
        controlPanel.add(playOrPauseButton);
        controlPanel.add(nextButton);
        controlPanel.add(playModeButton);
//        controlPanel.add(backwardButton);
//        controlPanel.add(forwardButton);
        controlPanel.add(volumeSlider);
        bottomBox.add(controlPanel);
        globalPanel.add(bottomBox, BorderLayout.SOUTH);
    }

    // 准备播放，初始化播放器和 UI
    void prepareToPlay(File file) throws IOException, UnsupportedAudioFileException, ReadOnlyFileException, TagException, InvalidAudioFrameException, CannotReadException {
        // 将播放中的音乐打断
        if (player.getStatus() == PlayerStatus.PLAYING) {
            player.interrupt();
            // 停止只是设置播放器状态，需要等待播放线程结束
            while (player.getPlayThread().isAlive()) {

            }
        }
        player.load(file);
        UILoad(file);
        clearLrc();
        loadLrc(file);
        // 初始化音量
        player.setVolume((float) currVolume / MAX_VOLUME);
    }

    // 卸载当前文件
    void unload() {
        player.unload();
        // 关闭频谱线程
        if(spectrumThread != null && spectrumThread.isAlive()) {
            spectrumThread.interrupt();
            lrcScrollPane.repaint();
        }
        UIUnload();
        clearLrc();
    }

    // 界面加载新文件
    void UILoad(File file) {
        // 重置标题
        setTitle(TITLE + "(" + file.getPath() + ")");
        // 重置当前文件路径
        currAudioFilePath = file.getPath();
        // 重置当前文件路径标签
        currAudioFileLabel.setText(CURR_AUDIO_FILE_LABEL + currAudioFilePath);
        // 重置当前播放时间
        currTimeLabel.setText(player.getCurrTimeString());
        // 重置总时间
        durationLabel.setText(player.getDurationString());
        // 重置为“播放”
        playOrPauseButton.setIcon(ImageUtils.dye(playIcon, currUIStyle.getButtonColor()));
        playOrPauseButton.setToolTipText(PLAY_TIP);

        // 加载专辑图片
        loadAlbumImage();
        // 设置歌曲名称
        songNameLabel.setText(SONG_NAME_LABEL + player.getMusicInfo().getName());
        // 设置艺术家
        artistLabel.setText(ARTIST_LABEL + player.getMusicInfo().getArtist());
        // 设置专辑名称
        albumLabel.setText(ALBUM_NAME_LABEL + player.getMusicInfo().getAlbumName());
    }

    // 界面关闭文件
    void UIUnload() {
        // 重置标题
        setTitle(TITLE);
        // 重置当前文件路径
        currAudioFilePath = "无";
        // 重置当前文件路径标签
        currAudioFileLabel.setText(CURR_AUDIO_FILE_LABEL + currAudioFilePath);
        // 重置当前播放时间
        currTimeLabel.setText(DEFAULT_TIME);
        // 重置总时间
        durationLabel.setText(DEFAULT_TIME);
        // 重置为“播放”
        playOrPauseButton.setIcon(ImageUtils.dye(playIcon, currUIStyle.getButtonColor()));
        playOrPauseButton.setToolTipText(PLAY_TIP);

        // 卸载专辑图片
        albumImage.setIcon(null);
        // 设置歌曲名称
        songNameLabel.setText("");
        // 设置艺术家
        artistLabel.setText("");
        // 设置专辑名称
        albumLabel.setText("");
    }

    // 加载专辑图片
    void loadAlbumImage() {
        ImageIcon icon = player.getMusicInfo().getAlbum();
        if (icon != null) {
            // 专辑图片显示原本大小图片的一个缩小副本
            ImageIcon newIcon = new ImageIcon();
            newIcon.setImage(icon.getImage().getScaledInstance(
                    ALBUM_IMAGE_WIDTH,
                    ALBUM_IMAGE_HEIGHT,
                    Image.SCALE_DEFAULT));
            albumImage.setIcon(newIcon);
        } else {
            albumImage.setIcon(
                    ImageUtils.dye(
                            new ImageIcon(SimplePath.ICON_PATH + "album.png"),
                            currUIStyle.getButtonColor()
                    )
            );
        }
    }

    // 清空歌词
    void clearLrc() {
        // 设置歌词线程中断，但并未中断，而在线程内用 isInterrupted 判断进行操作
        if (lrcThread != null) {
            lrcThread.interrupt();
            while (lrcThread.isAlive()) {

            }
        }
        lrcListModel.clear();
    }

    // 加载歌词(如果有)
    void loadLrc(File file) {
        String lrcPath = file.getPath().substring(0, file.getPath().lastIndexOf('.') + 1) + "lrc";
        LrcData lrcData = null;
        try {
            lrcData = new LrcData(lrcPath);
            Vector<Statement> statements = lrcData.getStatements();
            // 添加空白充数
            for (int i = 0; i < LRC_INDEX; i++) statements.add(0, new Statement(0, " "));
            for (Statement stmt : statements) {
                if (!stmt.getLyric().equals("")) lrcListModel.addElement(stmt);
            }
            lrcList.repaint();
            // 监听并更新歌词
            lrcThread = new Thread(() -> {
                int index = LRC_INDEX - 1;
                ListCellRenderer<? super Statement> lrcListRenderer = lrcList.getCellRenderer();
                if (lrcListRenderer instanceof DefaultLrcListRenderer) {
                    DefaultLrcListRenderer defaultLrcListRenderer
                            = (DefaultLrcListRenderer) lrcListRenderer;
                    defaultLrcListRenderer.setRow(index);
                    lrcList.setCellRenderer(defaultLrcListRenderer);
                } else if (lrcListRenderer instanceof TranslucentLrcListRenderer) {
                    TranslucentLrcListRenderer translucentLrcListRenderer
                            = (TranslucentLrcListRenderer) lrcListRenderer;
                    translucentLrcListRenderer.setRow(index);
                    lrcList.setCellRenderer(translucentLrcListRenderer);
                }
                while (true) {
                    // 最后一句歌词或线程中断，不再更新
                    if (index + 1 >= lrcListModel.getSize() || lrcThread.isInterrupted()) return;
                    // 判断是否该高亮下一行歌词
//                    System.out.println(player.getCurrTime() + " "+((Statement) lrcListModel.get(index + 1)).getTime());
                    if (player.getCurrTime() >= ((Statement) lrcListModel.get(index + 1)).getTime()) {
                        // 删除第一行歌词，整体往上移
                        lrcListModel.remove(0);
                    }
                }
            });
            lrcThread.start();
        }
        // 无歌词或歌词文件损坏
        catch (IOException e) {
            ListCellRenderer<? super Statement> lrcListRenderer = lrcList.getCellRenderer();
            if (lrcListRenderer instanceof DefaultLrcListRenderer) {
                DefaultLrcListRenderer defaultLrcListRenderer
                        = (DefaultLrcListRenderer) lrcListRenderer;
                defaultLrcListRenderer.setRow(-1);
                lrcList.setCellRenderer(defaultLrcListRenderer);
            } else if (lrcListRenderer instanceof TranslucentLrcListRenderer) {
                TranslucentLrcListRenderer translucentLrcListRenderer
                        = (TranslucentLrcListRenderer) lrcListRenderer;
                translucentLrcListRenderer.setRow(-1);
                lrcList.setCellRenderer(translucentLrcListRenderer);
            }
            // 添加空白充数
            for (int i = 0; i < LRC_INDEX - 1; i++) lrcListModel.addElement(new Statement(0, " "));
            lrcListModel.addElement(new Statement(0, NO_LRC_MSG));
        }
    }

    // 开启监控播放器状态的线程
    void loadMonitor() {
        playerStatusMonitorThread = new Thread(() -> {
            int degree = 1;
            while (true) {
                interval();
                switch (player.getStatus()) {
                    // 监听播放结束自动切换下一曲
                    case PlayerStatus.STOPPED:
                        // 单曲循环
                        if (single.isSelected()) {
                            playSelected();
                        }
                        // 顺序播放
                        else if (sequence.isSelected()) {
                            playNext();
                        }
                        // 随机播放
                        else if (shuffle.isSelected()) {
                            // 随机列表播放完了就生成一个
                            if (shuffleIndex >= shuffleList.size()) {
                                generateShuffleList();
                            }
                            // 播放下一个随机
                            playNextShuffle();
                        }
                        break;
                    // 播放时加载默认专辑图片旋转动画
                    case PlayerStatus.PLAYING:
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ImageIcon icon = player.getMusicInfo().getAlbum();
                        if (icon == null) {
                            ImageIcon imgIcon = ImageUtils.dye(
                                    new ImageIcon(SimplePath.ICON_PATH + "album.png"),
                                    currUIStyle.getButtonColor());
                            BufferedImage bImageTranslucent = ImageUtils.castImageIconToBuffedImageTranslucent(imgIcon);
                            BufferedImage rotated = null;
                            try {
                                rotated = Thumbnails.of(bImageTranslucent)
                                        .rotate(degree)
                                        .size(ALBUM_IMAGE_WIDTH, ALBUM_IMAGE_HEIGHT)
                                        .asBufferedImage();
                                degree = (degree + 1) % 360;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ImageIcon rotatedImageIcon
                                    = new ImageIcon(rotated.getSubimage(
                                    (rotated.getWidth() - ALBUM_IMAGE_WIDTH) / 2,
                                    (rotated.getHeight() - ALBUM_IMAGE_HEIGHT) / 2,
                                    ALBUM_IMAGE_WIDTH,
                                    ALBUM_IMAGE_HEIGHT)
                            );
                            albumImage.setIcon(rotatedImageIcon);
                            // 解决卸载文件后还在继续渲染默认专辑文件的问题
                            if (player.getStatus() == PlayerStatus.EMPTY) albumImage.setIcon(null);
                        }
                        break;
                }
            }
        });
        playerStatusMonitorThread.start();
    }

    // 播放选中歌曲
    void playSelected() {
        File file = musicList.getSelectedValue();
        // 根本没选歌曲，跳出
        if (file == null) return;
        // 文件不存在，询问是否从列表中删除
        if (!file.exists()) {
            int response = JOptionPane.showConfirmDialog(globalPanel,
                    ASK_REMOVE_FIFE_NOT_FOUND_MSG,
                    TITLE,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                musicListModel.remove(musicList.getSelectedIndex());
            }
            return;
        }
        currSong = musicList.getSelectedIndex();
        try {
            prepareToPlay(file);
            player.play();
            if (spectrumMenuItem.isSelected()) {
                // 先中断上一个频谱线程
                if (spectrumThread != null && spectrumThread.isAlive()) {
                    spectrumThread.interrupt();
                }
                // 启动频谱线程
                spectrumThread = new Thread(() -> {
                    updateSpectrum();
                    lrcScrollPane.repaint();
                });
                spectrumThread.start();
            }
            // 重绘歌单，刷新播放中的图标
            musicList.repaint();
            playOrPauseButton.setIcon(ImageUtils.dye(pauseIcon, currUIStyle.getButtonColor()));
            playOrPauseButton.setToolTipText(PAUSE_TIP);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (UnsupportedAudioFileException unsupportedAudioFileException) {
            unsupportedAudioFileException.printStackTrace();
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        }
    }

    // 播放上一曲
    void playLast() {
        // 当前有歌曲被选中
        if (currSong != -1) {
            int size = musicListModel.getSize();
            // 选中上一曲
            currSong = currSong - 1 < 0 ? size - 1 : currSong - 1;
            musicList.setSelectedIndex(currSong);
            playSelected();
        }
    }

    // 播放下一曲 / 顺序播放
    void playNext() {
        // 当前有歌曲被选中
        if (currSong != -1) {
            // 选中下一曲
            currSong = (currSong + 1) % musicListModel.getSize();
            musicList.setSelectedIndex(currSong);
            playSelected();
        }
    }

    // 生成随机播放序列
    void generateShuffleList() {
        // 随机列表为空或者歌曲数量发生变化
        if (shuffleList.isEmpty() || shuffleList.size() != musicListModel.getSize()) {
            shuffleList.clear();
            for (int i = 0; i < musicListModel.getSize(); i++) shuffleList.add(i);
        }
        Collections.shuffle(shuffleList);
        shuffleIndex = 0;
    }

    // 播放随机列表的下一首
    void playNextShuffle() {
        // 选择随机列表下一首
        musicList.setSelectedIndex(shuffleList.get(shuffleIndex++));
        playSelected();
    }

    // 改变到单曲循环
    void changeToSingle() {
        single.setSelected(true);
        playModeButton.setIcon(ImageUtils.dye(singleIcon, currUIStyle.getButtonColor()));
        playModeButton.setToolTipText(SINGLE_TIP);
    }

    // 改变到顺序播放
    void changeToSequence() {
        sequence.setSelected(true);
        playModeButton.setIcon(ImageUtils.dye(sequenceIcon, currUIStyle.getButtonColor()));
        playModeButton.setToolTipText(SEQUENCE_TIP);
    }

    // 变为随机播放
    void changeToShuffle() {
        shuffle.setSelected(true);
        playModeButton.setIcon(ImageUtils.dye(shuffleIcon, currUIStyle.getButtonColor()));
        playModeButton.setToolTipText(SHUFFLE_TIP);
    }

    // 列表排序
    void sortFiles(int method) {
        List<File> list = Collections.list(musicListModel.elements());
        if (method == SortMethod.BY_NAME) {
            Collections.sort(list, Comparator.comparing(File::getName));
        } else if (method == SortMethod.BY_TIME) {
            Collections.sort(list, (f1, f2) -> {
                double d1;
                double d2;
                try {
                    d1 = MusicUtils.getDuration(f1);
                    d2 = MusicUtils.getDuration(f2);
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
                return d1 - d2 < 0 ? -1 : 1;
            });
        } else if (method == SortMethod.BY_SIZE) {
            Collections.sort(list, Comparator.comparing(File::length));
        }
        musicListModel.removeAllElements();
        list.forEach(file -> {
            musicListModel.addElement(file);
            // 修改当前正在播放的歌曲的索引
            if (player.isPlayingFile(file)) currSong = musicListModel.getSize() - 1;
        });
    }

    // 更新频谱，在多线程中使用
    void updateSpectrum() {
        while (true) {
            if (!interval()) return;
            LinkedList<Double> deque = player.getDeque();
            // 音频数据达到频谱数量才显示频谱
            if (deque.size() >= SpectrumConstants.SPECTRUM_TOTAL_NUMBER) {
                Double[] data = FFTUtils.listToArray(deque);
                int lrcScrollPaneWidth = lrcScrollPane.getWidth(), lrcScrollPaneHeight = lrcScrollPane.getHeight();
                // 获取歌词列表相对于窗口的坐标
                Point pointLrcList = SwingUtilities.convertPoint(lrcList,
                        lrcList.getX(), lrcList.getY(), this);
                int lrcX = pointLrcList.x, lrcY = pointLrcList.y;
                BufferedImage bufferedImage = new BufferedImage(
                        lrcScrollPaneWidth, lrcScrollPaneHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = bufferedImage.createGraphics();
                // 获取透明的 BufferedImage
                BufferedImage bImageTranslucent
                        = g.getDeviceConfiguration().createCompatibleImage(
                        lrcScrollPaneWidth, lrcScrollPaneHeight, Transparency.TRANSLUCENT);
                g.dispose();
                g = bImageTranslucent.createGraphics();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g.setColor(currUIStyle.getSpectrumColor());
                for (int i = 0, length = data.length; i < length; i++) {
                    // 得到频谱高度并绘制
                    int sHeight = (int) Math.abs(data[i]);
                    g.fillRect(
                            i * SpectrumConstants.SPECTRUM_WIDTH,
                            lrcScrollPaneHeight - sHeight,
                            SpectrumConstants.SPECTRUM_WIDTH,
                            sHeight
                    );
                }
                lrcScrollPane.repaint();
                // 使重绘之后再画频谱，repaint 不是立即重绘的！
                try {
                    int imgX = lrcX + (lrcScrollPaneWidth - SpectrumConstants.SPECTRUM_WIDTH * SpectrumConstants.SPECTRUM_TOTAL_NUMBER) / 2;
                    int imgY = lrcY - 55;
                    SwingUtilities.invokeAndWait(() -> {
                        globalPanel.getGraphics().drawImage(
                                bImageTranslucent,
                                imgX,
                                imgY,
                                null);
                    });
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return;
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                // 暂停就暂时不绘制频谱，线程等待
                if (player.getStatus() != PlayerStatus.PLAYING) {
                    try {
                        MySemaphore.semaphore.acquire();
                        MySemaphore.semaphore.release();
                    } catch (InterruptedException e) {
                        // 暂停时被打断播放，直接跳出，否则会卡死频谱线程
                        return;
                    }
                }
                // 绘制频谱线程中断
                if (spectrumThread.isInterrupted()) {
                    return;
                }
            }
        }
    }

    // 改变 UI 风格
    void changeUIStyle(UIStyle style) throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        if (style == currUIStyle) return;
        boolean opaque = style.getOpaque();
        // 工具栏消除边框和透明
        if (style.getStyleType() != UIStyleConstants.NORMAL) {
            musicToolBar.setBorder(null);
        } else {
            // 恢复默认的工具栏边框
            musicToolBar.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }
        addToolButton.setOpaque(opaque);
        removeToolButton.setOpaque(opaque);
        sortToolButton.setOpaque(opaque);
        styleToolButton.setOpaque(opaque);
        // 工具栏按钮颜色
        addToolButton.setIcon(ImageUtils.dye((ImageIcon) addToolButton.getIcon(), style.getButtonColor()));
        removeToolButton.setIcon(ImageUtils.dye((ImageIcon) removeToolButton.getIcon(), style.getButtonColor()));
        sortToolButton.setIcon(ImageUtils.dye((ImageIcon) sortToolButton.getIcon(), style.getButtonColor()));
        styleToolButton.setIcon(ImageUtils.dye((ImageIcon) styleToolButton.getIcon(), style.getButtonColor()));
        musicToolBar.setOpaque(opaque);
        // 播放列表透明
        if (style.getStyleType() != UIStyleConstants.NORMAL) {
            TranslucentMusicListRenderer musicListRenderer = new TranslucentMusicListRenderer(globalFont, player);
            musicListRenderer.setForeColor(style.getForeColor());
            musicListRenderer.setSelectedColor(style.getSelectedColor());
            musicList.setCellRenderer(musicListRenderer);
        } else {
            DefaultMusicListRenderer defaultMusicListRenderer = new DefaultMusicListRenderer(globalFont, player);
            musicList.setCellRenderer(defaultMusicListRenderer);
        }
        musicList.setOpaque(opaque);
        // 注意滚动条透明的写法！
        musicScrollPane.getVerticalScrollBar().setOpaque(opaque);
        musicScrollPane.getHorizontalScrollBar().setOpaque(opaque);
        musicScrollPane.setOpaque(opaque);
        musicScrollPane.getViewport().setOpaque(opaque);
        if (style.getStyleType() != UIStyleConstants.NORMAL) {
            musicScrollPane.getHorizontalScrollBar().setUI(new ScrollBarUI(style.getScrollBarColor()));
            musicScrollPane.getVerticalScrollBar().setUI(new ScrollBarUI(style.getScrollBarColor()));
            // 歌单滚动面板消除边框
            musicScrollPane.setBorder(null);
        }
        // 歌词列表透明
        // 设置 JLabel 禁用状态字体颜色，即歌词列表禁用状态字体颜色
        UIManager.put("Label.disabledForeground", style.getLrcColor());
        // 恢复歌词高亮显示
        int lastRow = 0;
        ListCellRenderer<? super Statement> lastLrcListRenderer = lrcList.getCellRenderer();
        if (lastLrcListRenderer instanceof DefaultLrcListRenderer) {
            lastRow = ((DefaultLrcListRenderer) lastLrcListRenderer).getRow();
        } else if (lastLrcListRenderer instanceof TranslucentLrcListRenderer) {
            lastRow = ((TranslucentLrcListRenderer) lastLrcListRenderer).getRow();
        }
        if (style.getStyleType() != UIStyleConstants.NORMAL) {
            TranslucentLrcListRenderer lrcListRenderer = new TranslucentLrcListRenderer();
            lrcListRenderer.setBackgroundColor(style.getHighlightColor());  // 高亮颜色
            lrcListRenderer.setRow(lastRow);
            lrcListRenderer.setDefaultFont(globalFont);
            lrcListRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            lrcList.setCellRenderer(lrcListRenderer);
            lrcList.setUI(new ListUI(style.getHighlightColor(), LRC_INDEX - 1));  // 歌词禁用字体透明，需要用到自定义 List
        } else {
            DefaultLrcListRenderer lrcListRenderer = new DefaultLrcListRenderer();
            lrcListRenderer.setBackgroundColor(style.getHighlightColor());
            lrcListRenderer.setRow(lastRow);
            lrcListRenderer.setDefaultFont(globalFont);
            lrcListRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            lrcList.setCellRenderer(lrcListRenderer);
        }
        lrcList.setOpaque(opaque);
        lrcScrollPane.setOpaque(opaque);
        lrcScrollPane.getViewport().setOpaque(opaque);
        lrcScrollPane.getHorizontalScrollBar().setOpaque(opaque);
        lrcScrollPane.getVerticalScrollBar().setOpaque(opaque);
        if (style.getStyleType() != UIStyleConstants.NORMAL) {
            // 歌词滚动面板消除边框
            lrcScrollPane.setBorder(null);
        }
        // 进度条和控制面板透明
        timeBar.setOpaque(opaque);
        if (style.getStyleType() != UIStyleConstants.NORMAL) {
            timeBar.setBorder(new RoundBorder(style.getTimeBarColor()));
            timeBar.setUI(new ProcessBarUI(style.getTimeBarColor()));      // 自定义进度条 UI
        }
        // 时间标签用进度条的颜色
        currTimeLabel.setForeground(style.getTimeBarColor());
        durationLabel.setForeground(style.getTimeBarColor());
        processPanel.setOpaque(opaque);
        lastButton.setOpaque(opaque);
        lastButton.setContentAreaFilled(opaque);
        playOrPauseButton.setOpaque(opaque);
        playOrPauseButton.setContentAreaFilled(opaque);
        nextButton.setOpaque(opaque);
        nextButton.setContentAreaFilled(opaque);
        playModeButton.setOpaque(opaque);
        playModeButton.setContentAreaFilled(opaque);
        backwardButton.setOpaque(opaque);
        backwardButton.setContentAreaFilled(opaque);
        forwardButton.setOpaque(opaque);
        forwardButton.setContentAreaFilled(opaque);
        // 按钮图标颜色
        lastButton.setIcon(ImageUtils.dye((ImageIcon) lastButton.getIcon(), style.getButtonColor()));
        playOrPauseButton.setIcon(ImageUtils.dye((ImageIcon) playOrPauseButton.getIcon(), style.getButtonColor()));
        nextButton.setIcon(ImageUtils.dye((ImageIcon) nextButton.getIcon(), style.getButtonColor()));
        playModeButton.setIcon(ImageUtils.dye((ImageIcon) playModeButton.getIcon(), style.getButtonColor()));
//        backwardButton.setIcon(ImageUtils.dye((ImageIcon) backwardButton.getIcon(), style.getButtonColor()));
//        forwardButton.setIcon(ImageUtils.dye((ImageIcon) forwardButton.getIcon(), style.getButtonColor()));
        volumeSlider.setOpaque(opaque);
        if (style.getStyleType() != UIStyleConstants.NORMAL) {
            volumeSlider.setUI(new SliderUI(volumeSlider, style.getSliderColor(), style.getSliderColor()));
        }
        controlPanel.setOpaque(opaque);
        // 设置该风格自己的按钮样式
        if (style.getStyleType() != UIStyleConstants.NORMAL) {
            lastButton.setUI(new ButtonUI(lastIcon, style.getButtonColor()));
            playOrPauseButton.setUI(new ButtonUI(
                    player.getStatus() == PlayerStatus.PLAYING ? pauseIcon : playIcon, style.getButtonColor()));
            nextButton.setUI(new ButtonUI(nextIcon, style.getButtonColor()));
            playModeButton.setUI(new ButtonUI(sequenceIcon, style.getButtonColor()));
        }
        // 默认样式就用系统自带的
        else {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
            SwingUtilities.updateComponentTreeUI(globalPanel);
        }
        // 其他标签颜色
        Color labelColor = style.getLabelColor();
        songNameLabel.setForeground(labelColor);
        artistLabel.setForeground(labelColor);
        albumLabel.setForeground(labelColor);
        // 切换风格，包含背景图
        String styleImgPath = style.getStyleImgPath();
        if (styleImgPath == null || styleImgPath.equals("")) {
            globalPanel.setBackgroundImage(null);
        } else {
            BufferedImage styleImage = ImageIO.read(new File(styleImgPath));
            globalPanel.setBackgroundImage(styleImage);
        }
        currUIStyle = style;
        repaint();
    }

    // 判断歌曲文件是否已在当前列表中
    boolean isDuplicate(File file) {
        Object[] files = musicListModel.toArray();
        for (Object f : files) {
            if (((File) f).getPath().equals(file.getPath())) {
                return true;
            }
        }
        return false;
    }

    // 线程间隔时间(避免线程一直抢占资源导致运行异常！)
    boolean interval() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, IOException, JavaLayerException, AWTException {
        new PlayerFrame().initUI();
    }
}
