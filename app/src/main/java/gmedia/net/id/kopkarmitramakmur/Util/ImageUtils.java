package gmedia.net.id.kopkarmitramakmur.Util;

/**
 * Created by Shin on 2/16/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import gmedia.net.id.kopkarmitramakmur.R;


public class ImageUtils {

    public static Bitmap convert(String base64Str) throws IllegalArgumentException
    {
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",")  + 1),
                Base64.DEFAULT
        );

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String convert(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    public static Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    // Header Slider
    public void LoadImageHeaderSlider(Context context, String uri, final ImageView image){

        Picasso.with(context).load(Uri.parse(uri)).memoryPolicy(MemoryPolicy.NO_CACHE).resize(720, 512).error(context.getResources().getDrawable(R.mipmap.ic_kopkar_logo)).into(image);
    }

    // General Image
    public void LoadImage(Context context, String uri, final ImageView image){

        Picasso.with(context).load(Uri.parse(uri)).memoryPolicy(MemoryPolicy.NO_CACHE).resize(720, 480).error(context.getResources().getDrawable(R.mipmap.ic_kopkar_logo)).into(image);
    }

    // General Icon
    public void LoadIcon(Context context, int id, final ImageView image){

        Picasso.with(context).load(id).memoryPolicy(MemoryPolicy.NO_CACHE).error(context.getResources().getDrawable(R.mipmap.ic_kopkar_logo)).into(image);
    }

    // General Image
    public void LoadImageNormal(Context context, String uri, final ImageView image){

        Picasso.with(context).load(Uri.parse(uri)).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).error(context.getResources().getDrawable(R.mipmap.ic_kopkar_logo)).into(image);
    }
}
