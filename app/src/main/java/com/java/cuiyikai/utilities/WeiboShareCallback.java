package com.java.cuiyikai.utilities;

import android.content.Context;
import android.widget.Toast;

import com.sina.weibo.sdk.common.UiError;
import com.sina.weibo.sdk.share.WbShareCallback;

public class WeiboShareCallback implements WbShareCallback {

    private final Context context;

    public WeiboShareCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onComplete() {
        Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(UiError uiError) {
        Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {
        Toast.makeText(context, "分享取消", Toast.LENGTH_SHORT).show();
    }
}
