package rkr.binatestation.eqsoft.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.BuildConfig;
import android.support.v4.util.LruCache;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import rkr.binatestation.eqsoft.utils.Constants;

/**
 * Created by RKR on 12-11-2015.
 * VolleySingleTon.
 */
public class VolleySingleTon {
    /**
     * assignment of domain urls
     * the variable isLocal define if the project work in local domain or in live server domains
     */
    private static final String domainUrl = "http://www.piclo.in/app/";
    private static final String domainUrlForImage = "http://www.piclo.in/images/gallery/";

    private static final String localDomainUrl = "http://binatestation.com/piclo/app/";
    private static final String localDomainUrlForImage = "http://binatestation.com/piclo/images/gallery/";

    /**
     * static variable for maintain single Volley queue
     */
    private static VolleySingleTon mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private VolleySingleTon(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    /**
     * synchronised method to ensure only single instance
     */
    public static synchronized VolleySingleTon getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleTon(context);
        }
        return mInstance;
    }

    /**
     * static method used to get the domain url for the images
     */

    public static String getDomainUrlForImage() {
        if (BuildConfig.DEBUG) {
            return localDomainUrlForImage;
        } else {
            return domainUrlForImage;
        }
    }

    /**
     * static method used to get the domain url for APIs
     */
    public static String getDomainUrl() {
        if (BuildConfig.DEBUG) {
            return localDomainUrl;
        } else {
            return domainUrl;
        }
    }

    /**
     * static method used to get the single request queue
     */

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * static method used to add request to the request queue
     */
    public <T> void addToRequestQueue(Request<T> req) {
        req.setRetryPolicy(new DefaultRetryPolicy(Constants.socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    /**
     * static method used to get the image loader
     */

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
