package com.java.cuiyikai.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.java.cuiyikai.R;
import com.java.cuiyikai.network.RequestBuilder;
import com.java.cuiyikai.utilities.DensityUtilities;
import com.java.cuiyikai.utilities.PermissionUtilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PointExtractActivity extends AppCompatActivity {

    private static final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private static final int TAKE_PHOTO = 101;
    private static final int TAKE_CAMERA = 100;

    private Uri imageUri;

    private ImageView photoView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            switch(requestCode) {
                case TAKE_PHOTO: {
                    try {
                        if(imageUri == null) {
                            Bundle bundle = data.getExtras();
                            bitmap = (Bitmap) bundle.get("data");
                        }
                        else
                            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        photoView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
                case TAKE_CAMERA:
                    try {
                        imageUri = data.getData();
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        photoView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException | NullPointerException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            if(bitmap == null)
                return;
            TessBaseAPI baseAPI = new TessBaseAPI();
            File tessdata = new File(getFilesDir(), "tessdata");
            if(!tessdata.exists()) {
                tessdata.mkdirs();
                InputStream inputStream = getResources().openRawResource(R.raw.chi_sim);
                File file = new File(tessdata, "chi_sim.traineddata");
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1)
                        outputStream.write(buffer, 0, len);
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            baseAPI.init(getFilesDir().getAbsolutePath(), "chi_sim");
            baseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);
            baseAPI.setImage(bitmap);
            String result = baseAPI.getUTF8Text();
            baseAPI.end();
            System.out.printf("result:%s%n",result);
            EditText editText = (EditText) findViewById(R.id.extract_text_input);
            editText.setText(result);
            onTextReceived(result);
        }
    }

    private void onTextReceived(String text) {
        TextView result = (TextView) findViewById(R.id.extract_result);
        Map<String,String> args = new HashMap<>();
        args.put("context", text);
        args.put("course", "");
        JSONObject res;
        try {
            res = RequestBuilder.sendPostRequest("typeOpen/open/linkInstance", args);
        } catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return;
        }
        System.out.println("requesting!!");
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(text);
        JSONArray data = res.getJSONObject("data").getJSONArray("results");
        System.out.println(data.toString());
        for(Object keyword: data) {
            JSONObject obj = JSON.parseObject(keyword.toString());
            int L = obj.getInteger("start_index");
            int R = obj.getInteger("end_index");
            String name = obj.getString("entity");
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    System.out.println("clicked!!!");
                    Intent f=new Intent(PointExtractActivity.this,EntityActivity.class);
                    f.putExtra("name",name);
                    System.out.println(name);
                    f.putExtra("subject","");
                    startActivity(f);
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                    ds.setColor(Color.rgb(0x66,0xcc,0xff));
                }
            };
            ssBuilder.setSpan(clickableSpan, L, R+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        result.setMovementMethod(LinkMovementMethod.getInstance());
        result.setText(ssBuilder);
        result.setVisibility(View.VISIBLE);
        TextView resultTag = (TextView) findViewById(R.id.result_tag);
        resultTag.setVisibility(View.VISIBLE);
        Button uploadButton = (Button) findViewById(R.id.btn_upload);
        uploadButton.setText("再搜一次");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_extract);
        Button uploadButton = (Button) findViewById(R.id.btn_upload);
        photoView = (ImageView) findViewById(R.id.submit_photo);
        uploadButton.setOnClickListener((View view) -> {
            EditText editText = (EditText) findViewById(R.id.extract_text_input);
            PointExtractActivity.this.onTextReceived(editText.getText().toString());
        });
        Dialog bottomDialog = new Dialog(PointExtractActivity.this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_camera, null);
        bottomDialog.setContentView(contentView);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtilities.dp2px(this, 16f);
        params.bottomMargin = DensityUtilities.dp2px(this, 8f);
        contentView.setLayoutParams(params);
        Button takePhoto = (Button) contentView.findViewById(R.id.take_photo);
        Button fromAlbum = (Button) contentView.findViewById(R.id.from_album);
        Button camera = (Button) findViewById(R.id.camera);
        camera.setOnClickListener((View view) -> bottomDialog.show());
        takePhoto.setOnClickListener((View view) -> {
            if(PermissionUtilities.verifyPermissions(PointExtractActivity.this, Manifest.permission.CAMERA) == 0) {
                ActivityCompat.requestPermissions(PointExtractActivity.this, PERMISSIONS, 3);
                return;
            }
            File outputImage = new File(getFilesDir(), System.currentTimeMillis() + ".jpg");
            if(outputImage.exists())
                outputImage.delete();
            try {
                outputImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageUri = FileProvider.getUriForFile(PointExtractActivity.this, "com.java.cuiyikai.fileprovider", outputImage);
            Intent photoIntent = new Intent("android.media.action.IMAGE_CAPTURE");
            photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(photoIntent, TAKE_PHOTO);
            bottomDialog.dismiss();
        });
        fromAlbum.setOnClickListener((View view) -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, TAKE_CAMERA);
            bottomDialog.dismiss();
        });
    }
}