package com.kson.fileupload;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;

import com.kson.fileupload.common.Api;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 点击上传文件
     *
     * @param view
     */
    public void upload(View view) {

        File file = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/kson/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            //此处的文件是你本地存储的文件，为了演示方便，写死
            file = new File(dir, "hi.jpg");//第一种获取文件写法
//            File file1  = new File(dir.getPath()+"t.jpg");//第二种获取文件方法
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("uid", "71");
        if (file != null) {
            params.put("file", file);
            System.out.println("filepath:" + file.getPath());
        }
        //上传文件
        upload(params);
        //获取用户信息
        getUserInfo();

    }


    /**
     * post请求方式一：string类型参数请求
     */
    public void getUserInfo() {
        //post请求方式一：application/x-www-form-urlencoded（只能上传string类型的参数，不能上传文件）
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("uid", "71");

        Request request = new Request.Builder().post(builder.build()).url(Api.GETUSERINFO).build();

       okHttpClient.newCall(request).enqueue(new Callback() {
           @Override
           public void onFailure(okhttp3.Call call, IOException e) {

           }

           @Override
           public void onResponse(okhttp3.Call call, Response response) throws IOException {
               if (response.isSuccessful()) {

                   System.out.println("getuserinfo:"+response.body().string());
               }
           }
       });
    }

    /**
     * post方式二：stirng类型参数和上传文件参数
     */
    public void upload(Map<String, Object> params) {
        //post请求方式二：multipart/form-data(不仅能够上传string类型的参数，还可以上传文件（流的形式，file）)
        OkHttpClient okHttpClient1 = new OkHttpClient();


        MultipartBody.Builder builder1 = new MultipartBody.Builder();
        builder1.setType(MultipartBody.FORM);
        for (Map.Entry<String, Object> stringObjectEntry : params.entrySet()) {
            String key = stringObjectEntry.getKey();
            Object value = stringObjectEntry.getValue();
            if (value instanceof File) {//如果请求的值是文件
                File file = (File) value;
                //MediaType.parse("application/octet-stream")以二进制的形式上传文件
                builder1.addFormDataPart(key, ((File) value).getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
            } else {//如果请求的值是string类型
                builder1.addFormDataPart(key, value.toString());
            }
        }


        Request request1 = new Request.Builder().post(builder1.build()).url(Api.UPLOAD).build();

        okHttpClient1.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {

                if (response.isSuccessful()){
                    System.out.println("fileuploadsuccess：" + response.body().string());
                }
            }
        });
    }
}
