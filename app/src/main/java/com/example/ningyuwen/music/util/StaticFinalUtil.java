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
    //发送广播更新数据和布局，主要在播放页面,主要是更换了歌单之后的一些操作
    public static String SERVICE_RECEIVE_REFRESH_MUSICLIST = "refresh_music_data";

    //此常量用于表示当前播放的歌单位置：
//    public static final int MUSIC_LIST_PLAY_NOW = 1010;
    public static final int MUSIC_LIST_PLAY_ALL_MUSIC = 1011;           //所有音乐
    public static final int MUSIC_LIST_PLAY_CISTOM_LIST = 1012;         //自定义歌单
    public static final int MUSIC_LIST_PLAY_MYLOVE = 1013;              //我喜爱的
    public static final int MUSIC_LIST_PLAY_CLASSIFY_PLAYER = 1014;     //歌手分类


}
