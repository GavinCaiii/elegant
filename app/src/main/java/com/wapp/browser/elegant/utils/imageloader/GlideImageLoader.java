package com.wapp.browser.elegant.utils.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.EmptySignature;
import com.wapp.browser.elegant.AppContext;
import com.wapp.browser.elegant.manager.ThreadManager;

import java.io.File;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * @className: GlideImageLoader
 * @classDescription: 针对Glide load封装
 * @Author: allen
 * @createTime: 2018/3/12.
 */
public class GlideImageLoader {

    private static final String ANDROID_RESOURCE = "android.resource://";
    private static final String FILE = "file://";
    private static final String SEPARATOR = "/";
//    private static final String HTTP = "http";

    private ImageView mImageView;

    private static final long MAX_CACHE_SIZE = 250 * 1024 * 1024;

    public static GlideImageLoader create(ImageView imageView) {

        return new GlideImageLoader(imageView);
    }

    public static boolean isCacheFileExist(Context context, String url) {
        // 寻找缓存图片
        if (context == null || TextUtils.isEmpty(url)) {
            return false;
        }
        File file = DiskLruCacheWrapper.create(Glide.getPhotoCacheDir(context), MAX_CACHE_SIZE).get(new OriginalKey(url, EmptySignature.obtain()));
        if (file == null) {
            return false;
        }
        return file.exists();
    }

    private GlideImageLoader(ImageView imageView) {

        mImageView = imageView;
    }

    private ImageView getImageView() {
        if (mImageView != null) {
            return mImageView;
        }
        return null;
    }

    private Context getContext() {
        return AppContext.getInstance().getApplicationContext();
    }

    /**
     * 获取本地资源uri
     *
     * @param resourceId 本地资源
     * @return
     */
    private Uri resId2Uri(int resourceId) {
        if (getContext() == null) return null;
        return Uri.parse(ANDROID_RESOURCE + getContext().getPackageName() + SEPARATOR + resourceId);
    }

    /**
     * 加载本地资源
     *
     * @param resId   本地资源
     * @param options 图片配置器
     */
    private void load(int resId, RequestOptions options) {
        load(resId2Uri(resId), options);
    }

    /**
     * 加载图片
     *
     * @param uri     图片uri格式
     * @param options 图片配置器
     */
    private void load(Uri uri, RequestOptions options) {
        if (getContext() == null && getImageView() == null) return;
        requestBuilder(uri, options, null).into(getImageView());
    }

    /**
     * 加载网络图片
     *
     * @param url     图片地址
     * @param options 图片配置器
     */
    public void load(String url, RequestOptions options) {
        if (getContext() == null && getImageView() == null) return;
        requestBuilder(url, options, null).into(getImageView());
    }

    /**
     * 加载网络图片
     *
     * @param url      图片地址
     * @param options  图片配置器
     * @param listener 监听器
     */
    private void load(String url, RequestOptions options, RequestListener<Drawable> listener) {
        if (getContext() == null && getImageView() == null) return;
        requestBuilder(url, options, listener).into(getImageView());
    }

    private RequestBuilder<Drawable> requestBuilder(Object obj, RequestOptions options, RequestListener<Drawable> listener) {

        RequestBuilder<Drawable> requestBuilder = Glide.with(getContext())
                .load(obj)
                .apply(options);
        if (listener != null)
            requestBuilder.listener(listener);
        return requestBuilder;
    }

    /**
     * 添加占位符
     *
     * @param placeholderResId 本地资源占位符
     * @return
     */
    private RequestOptions requestOptions(int placeholderResId) {
        return requestOptions(placeholderResId, placeholderResId);
    }

    /**
     * 添加占位符和加载失败图
     *
     * @param placeholderResId  本地资源占位符
     * @param errorResId        本地资源加载失败图
     * @param diskCacheStrategy 磁盘缓存
     * @return
     */
    private RequestOptions requestOptions(int placeholderResId, int errorResId,
                                          DiskCacheStrategy diskCacheStrategy) {

        boolean isSkipMemoryCache = diskCacheStrategy == DiskCacheStrategy.NONE;
        return requestOptions(placeholderResId, errorResId)
                .diskCacheStrategy(diskCacheStrategy)
                .skipMemoryCache(isSkipMemoryCache)
                .dontAnimate();
    }

    /**
     * 添加占位符和加载失败图
     *
     * @param placeholderResId 本地资源占位符
     * @param errorResId       本地资源加载失败图
     * @return
     */
    private RequestOptions requestOptions(int placeholderResId, int errorResId) {
        return new RequestOptions()
                .placeholder(placeholderResId)
                .error(errorResId)
                .dontAnimate();
    }


    /**
     * 添加占位符和加载失败图
     *
     * @param placeholder 本地资源占位符
     * @param error       本地资源加载失败图
     * @return
     */
    private RequestOptions requestOptions(@Nullable Drawable placeholder, Drawable error) {
        return new RequestOptions()
                .placeholder(placeholder)
                .error(error)
                .dontAnimate();
    }

    /**
     * 添加圆形图片占位图
     *
     * @param placeholderResId 本地资源占位符
     * @return
     */
    private RequestOptions circleRequestOptions(int placeholderResId) {
        return circleRequestOptions(placeholderResId, placeholderResId);
    }

    /**
     * 添加圆形占位符和加载失败图
     *
     * @param placeholderResId 本地资源占位符
     * @param errorResId       本地资源加载失败图
     * @return
     */
    private RequestOptions circleRequestOptions(int placeholderResId, int errorResId) {

        return requestOptions(placeholderResId, errorResId)
                .transform(new CropCircleTransformation())
                .dontAnimate();
    }

    /**
     * 添加圆形带边框占位符和加载失败图
     *
     * @param placeholderResId
     * @param errorResId
     * @param borderWidth
     * @param borderColorId
     * @return
     */
    private RequestOptions circleWithBorderRequestOptions(int placeholderResId, int errorResId, int borderWidth, int borderColorId) {

        return requestOptions(placeholderResId, errorResId)
                .transform(new GlideCircleTransformWithBorder(getContext(), borderWidth, borderColorId))
                .skipMemoryCache(false);
    }

    /**
     * 添加圆角占位符和加载失败图
     *
     * @param placeholderResId 本地资源占位符
     * @param errorResId       本地资源加载失败图
     * @param radius           圆角弧度
     * @param margin           图片间距
     * @return
     */
    private RequestOptions roundRequestOptions(int placeholderResId, int errorResId, int radius, int margin) {

        return requestOptions(placeholderResId, errorResId)
                .transform(new RoundedCornersTransformation(radius, margin))
                .dontAnimate();
    }

    /**
     * 加载网络图片
     *
     * @param url              图片地址
     * @param placeholderResId 本地资源占位图
     */
    public void loadImage(String url, int placeholderResId) {
        load(url, requestOptions(placeholderResId));
    }

    /**
     * 加载网络图片
     *
     * @param url         图片地址
     * @param placeholder 本地资源占位图
     * @param error       错误图
     */
    public void loadImage(String url, Drawable placeholder, Drawable error) {
        load(url, requestOptions(placeholder, error));
    }

    /**
     * 加载网络图片
     *
     * @param url 图片地址
     */
    public void loadImage(String url) {

        load(url, requestOptions(0));
    }

    public void loadImage(int drawable) {
        load(drawable, requestOptions(0));
    }

    /**
     * 加载网络图片
     *
     * @param url               图片地址
     * @param placeholderResId  本地资源占位图
     * @param diskCacheStrategy 磁盘缓存
     * @param listener          监听器
     */
    public void loadImage(String url, int placeholderResId,
                          DiskCacheStrategy diskCacheStrategy, RequestListener<Drawable> listener) {

        load(url, requestOptions(placeholderResId, placeholderResId, diskCacheStrategy), listener);
    }

    /**
     * 加载网络圆形图片
     *
     * @param url              图片地址
     * @param placeholderResId 本地资源占位图
     * @param listener         监听器
     */
    public void loadCircleImage(String url, int placeholderResId, RequestListener<Drawable> listener) {

        load(url, circleRequestOptions(placeholderResId, placeholderResId), listener);
    }

    /**
     * 加载网络圆形带边框图片
     *
     * @param url
     * @param placeholderResId
     * @param borderWidth
     * @param borderColorId
     */
    public void loadCircleWithBorderImage(String url, int placeholderResId, int errorResId, int borderWidth, int borderColorId) {

        load(url, circleWithBorderRequestOptions(placeholderResId, errorResId, borderWidth, borderColorId));
    }

    /**
     * 加载圆角图片
     *
     * @param url              图片地址
     * @param placeholderResId 本地资源占位图
     * @param radius           圆角弧度
     * @param margin           图片间距
     */
    public void loadRoundImage(String url, int placeholderResId, int radius, int margin) {

        load(url, roundRequestOptions(placeholderResId, placeholderResId, radius, margin));
    }


    /**
     * 加载本地图片
     *
     * @param resId            本地资源
     * @param placeholderResId 本地资源占位图
     */
    public void loadLocalImage(@DrawableRes int resId, int placeholderResId) {
        load(resId, requestOptions(placeholderResId));
    }

    /**
     * 加载设备图片
     *
     * @param localPath        设备图片地址
     * @param placeholderResId 本地资源占位图
     */
    public void loadLocalImage(String localPath, int placeholderResId) {
        load(FILE + localPath, requestOptions(placeholderResId));
    }

    /**
     * 加载圆形图片
     *
     * @param url              图片地址
     * @param placeholderResId 本地资源占位图
     */
    public void loadCircleImage(String url, int placeholderResId) {
        load(url, circleRequestOptions(placeholderResId));
    }

    /**
     * 加载本地圆形图片
     *
     * @param resId            本地资源图片
     * @param placeholderResId 本地资源占位图
     */
    public void loadLocalCircleImage(int resId, int placeholderResId) {
        load(resId, circleRequestOptions(placeholderResId));
    }

    /**
     * 加载设备圆形图片
     *
     * @param localPath        设备图片地址
     * @param placeholderResId 本地资源占位图
     */
    public void loadLocalCircleImage(String localPath, int placeholderResId) {
        load(FILE + localPath, circleRequestOptions(placeholderResId));
    }

    private OnGetBitmapCall mCacheBitmapCall;

    /**
     * 允许图片的字节流到磁盘中
     *
     * @param url  图片地址
     * @param call 图片回调
     */
    public void loadCacheImage(final String url, OnGetBitmapCall call) {

        mCacheBitmapCall = call;
        ThreadManager.execute(new Runnable() {
            @Override
            public void run() {

                File file;
                Message msg = new Message();
                try {
                    file = Glide.with(getContext())
                            .load(url)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                } catch (Exception ex) {
                    file = null;
                }
                if (file != null && mCacheBitmapCall != null) {
                    msg.obj = file;
                    mHandler.sendMessage(msg);
                }
            }
        });
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (mCacheBitmapCall != null) {
                File file = (File) msg.obj;
                String path = file.getPath();
                mCacheBitmapCall.getBitmap(BitmapFactory.decodeFile(path));
            }
        }
    };

    public interface OnGetBitmapCall {

        /**
         * 获取图片回调
         *
         * @param bitmap 图片
         */
        void getBitmap(Bitmap bitmap);
    }

}
