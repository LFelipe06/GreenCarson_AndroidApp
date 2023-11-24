package com.example.greencarson;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

public class ImageLoader {

    // Método para cargar una imagen desde una URL y convertirla en un Drawable
    public static void loadDrawableFromUrl(Context context, String imageUrl, int maxSize, final OnDrawableLoadedListener listener) {
        Glide.with(context)
                .load(imageUrl)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        // Ajustar el tamaño del Drawable
                        int width = resource.getIntrinsicWidth();
                        int height = resource.getIntrinsicHeight();
                        float scale = (float) maxSize / Math.max(width, height);
                        Matrix matrix = new Matrix();
                        matrix.postScale(scale, scale);
                        Bitmap bitmap = drawableToBitmap(resource);
                        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

                        // Convertir el Bitmap ajustado de nuevo a Drawable
                        Drawable scaledDrawable = new BitmapDrawable(context.getResources(), scaledBitmap);

                        // Llama al listener cuando la imagen está cargada
                        if (listener != null) {
                            listener.onDrawableLoaded(scaledDrawable);
                        }
                    }
                });
    }

    // Interfaz para notificar cuando se ha cargado el Drawable
    public interface OnDrawableLoadedListener {
        void onDrawableLoaded(Drawable drawable);
    }
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}

