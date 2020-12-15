package com.model;

import com.component.MySemaphore;
import com.constant.Format;
import com.constant.PlayerStatus;
import com.constant.SpectrumConstants;
import com.model.MusicInfo;
import com.utils.CharsetUtils;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import lombok.Data;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@Data
public class MusicPlayer {
    // 当前载入的文件的信息
    private MusicInfo musicInfo;
    // 当前播放进度(秒)
    private float currTime;
    // 当前播放器状态
    private int status;
    // 播放器音量控制器
    private FloatControl volumeControl;
    // 播放器速率控制器
    private FloatControl rateControl;
    // 播放器消音控制器
    private FloatControl muteControl;
    // 播放器消音控制器
    private FloatControl balanceControl;
    // 播放器声像控制器
    private FloatControl panControl;
    // 当前播放线程
    private Thread playThread;

    // 音频流
    private AudioInputStream stream = null;
    private long bytesSkipped;
    // 音频格式
    private AudioFormat format = null;

    // 声音数据，用于获取频谱
    private LinkedList<Double> deque = new LinkedList<Double>();

//    public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
//        MusicPlayer player = new MusicPlayer();
//        player.load("C:\\Users\\我\\Desktop\\Niko Kaz - NGC 3_14 - Restore.wav");
//        player.play();
//    }

    public MusicPlayer() {
        status = PlayerStatus.EMPTY;
    }

    // 判断是否支持该格式
    public boolean support(String format) {
        for (String fmt: Format.SUPPORTED) {
            if(fmt.equals(format.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // 载入文件
    public void load(String source) throws IOException, UnsupportedAudioFileException, ReadOnlyFileException, TagException, InvalidAudioFrameException, CannotReadException {
        load(new File(source));
    }

    public void load(File source) throws IOException, UnsupportedAudioFileException, ReadOnlyFileException, TagException, InvalidAudioFrameException, CannotReadException {
        currTime = 0;
        initialMusicInfo(source);
        status = PlayerStatus.LOADED;
    }

    // 卸载当前文件
    public void unload() {
        currTime = 0;
        musicInfo.setFile(null);
        musicInfo.setDuration(0);
        musicInfo.setFormat(null);
        musicInfo.setName(null);
        musicInfo.setArtist(null);
        musicInfo.setAlbumName(null);
        musicInfo.setAlbum(null);
        status = PlayerStatus.EMPTY;
    }

    // 判断播放器是否正在播放某文件
    public boolean isPlayingFile(File file) {
        if(musicInfo == null || musicInfo.getFile() == null) return false;
        return musicInfo.getFile().equals(file);
    }

    // 初始化音频信息(pcm wav)
    private void initialMusicInfo(File source) throws IOException, UnsupportedAudioFileException, TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);

        if (musicInfo == null) musicInfo = new MusicInfo();
        // 文件
        musicInfo.setFile(source);
        // 音频格式
        musicInfo.setFormat(source.getName().substring(source.getName().lastIndexOf('.') + 1).toLowerCase());

        // MP3 文件的时长和其他信息
        if (musicInfo.getFormat().equals(Format.MP3)) {
            // 时长
            MP3File f = (MP3File) AudioFileIO.read(source);
            MP3AudioHeader audioHeader = (MP3AudioHeader) f.getAudioHeader();
            musicInfo.setDuration(audioHeader.getTrackLength());

            Tag tagV24 = f.getID3v2TagAsv24();
            // 先从 ID3v2 找信息
            if (tagV24 != null) {
                String nameOrigin = tagV24.getFirst(FieldKey.TITLE);
                String artistOrigin = tagV24.getFirst(FieldKey.ARTIST);
                String albumNameOrigin = tagV24.getFirst(FieldKey.ALBUM);
                String name = CharsetUtils.toGB2312(nameOrigin);
                String artist = CharsetUtils.toGB2312(artistOrigin);
                String albumName = CharsetUtils.toGB2312(albumNameOrigin);
                if (name.contains("?") || artist.contains("?") || albumName.contains("?")) {
                    name = nameOrigin;
                    artist = artistOrigin;
                    albumName = albumNameOrigin;
                }
                // 歌曲名称
                musicInfo.setName(name.equals("") ? "未知" : name);
                // 艺术家
                musicInfo.setArtist(artist.equals("") ? "未知" : artist);
                // 专辑
                musicInfo.setAlbumName(albumName.equals("") ? "未知" : albumName);
            } else {
                // ID3v2 没有信息，从 ID3v1 找信息，注意转码
                ID3v1Tag id3v1Tag = f.getID3v1Tag();
                if (id3v1Tag != null) {
                    String name = CharsetUtils.toGB2312(id3v1Tag.getFirst(FieldKey.TITLE));
                    String artist = CharsetUtils.toGB2312(id3v1Tag.getFirst(FieldKey.ARTIST));
                    String albumName = CharsetUtils.toGB2312(id3v1Tag.getFirst(FieldKey.ALBUM));
                    musicInfo.setName(name.equals("") ? "未知" : name);
                    musicInfo.setArtist(artist.equals("") ? "未知" : artist);
                    musicInfo.setAlbumName(albumName.equals("") ? "未知" : albumName);
                }
                // 都没有信息，则设为“未知”
                else {
                    musicInfo.setName("未知");
                    musicInfo.setArtist("未知");
                    musicInfo.setAlbumName("未知");
                }
            }

            // 获取 MP3 专辑图片
            AbstractID3v2Tag tag = f.getID3v2Tag();
            if (tag != null) {
                AbstractID3v2Frame frame = (AbstractID3v2Frame) tag.getFrame("APIC");
                if (frame != null) {
                    FrameBodyAPIC body = (FrameBodyAPIC) frame.getBody();
                    byte[] image = body.getImageData();
                    Image img = Toolkit.getDefaultToolkit().createImage(image, 0, image.length);
                    ImageIcon icon = new ImageIcon(img);
                    musicInfo.setAlbum(icon);
                } else {
                    musicInfo.setAlbum(null);
                }
            } else {
                musicInfo.setAlbum(null);
            }
        }
        // 其他类型的文件时长和其他信息
        else {
            AudioInputStream stream = AudioSystem.getAudioInputStream(source);
            AudioFormat format = stream.getFormat();
            musicInfo.setDuration(stream.getFrameLength() / format.getSampleRate());

            musicInfo.setName("未知");
            musicInfo.setArtist("未知");
            musicInfo.setAlbumName("未知");
            musicInfo.setAlbum(null);
        }
    }

    public void play() {
        status = PlayerStatus.PLAYING;
        playThread = new Thread(() -> {
            try {
                playSingle();
                // 如果播放器未被打断或者未卸载文件，则表示正常播放结束，变为停止状态
                if (status != PlayerStatus.INTERRUPTED
                        && status != PlayerStatus.EMPTY) status = PlayerStatus.STOPPED;
                // 清空音乐数据
                deque.clear();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        });
        playThread.start();
    }

    // 播放 MP3 WAV PCM
    private void playSingle() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        synchronized (this) {
            File file = musicInfo.getFile();
            if (!file.exists()) {
                throw new FileNotFoundException("文件不存在");
            }
            // 使用 mp3spi 解码 mp3 音频文件
            if (musicInfo.getFormat().equals(Format.MP3)) {
                MpegAudioFileReader mp = new MpegAudioFileReader();
                stream = mp.getAudioInputStream(file);              // 原始音频流
                AudioFormat baseFormat = stream.getFormat();        // 获得原始的 format
                // 设定输出格式为 pcm 格式的音频文件
                format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(), 16,
                        baseFormat.getChannels(), baseFormat.getChannels() * 2,
                        baseFormat.getSampleRate(), false);
                stream = AudioSystem.getAudioInputStream(format, stream);
            } else if (musicInfo.getFormat().equals(Format.FLAC)) {
                stream = AudioSystem.getAudioInputStream(file);
                format = stream.getFormat();
                if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                    format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), 16, format.getChannels(), format.getChannels() * 2, format.getSampleRate(), false);
                    stream = AudioSystem.getAudioInputStream(format, stream);
                }
            } else if (musicInfo.getFormat().equals(Format.PCM) || musicInfo.getFormat().equals(Format.WAV)) {
                stream = AudioSystem.getAudioInputStream(file);
                format = stream.getFormat();
            }

            DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                    format, AudioSystem.NOT_SPECIFIED);
            SourceDataLine line = null;
            try {
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();
                try {
                    // 得到音量控制器
                    volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                    // 得到速率控制器
                    rateControl = (FloatControl) line.getControl(FloatControl.Type.SAMPLE_RATE);
                } catch (Exception e) {
//                    e.printStackTrace();
                }
                byte[] buffer = new byte[4];
                int len;
                // 帧长度
                long frameLength = stream.getFrameLength();
                // 声道数量
                int channels = format.getChannels();
                // 样本率
                float rate = format.getSampleRate();
                // 播放总时长
                float duration = musicInfo.getFormat().equals(Format.MP3) ?
                        -2.2675737E-5f : musicInfo.getDuration();
                while ((len = stream.read(buffer)) > 0) {
                    // 暂停播放，线程挂起
                    if (status == PlayerStatus.PAUSING) {
                        wait();
                    }
                    // 打断播放或者文件被卸载，跳出
                    if (status == PlayerStatus.INTERRUPTED || status == PlayerStatus.EMPTY) {
                        break;
                    }
                    // 更新当前播放时间
                    currTime = duration * line.getLongFramePosition() / frameLength;
//                System.out.println(line.getLongFramePosition() + " " + frameLength + " " + currTime + " " + musicInfo.getDuration());
                    // 双声道
                    if(channels == 2) {
                        if(rate == 16) {
                            put((double) ((buffer[1] << 8) | buffer[0]));     // 左声道
//                            put((double) ((buffer[3] << 8) | buffer[2]));     // 右声道
                        } else {
                            put((double) buffer[1]);     // 左声道
                            put((double) buffer[3]);     // 左声道
                            //put((double) buffer[2]);//右声道
                            //put((double) buffer[4]);//右声道
                        }
                    }
                    // 单声道
                    else {
                        if(rate == 16) {
                            put((double) ((buffer[1] << 8) | buffer[0]));     // 左声道
                            put((double) ((buffer[3] << 8) | buffer[2]));     // 右声道
                        } else {
                            put((double) buffer[0]);     // 左声道
                            put((double) buffer[1]);     // 左声道
                            put((double) buffer[2]);     // 右声道
                            put((double) buffer[3]);     // 右声道
                        }
                    }
                    line.write(buffer, 0, len);
                }
                line.drain();
                line.stop();
                line.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stream.close();
            }
        }
    }

    // 暂停
    public void pause() throws InterruptedException {
        MySemaphore.semaphore.acquire();        // 处理频谱同步问题
        status = PlayerStatus.PAUSING;
    }

    // 继续，唤醒播放线程
    public void continuePlay() {
        synchronized (this) {
            MySemaphore.semaphore.release();        // 处理频谱同步问题
            status = PlayerStatus.PLAYING;
            notifyAll();
        }
    }

    // 打断播放
    public void interrupt() {
        playThread.interrupt();
        status = PlayerStatus.INTERRUPTED;
    }

    // 设置音量
    public void setVolume(float volume) {
        if (volumeControl == null) return;
//        float minimum = volumeControl.getMinimum();
        float minimum = -50;
        float maximum = volumeControl.getMaximum();
        volumeControl.setValue(minimum + volume * (maximum - minimum));
        System.out.println(volumeControl.getValue());
    }

    // 快进
    public void forward(int seconds) throws IOException {
        System.out.println((long) (format.getSampleRate()
                * format.getChannels()
                * format.getFrameSize() * seconds));
        bytesSkipped += stream.skip((long) (format.getSampleRate()
                * format.getChannels()
                * format.getFrameSize() * seconds));
    }

    // 设置播放速率
    public void setRate(float rate) {
        rateControl.setValue(rate);
    }

    // 获取当前进度比例
    public float getCurrScale() {
        return currTime / musicInfo.getDuration();
    }

    // 获取当前进度字符串
    public String getCurrTimeString() {
        return ((int) currTime / 60) + ":" + ((int) currTime % 60);
    }

    // 获取当前总时间字符串
    public String getDurationString() {
        float duration = musicInfo.getDuration();
        return ((int) duration / 60) + ":" + ((int) duration % 60);
    }

    // 将音频数据放入 deque
    private void put(Double v) {
        synchronized (deque) {
            deque.add(v);
            if (deque.size() > SpectrumConstants.SPECTRUM_TOTAL_NUMBER) {
                deque.removeFirst();
            }
        }
    }
}
