package gmedia.net.id.kopkarmitramakmur.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Shin on 1/31/2017.
 */

public class SignatureBuilder extends View {

    private static final float STROKE_WIDTH = 5f;
    private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
    private Paint paint = new Paint();
    private Path path = new Path();

    private float lastTouchX;
    private float lastTouchY;
    private final RectF dirtyRect = new RectF();
    public Bitmap mBitmap;
    private LinearLayout mContent;
    private Context context;
    public File mypath;
    public String path_image;

    public SignatureBuilder(Context context, AttributeSet attrs, final LinearLayout mContent, File mypath)
    {
        super(context, attrs);
        this.context = context;
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(STROKE_WIDTH);
        this.mContent = mContent;
        this.mypath = mypath;
    }

    public void SaveBitmap(View v)
    {
        Log.v("log_tag", "Width: " + v.getWidth());
        Log.v("log_tag", "Height: " + v.getHeight());
        if(mBitmap == null)
        {
            mBitmap =  Bitmap.createBitmap (mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);;
        }
        Canvas canvas = new Canvas(mBitmap);
        try
        {
            FileOutputStream mFileOutStream = new FileOutputStream(mypath);

            v.draw(canvas);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, mFileOutStream);

            mFileOutStream.flush();
            mFileOutStream.close();
            path_image = MediaStore.Images.Media.insertImage(context.getContentResolver(), mBitmap, "title", null);
            mypath.delete();
            Log.v("log_tag","url: " + path_image);
            //In case you want to delete the file
            //boolean deleted = mypath.delete();
            //Log.v("log_tag","deleted: " + mypath.toString() + deleted);
            //If you want to convert the image to string use base64 converter

        }
        catch(Exception e)
        {
            Log.v("log_tag", e.toString());
        }
    }

    public void clear()
    {
        path.reset();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float eventX = event.getX();
        float eventY = event.getY();
//        mGetSign.setEnabled(true);

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(eventX, eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;
                return true;

            case MotionEvent.ACTION_MOVE:

            case MotionEvent.ACTION_UP:

                resetDirtyRect(eventX, eventY);
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++)
                {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    expandDirtyRect(historicalX, historicalY);
                    path.lineTo(historicalX, historicalY);
                }
                path.lineTo(eventX, eventY);
                break;

            default:
                debug("Ignored touch event: " + event.toString());
                return false;
        }

        invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

        lastTouchX = eventX;
        lastTouchY = eventY;

        return true;
    }

    private void debug(String string){
    }

    private void expandDirtyRect(float historicalX, float historicalY)
    {
        if (historicalX < dirtyRect.left)
        {
            dirtyRect.left = historicalX;
        }
        else if (historicalX > dirtyRect.right)
        {
            dirtyRect.right = historicalX;
        }

        if (historicalY < dirtyRect.top)
        {
            dirtyRect.top = historicalY;
        }
        else if (historicalY > dirtyRect.bottom)
        {
            dirtyRect.bottom = historicalY;
        }
    }

    private void resetDirtyRect(float eventX, float eventY)
    {
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }
}
