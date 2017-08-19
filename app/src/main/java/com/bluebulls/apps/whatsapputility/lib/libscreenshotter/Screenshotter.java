package com.bluebulls.apps.whatsapputility.lib.libscreenshotter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Created by omerjerk on 17/2/16.
 */
public class Screenshotter implements ImageReader.OnImageAvailableListener {

    private static final String TAG = "LibScreenshotter";

    private VirtualDisplay virtualDisplay;

    private int width;
    private int height;

    private int resultCode;
    private Intent data;
    private ScreenshotCallback cb;

    private static Screenshotter mInstance;

    private ImageReader mImageReader;
    private MediaProjection mMediaProjection;
    private volatile int imageAvailable = 0;

    /**
     * Get the single instance of the Screenshotter class.
     * @return the instance
     */
    public static Screenshotter getInstance() {
        if (mInstance == null) {
            mInstance = new Screenshotter();
        }
        return mInstance;
    }

    private Screenshotter() {}

    /**
     * Takes the screenshot of whatever currently is on the default display.
     * @param resultCode The result code returned by the request for accessing MediaProjection permission
     * @param data The intent returned by the same request
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Screenshotter takeScreenshot(Context context, int resultCode, Intent data, final ScreenshotCallback cb) {
        this.cb = cb;
        this.resultCode = resultCode;
        this.data = data;

        imageAvailable = 0;

        //mImageReader = ImageReader.newInstance(width, height, ImageFormat.RGB_565, 2);

        mImageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 5);

        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) context
                .getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        if (mMediaProjection == null) {
            mMediaProjection = mediaProjectionManager.getMediaProjection(this.resultCode, this.data);
            if (mMediaProjection == null) {
                Log.e(TAG, "MediaProjection null. Cannot take the screenshot.");
            }
        }
        try {
            virtualDisplay = mMediaProjection.createVirtualDisplay("Screenshotter",
                    width, height, context.getResources().getDisplayMetrics().densityDpi,
                    17,
                    mImageReader.getSurface(), null, null);
            mImageReader.setOnImageAvailableListener(Screenshotter.this, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    /**
     * Set the size of the screenshot to be taken
     * @param width width of the requested bitmap
     * @param height height of the request bitmap
     * @return the singleton instance
     */
    public Screenshotter setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public Screenshotter setDefaultSize(Context context){
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        this.width = point.x;
        this.height = point.y;
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Screenshotter takeScreenShotUltra(Context context, int resultCode, Intent data, final ScreenshotCallback cb){
        this.cb = cb;
        this.resultCode = resultCode;
        this.data = data;

        mImageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 1);
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) context
                .getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mMediaProjection == null) {
            mMediaProjection = mediaProjectionManager.getMediaProjection(this.resultCode, this.data);
            if (mMediaProjection == null) {
                Log.e(TAG, "MediaProjection null. Cannot take the screenshot.");
            }
        }
        virtualDisplay = mMediaProjection.createVirtualDisplay("Screenshotter",
                width, height, context.getResources().getDisplayMetrics().densityDpi,
                17,
                mImageReader.getSurface(), null, null);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Log.i(TAG, "in OnImageAvailable");
                FileOutputStream fos = null;
                Bitmap bitmap = null;
                Image img = null;
                try {
                    img = reader.acquireLatestImage();
                    if (img != null) {
                        Image.Plane[] planes = img.getPlanes();
                        if (planes[0].getBuffer() == null) {
                            return;
                        }
                        int width = img.getWidth();
                        int height = img.getHeight();
                        int pixelStride = planes[0].getPixelStride();
                        int rowStride = planes[0].getRowStride();
                        int rowPadding = rowStride - pixelStride * width;
                        int offset = 0;
                        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        ByteBuffer buffer = planes[0].getBuffer();
                        for (int i = 0; i < height; ++i) {
                            for (int j = 0; j < width; ++j) {
                                int pixel = 0;
                                pixel |= (buffer.get(offset) & 0xff) << 16;     // R
                                pixel |= (buffer.get(offset + 1) & 0xff) << 8;  // G
                                pixel |= (buffer.get(offset + 2) & 0xff);       // B
                                pixel |= (buffer.get(offset + 3) & 0xff) << 24; // A
                                bitmap.setPixel(j, i, pixel);
                                offset += pixelStride;
                            }
                            offset += rowPadding;
                        }
                        cb.onScreenshot(bitmap);
                        tearDown();
                        img.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (null != fos) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (null != bitmap) {
                        bitmap.recycle();
                    }
                    if (null != img) {
                        img.close();
                    }
                }
            }
        }, null);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image;
        synchronized (this) {
            ++imageAvailable;
            if (imageAvailable != 2) {
                try {
                    wait(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                image = reader.acquireLatestImage();
                if (image == null) return;
                image.close();
                return;
            }
        }
        image = reader.acquireLatestImage();
        if (image == null) return;
        final Image.Plane[] planes = image.getPlanes();
        final Buffer buffer = planes[0].getBuffer().rewind();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        // create bitmap
        Bitmap bitmap = Bitmap.createBitmap(width+rowPadding/pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        tearDown();
        image.close();
        cb.onScreenshot(bitmap);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void tearDown() {
        virtualDisplay.release();
        mMediaProjection.stop();
        mMediaProjection = null;
        mImageReader = null;
    }
}
