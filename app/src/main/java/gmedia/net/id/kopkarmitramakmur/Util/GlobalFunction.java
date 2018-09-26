package gmedia.net.id.kopkarmitramakmur.Util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.TypedValue;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Shin on 3/21/2017.
 */

public class GlobalFunction {

    public int dpToPx(Context context, int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static String getStatus(String status){

        switch (status) {
            case "0":
                return "<font color='#4688c3'>Diproses</font>";
            case "1":
                return "<font color='#E38217'>Diterima</font>";
            case "2":
                return "<font color='#3EA055'>Disetujui</font>";
            case "3":
                return "<font color='#e35152'>Ditolak</font>";
            default:
                return "<font color='#4688c3'>Diproses</font>";
        }
    }

    public static String getStatusBelanja(String status){

        ItemValidation iv = new ItemValidation();
        switch (iv.parseNullInteger(status)) {
            case 0:
                return "<font color='#9aaab9'>Order Diterima</font>";
            case 1:
                return "<font color='#E38217'>Sudah Siap</font>";
            case 2:
                return "<font color='#3EA055'>Selesai</font>";
            case 3:
                return "<font color='#e35152'>Ditolak</font>";
            default:
                return "<font color='#4688c3'>Order Diterima</font>";
        }
    }

    public static boolean isLinkActive(URL url){
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            urlConnection.disconnect();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public static class UrlChecker extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String url = params[0];
            // Check your URL here
            return true;
        }

        @Override
        protected void onPostExecute(Boolean exists) {
            // Do something with your result
        }
    }
}
