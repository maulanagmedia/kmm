package gmedia.net.id.kopkarmitramakmur.Util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import gmedia.net.id.kopkarmitramakmur.CustomView.BadgeDrawable;
import gmedia.net.id.kopkarmitramakmur.R;

/**
 * Created by Shin on 4/3/2017.
 */

public class Utils {

    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    public static final String Setoran = "SS";
    public static final String Penarikan = "PS";
    public static final String Pinjaman = "PP";
    public static final String Belanja = "BO";

}
