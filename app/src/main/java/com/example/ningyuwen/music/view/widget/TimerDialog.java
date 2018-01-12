package com.example.ningyuwen.music.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RadioGroup;

/**
 * Created by money on 18-1-10.
 */

public class TimerDialog extends Dialog {
    private View contentView ;
    private RadioGroup.OnCheckedChangeListener RadioListener;


    public TimerDialog(@NonNull Context context, int themeResId, RadioGroup.OnCheckedChangeListener onCheckedChangeListener) {
        super(context, themeResId);

        RadioListener = onCheckedChangeListener;


    }
}
