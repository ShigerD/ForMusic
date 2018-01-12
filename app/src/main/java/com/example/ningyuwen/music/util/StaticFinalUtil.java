package com.example.ningyuwen.music.util;

/**
 * 保存常量值
 * Created by ningyuwen on 17-11-2.
 */

public class StaticFinalUtil {
    public static final int MUSIC_FILE_PERMISSION = 1001;  //权限
    public static final int HANDLER_ACTIVITY_LYRIC = 1002;  //歌词
    public static final int HANDLER_REFRESH_MUSIC = 1003;   //初始化加载和重新加载时使用
    public static final String RECEIVER_CLOSE_APP = "close_app";      //接收广播，用于关闭App
    public static final int HANDLER_SHOW_CUSTOM = 1004;     //显示通知栏
    public static final int HANDLER_SHOW_DISC_ROTATION = 1005;  //DISC的旋转
    public static final int SERVICE_PLAY_TYPE_LIST = 1006;  //列表循环
    public static final int SERVICE_PLAY_TYPE_SINGLE = 1007;  //单曲循环
    public static final int SERVICE_PLAY_TYPE_RANDOM = 1008;  //随机播放
    public static int SERVICE_PLAY_TYPE_NOW = 1009;  //当前播放模式

}
