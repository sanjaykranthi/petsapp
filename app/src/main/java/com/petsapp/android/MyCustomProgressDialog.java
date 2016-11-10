package com.petsapp.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

public class MyCustomProgressDialog extends ProgressDialog {

    private AnimationDrawable animation;

    public MyCustomProgressDialog(Context context) {
        super(context);
        //  this.context = context;
    }

    public MyCustomProgressDialog() {
        super(null);

    }

    public static ProgressDialog ctor(Context context) {
        MyCustomProgressDialog dialog = new MyCustomProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_custom_progress_dialog);

        ImageView la = (ImageView) findViewById(R.id.animation);
        la.setBackgroundResource(R.drawable.custom_progressbar);
        animation = (AnimationDrawable) la.getBackground();
    }

    @Override
    public void show() {
        super.show();
        animation.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        animation.stop();
    }
}
