package com.example.ningyuwen.music.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.ningyuwen.music.R;

/**
 * 自定义dialog，用于长时间任务加载时的显示，例如扫描音乐
 * Created by ningyuwen on 18-1-13.
 */

public class MusicProgressDialog extends Dialog {

    public MusicProgressDialog(@NonNull Context context) {
        super(context);
    }

    public MusicProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MusicProgressDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_music_progress_dialog);
    }
}
