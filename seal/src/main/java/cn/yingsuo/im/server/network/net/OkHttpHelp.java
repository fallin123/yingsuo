package cn.yingsuo.im.server.network.net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.String.valueOf;

/**
 * Created by Daisl on 2017/1/22.
 */

public class OkHttpHelp {
    public static final MediaType JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    private static OkHttpHelp httpHelp;
    private Context context;
    private boolean showDialog = true;
   // private CommondDialog commondDialog;

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            if (null != commondDialog) {
//                commondDialog.dismiss();
//            }
        }
    };

    private OkHttpHelp(Context context) {
        this.context = context;
    }

    public static OkHttpHelp getInstance(Context context) {
        httpHelp = new OkHttpHelp(context);
        return httpHelp;
    }

    /**
     * 异步 Get方法
     */
    public void okHttp_asynchronousGet(String url) {
        try {
            Log.i("", "main thread id is " + Thread.currentThread().getId());
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // 注：该回调是子线程，非主线程
                    Log.i("", "callback thread id is " + Thread.currentThread().getId());
                    Log.i("", response.body().string());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void okHttp_postFromParameters(final String url, final String params, final INetWorkCallBack callBack) {
        Log.e("url", url);
        Log.e("提交参数", params.toString());
        if (showDialog) {
//            if (null == commondDialog) {
//                commondDialog = new CommondDialog(context);
//            }
//            commondDialog.show();

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 请求完整url：http://api.k780.com:88/?app=weather.future&weaid=1&&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody body = RequestBody.create(JSON, params);//new FormBody.Builder().;//.add("values",params).build();
                    // Log.e("--------","------------"+body.toString());
                    Request request = new Request.Builder().url(url).post(body).build();
                    Response response = okHttpClient.newCall(request).execute();
                    // Log.e("--------","--------request"+response.body().string());
                    final String resp = response.body().string();
                    Log.e("resp", "------------------------" + resp);
                    callBack.onNetWorkCallBack(resp);
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void postAsynFile(final String url, final File file, final Map<String, String> map, final INetWorkCallBack iNetWorkCallBack) {
        OkHttpClient client = new OkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (file != null) {
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            String filename = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("header_ico", filename, body);
        }
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
            }
        }
        Request request = new Request.Builder().url(url).post(requestBody.build()).tag(context).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("lfq", "onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String str = response.body().string();
                    iNetWorkCallBack.onNetWorkCallBack(str);
                    Log.i("lfq", response.message() + " , body " + str);

                } else {
                    Log.i("lfq", response.message() + " error : body " + response.body().string());
                }
            }
        });
    }

    public void postAsynFileList(final String url, final List<File> fileList, final Map<String, String> map, final INetWorkCallBack iNetWorkCallBack) {
        OkHttpClient client = new OkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (fileList != null) {
            for(File file:fileList){
                // MediaType.parse() 里面是上传的文件类型。
                RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
                String filename = file.getName();
                requestBody.addFormDataPart("header_ico", filename, body);
            }
        }
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
            }
        }
        Request request = new Request.Builder().url(url).post(requestBody.build()).tag(context).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("lfq", "onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String str = response.body().string();
                    iNetWorkCallBack.onNetWorkCallBack(str);
                    Log.i("lfq", response.message() + " , body " + str);

                } else {
                    Log.i("lfq", response.message() + " error : body " + response.body().string());
                }
            }
        });
    }

//    private void initOkHttpClient() {
//        File sdcache = context.getExternalCacheDir();
//        int cacheSize = 10 * 1024 * 1024;
//        OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                .connectTimeout(15, TimeUnit.SECONDS)
//                .writeTimeout(20, TimeUnit.SECONDS)
//                .readTimeout(20, TimeUnit.SECONDS)
//                .cache(new Cache(sdcache.getAbsoluteFile(), cacheSize));
//        mOkHttpClient = builder.build();
//    }
//
//    /**
//     * get异步请求
//     */
//    private void getAsynHttp() {
//
//        Request.Builder requestBuilder = new Request.Builder().url("http://www.baidu.com");
//        requestBuilder.method("GET", null);
//        Request request = requestBuilder.build();
//        Call mcall = mOkHttpClient.newCall(request);
//        mcall.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (null != response.cacheResponse()) {
//                    String str = response.cacheResponse().toString();
//                    Log.i("wangshu", "cache---" + str);
//                } else {
//                    response.body().string();
//                    String str = response.networkResponse().toString();
//                    Log.i("wangshu", "network---" + str);
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "请求成功", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }
//
//    /**
//     * post异步请求
//     */
//    private void postAsynHttp() {
//        RequestBody formBody = new FormBody.Builder()
//                .add("size", "10")
//                .build();
//        Request request = new Request.Builder()
//                .url("http://api.1-blog.com/biz/bizserver/article/list.do")
//                .post(formBody)
//                .build();
//        Call call = mOkHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String str = response.body().string();
//                Log.i("wangshu", str);
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "请求成功", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//        });
//    }
//
//    /**
//     * 异步上传文件
//     */
//    private void postAsynFile() {
//        File file = new File("/sdcard/wangshu.txt");
//        Request request = new Request.Builder()
//                .url("https://api.github.com/markdown/raw")
//                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
//                .build();
//        mOkHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.i("wangshu", response.body().string());
//            }
//        });
//    }
//
//
//    /**
//     * 异步下载文件
//     */
//    private void downAsynFile() {
//        String url = "http://img.my.csdn.net/uploads/201603/26/1458988468_5804.jpg";
//        Request request = new Request.Builder().url(url).build();
//        mOkHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) {
//                InputStream inputStream = response.body().byteStream();
//                FileOutputStream fileOutputStream = null;
//                try {
//                    fileOutputStream = new FileOutputStream(new File("/sdcard/wangshu.jpg"));
//                    byte[] buffer = new byte[2048];
//                    int len = 0;
//                    while ((len = inputStream.read(buffer)) != -1) {
//                        fileOutputStream.write(buffer, 0, len);
//                    }
//                    fileOutputStream.flush();
//                } catch (IOException e) {
//                    Log.i("wangshu", "IOException");
//                    e.printStackTrace();
//                }
//
//                Log.d("wangshu", "文件下载成功");
//            }
//        });
//    }
//
//    private void sendMultipart() {
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("title", "wangshu")
//                .addFormDataPart("image", "wangshu.jpg",
//                        RequestBody.create(MEDIA_TYPE_PNG, new File("/sdcard/wangshu.jpg")))
//                .build();
//
//        Request request = new Request.Builder()
//                .header("Authorization", "Client-ID " + "...")
//                .url("https://api.imgur.com/3/image")
//                .post(requestBody)
//                .build();
//
//        mOkHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.i("wangshu", response.body().string());
//            }
//        });
//    }
}
