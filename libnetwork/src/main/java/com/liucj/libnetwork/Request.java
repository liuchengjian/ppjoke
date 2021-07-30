package com.liucj.libnetwork;

import androidx.annotation.IntDef;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class Request<T, R extends Request> {
    protected String mUrl;
    protected HashMap<String, String> headers = new HashMap<>();
    protected HashMap<String, Object> params = new HashMap<>();
    public static final int CACHE_ONLY = 1;//只访问缓存
    public static final int CACHE_FIRST = 2;//先访问缓存，同时发起网络请求，成功后缓存到本地
    public static final int NET_ONLY = 3;//只访问网络
    public static final int NET_CACHE = 4;//先访问网络，成功后缓存到本地
    private String cacheKey;

    @IntDef({CACHE_ONLY, CACHE_FIRST, NET_ONLY, NET_CACHE})
    public @interface CacheStrategy {

    }

    public Request(String url) {
        //user/list,不带ip的URl
        mUrl = url;
    }

    public R addHeader(String key, String value) {
        headers.put(key, value);
        return (R) this;
    }

    public R addParam(String key, Object value) {
        //value只能是8种基本类型
        //判断是否是8种基本类型,通过反射得到
        Field field = null;
        try {
            field = value.getClass().getField("TYPE");
            Class clazz = (Class) field.get(null);
            if (clazz.isPrimitive()) {
                params.put(key, value);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (R) this;
    }

    public R cacheKey(String key) {
        this.cacheKey = key;
        return (R) this;
    }

    public void execute(JsonCallBack<T> callback) {
        getCall().enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ApiResponse<T> apiResponse = new ApiResponse<>();
                apiResponse.message = e.getMessage();
                callback.onError(apiResponse);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ApiResponse<T> apiResponse = parseResponse(response, callback);
                if (apiResponse.success) {
                    callback.onSuccess(apiResponse);
                } else {
                    callback.onError(apiResponse);
                }
            }

        });
    }

    private ApiResponse<T> parseResponse(Response response, JsonCallBack<T> callBack) {
        String message = null;
        int status = response.code();
        boolean success = response.isSuccessful();
        ApiResponse<T> result = new ApiResponse<>();
        Convert convert = ApiService.mConvert;
        try {
            String content = response.body().string();
            if (success) {
                if (callBack != null) {
                    ParameterizedType type = (ParameterizedType) callBack.getClass().getGenericSuperclass();
                    Type argument = type.getActualTypeArguments()[0];
                    result.body = (T) convert.convert(content, argument);
                }
            } else {
                message = content;
            }
        } catch (IOException e) {
            e.printStackTrace();
            message = e.getMessage();
            success = false;
        }
        result.success = success;
        result.status = status;
        result.message = message;
        return result;
    }

    public void execute() {

    }

    private Call getCall() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        addHeaders(builder);
        okhttp3.Request request = generateRequest(builder);
        Call call = ApiService.okHttpClient.newCall(request);
        return call;
    }

    protected abstract okhttp3.Request generateRequest(okhttp3.Request.Builder builder);

    private void addHeaders(okhttp3.Request.Builder builder) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
    }


}
