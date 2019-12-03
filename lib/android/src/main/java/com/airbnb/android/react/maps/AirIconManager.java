package com.airbnb.android.react.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class AirIconManager {
    private DataSource<CloseableReference<CloseableImage>> dataSource;
    Map<String, BitmapDescriptor> uriToBitMapDescriptorMap = new HashMap<>();
    String imageUri;
    View view;
    Context ctx;
    AirIconManagerCallbacks cb;
    private final DraweeHolder<?> logoHolder;
    private GenericDraweeHierarchy createDraweeHierarchy() {
        return new GenericDraweeHierarchyBuilder(view.getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setFadeDuration(0)
                .build();
    }
    public AirIconManager(Context ctx, View v, AirIconManagerCallbacks cb) {
        this.ctx = ctx;
        this.view = v;
        logoHolder = DraweeHolder.create(createDraweeHierarchy(), ctx);
        logoHolder.onAttach();
        this.cb = cb;
    }
    private int getDrawableResourceByName(String name) {
        return view.getResources().getIdentifier(
                name,
                "drawable",
                ctx.getPackageName());
    }
    private BitmapDescriptor getBitmapDescriptorByName(String name) {
        return BitmapDescriptorFactory.fromResource(getDrawableResourceByName(name));
    }
    public void loadImage(final String uri) {
        if (getDescriptor(uri) != null) return;
        if (uri != null && (uri.startsWith("http://") || uri.startsWith("https://") ||
                uri.startsWith("file://") || uri.startsWith("asset://") || uri.startsWith("data:"))) {
            ImageRequest imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(uri))
                    .build();

            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            dataSource = imagePipeline.fetchDecodedImage(imageRequest, this);
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setControllerListener(new BaseControllerListener<ImageInfo>() {
                        @Override
                        public void onFinalImageSet(
                                String id,
                                @Nullable final ImageInfo imageInfo,
                                @Nullable Animatable animatable) {
                            CloseableReference<CloseableImage> imageReference = null;
                            try {
                                int height = imageInfo.getHeight();
                                int width = imageInfo.getWidth();

                                float finalHeight = 150;
                                float finalWidth = finalHeight * width / height;
                                imageReference = dataSource.getResult();
                                if (imageReference != null) {
                                    CloseableImage image = imageReference.get();
                                    if (image != null && image instanceof CloseableStaticBitmap) {
                                        CloseableStaticBitmap closeableStaticBitmap = (CloseableStaticBitmap) image;
                                        Bitmap bitmap = closeableStaticBitmap.getUnderlyingBitmap();
                                        if (bitmap != null) {
                                            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
                                            bitmap = bitmap.createScaledBitmap(bitmap, (int)finalWidth, (int)finalHeight, false);
                                            BitmapDescriptor d = BitmapDescriptorFactory.fromBitmap(bitmap);
                                            uriToBitMapDescriptorMap.put(uri, d);
                                            if (cb != null) cb.onBitmapDescriptorReady(d, uri);
                                        }
                                    }
                                }
                            } finally {
                                dataSource.close();
                                if (imageReference != null) {
                                    CloseableReference.closeSafely(imageReference);
                                }
                            }
                        }
                    })
                    .setOldController(logoHolder.getController())
                    .build();
            logoHolder.setController(controller);
        } else {
            BitmapDescriptor iconBitmapDescriptor = getBitmapDescriptorByName(uri);
            if (iconBitmapDescriptor != null) {
                int drawableId = getDrawableResourceByName(uri);
                Bitmap iconBitmap = BitmapFactory.decodeResource(view.getResources(), drawableId);
                if (iconBitmap == null) { // VectorDrawable or similar
                    Drawable drawable = view.getResources().getDrawable(drawableId);
                    iconBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    Canvas canvas = new Canvas(iconBitmap);
                    drawable.draw(canvas);
                    iconBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(iconBitmap);
                    uriToBitMapDescriptorMap.put(uri, iconBitmapDescriptor);
                    if (cb != null) cb.onBitmapDescriptorReady(iconBitmapDescriptor, uri);
                }
            }
        }
    }
    public BitmapDescriptor getDescriptor(String uri) {
        return uriToBitMapDescriptorMap.get(uri);
    }
}
