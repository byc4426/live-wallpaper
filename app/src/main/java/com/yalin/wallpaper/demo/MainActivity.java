package com.yalin.wallpaper.demo;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yalin.wallpaper.demo.gl.AdvanceGLWallpaperService;
import com.yalin.wallpaper.demo.gl.SharePreferenceUtil;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.util.List;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        selectImage();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 100 && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            if (!TextUtils.isEmpty(pathList.get(0))) {
                Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
                options.size = 5;//设置压缩后大小 K
                Tiny.getInstance().source(pathList.get(0)).asFile().withOptions(options).compress(new FileCallback() {
                    @Override
                    public void callback(boolean isSuccess, String outfile) {
                        if (isSuccess) {
                            SharePreferenceUtil.setPath(MainActivity.this, outfile);
                            Intent intent = new Intent(
                                    WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                    new ComponentName(MainActivity.this, AdvanceGLWallpaperService.class));
                            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivityForResult(intent, 200);
                        }
                    }
                });
            }

        }
        Log.e("---", resultCode + "");
        if (requestCode == 200 && resultCode == RESULT_OK) {
            finish();
        }
        if (requestCode == 200 && resultCode != RESULT_OK) {
            selectImage();
        }
        if (requestCode == 100 && resultCode != RESULT_OK) {
            finish();
        }
    }

    private void selectImage() {
        ImageConfig imageConfig = new ImageConfig.Builder(
                // GlideLoader 可用自己用的缓存库
                new GlideLoader())
                // 如果在 4.4 以上，则修改状态栏颜色 （默认黑色）
//                .steepToolBarColor(getResources().getColor(R.color.blue_bg2))
                // 标题的背景颜色 （默认黑色）
//                .titleBgColor(getResources().getColor(R.color.blue_bg2))
                // 提交按钮字体的颜色  （默认白色）
//                .titleSubmitTextColor(getResources().getColor(R.color.white))
                // 标题颜色 （默认白色）
//                .titleTextColor(getResources().getColor(R.color.white))
                // 开启多选   （默认为多选）  (单选 为 singleSelect)
                .singleSelect()
//                .crop()
                // 多选时的最大数量
//                .mutiSelectMaxSize(5)
                // 已选择的图片路径
//                .pathList(path)
                // 拍照后存放的图片路径（默认 /temp/picture）
//                .filePath("/ImageSelector/Pictures")
                // 开启拍照功能 （默认开启）
//                .showCamera()
                .requestCode(100)
                .build();
        ImageSelector.open(this, imageConfig);
    }

    public class GlideLoader implements com.yancy.imageselector.ImageLoader {

        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Glide.with(context)
                    .load(path)
                    .placeholder(com.yancy.imageselector.R.mipmap.imageselector_photo)
                    .centerCrop()
                    .into(imageView);

        }

    }

}
