package com.java.cuiyikai.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

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
import com.java.cuiyikai.activities.EntityActivity;
import com.java.cuiyikai.network.RequestBuilder;
import com.java.cuiyikai.utilities.ConstantUtilities;
import com.java.cuiyikai.utilities.DensityUtilities;
import com.java.cuiyikai.utilities.PermissionUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PointExtractFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PointExtractFragment extends Fragment {

    private static final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private static final Logger logger = LoggerFactory.getLogger(PointExtractFragment.class);

    private static final int TAKE_PHOTO = 101;
    private static final int TAKE_CAMERA = 100;

    private Uri imageUri;

    private ImageView photoView;

    public PointExtractFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            switch(requestCode) {
                case TAKE_PHOTO: {
                    try {
                        if(imageUri == null) {
                            Bundle bundle = data.getExtras();
                            bitmap = (Bitmap) bundle.get(ConstantUtilities.ARG_DATA);
                        }
                        else
                            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                        photoView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
                case TAKE_CAMERA:
                    try {
                        imageUri = data.getData();
                        bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
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
            File tessdata = new File(getActivity().getFilesDir(), "tessdata");
            if(!tessdata.exists()) {
                tessdata.mkdirs();
                InputStream inputStream = getResources().openRawResource(R.raw.chi_sim);
                File file = new File(tessdata, "chi_sim.traineddata");
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1)
                        outputStream.write(buffer, 0, len);
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            baseAPI.init(getActivity().getFilesDir().getAbsolutePath(), "chi_sim");
            baseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);
            baseAPI.setImage(bitmap);
            String result = baseAPI.getUTF8Text();
            baseAPI.end();
            logger.info("result: {}",result);
            EditText editText = getView().findViewById(R.id.extract_text_input);
            editText.setText(result);
            onTextReceived(result);
        }
    }

    private void onTextReceived(String text) {
        TextView result = getView().findViewById(R.id.extract_result);
        Map<String,String> args = new HashMap<>();
        args.put("context", text);
        args.put(ConstantUtilities.ARG_COURSE, "");
        JSONObject res;
        try {
            res = RequestBuilder.sendPostRequest("typeOpen/open/linkInstance", args);
        } catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return;
        }
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(text);
        JSONArray data = res.getJSONObject(ConstantUtilities.ARG_DATA).getJSONArray("results");
        for(Object keyword: data) {
            JSONObject obj = JSON.parseObject(keyword.toString());
            int startIndex = obj.getInteger("start_index");
            int endIndex = obj.getInteger("end_index");
            String name = obj.getString("entity");
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    Intent f=new Intent(getActivity(), EntityActivity.class);
                    f.putExtra(ConstantUtilities.ARG_NAME,name);
                    f.putExtra(ConstantUtilities.ARG_SUBJECT,"");
                    startActivity(f);
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                    ds.setColor(Color.rgb(0x66,0xcc,0xff));
                }
            };
            ssBuilder.setSpan(clickableSpan, startIndex, endIndex+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        result.setMovementMethod(LinkMovementMethod.getInstance());
        result.setText(ssBuilder);
        result.setVisibility(View.VISIBLE);
        TextView resultTag = getView().findViewById(R.id.result_tag);
        resultTag.setVisibility(View.VISIBLE);
        Button uploadButton = getView().findViewById(R.id.btn_upload);
        uploadButton.setText("再搜一次");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PointExtractFragment.
     */
    public static PointExtractFragment newInstance() {
        PointExtractFragment fragment = new PointExtractFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_point_extract, container, false);

        Button uploadButton = view.findViewById(R.id.btn_upload);
        photoView = view.findViewById(R.id.submit_photo);
        uploadButton.setOnClickListener((View v) -> {
            EditText editText = view.findViewById(R.id.extract_text_input);
            onTextReceived(editText.getText().toString());
        });
        Dialog bottomDialog = new Dialog(getActivity(), R.style.BottomDialog);
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_camera, null);
        bottomDialog.setContentView(contentView);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtilities.dp2px(getActivity(), 16f);
        params.bottomMargin = DensityUtilities.dp2px(getActivity(), 8f);
        contentView.setLayoutParams(params);
        Button takePhoto = contentView.findViewById(R.id.take_photo);
        Button fromAlbum = contentView.findViewById(R.id.from_album);
        Button camera = view.findViewById(R.id.camera);
        camera.setOnClickListener((View v) -> bottomDialog.show());
        takePhoto.setOnClickListener((View v) -> {
            if(PermissionUtilities.verifyPermissions(getActivity(), Manifest.permission.CAMERA) == 0) {
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 3);
                return;
            }
            File outputImage = new File(getActivity().getFilesDir(), System.currentTimeMillis() + ".jpg");
            if(outputImage.exists())
                outputImage.delete();
            try {
                outputImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageUri = FileProvider.getUriForFile(getActivity(), "com.java.cuiyikai.fileprovider", outputImage);
            Intent photoIntent = new Intent("android.media.action.IMAGE_CAPTURE");
            photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(photoIntent, TAKE_PHOTO);
            bottomDialog.dismiss();
        });
        fromAlbum.setOnClickListener((View v) -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, TAKE_CAMERA);
            bottomDialog.dismiss();
        });
        return view;
    }
}