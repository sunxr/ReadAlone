package com.github.baby.owspace.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.github.baby.owspace.R;

public class LunarDialog extends Dialog {

    private Context context;

    public LunarDialog(@NonNull Context context) {
        super(context, R.style.LunarDialg);
        setCanceledOnTouchOutside(true);
        this.context = context;
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }
}
