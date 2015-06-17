package com.ving.narcoticinventory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CaptureSignature extends Activity { 
	
	public static final int SIGNATURE_ACTIVITY = 1;
	public static final int ERROR_SIGNATURE = 2;
	private MyApplication myApp;
	private View dialogView;
	private AlertDialog.Builder dialog;
	private AlertDialog captureSignatureDialog;
    private LinearLayout mContent;
    private Signature mSignature;
    private Button mClear, mGetSign, mCancel;
    private String name = null;
    public static String tempDir;
    public int count = 1;
    public String current = null;
    private Bitmap mBitmap;
    private View mView;
    private File mypath;
    private String uniqueId;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	myApp = (MyApplication) getApplication();
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent myIntent = getIntent();
        name = myIntent.getStringExtra("Name");
        tempDir = myIntent.getStringExtra("Directory");
        dialogView = getLayoutInflater().inflate(R.layout.dialog_signature,null);
        TextView nameLabel = (TextView) dialogView.findViewById(R.id.nameLabel);
        nameLabel.setText(name);
        File directory = myApp.getDrugs().storageDirectory();
        uniqueId = getTodaysDate() + "_" + getCurrentTime();
        current = uniqueId + ".png";
        mypath = new File(directory,current);

        LinearLayout dialogBg = (LinearLayout) dialogView.findViewById(R.id.linearLayout1);
        dialogBg.setBackgroundColor(Color.LTGRAY);
        mContent = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        mSignature = new Signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature);
        mClear = (Button) dialogView.findViewById(R.id.clear);
        mGetSign = (Button) dialogView.findViewById(R.id.getsign);
        mGetSign.setEnabled(false);
        mCancel = (Button) dialogView.findViewById(R.id.cancel);
        mView = mContent;
        
        dialog = new AlertDialog.Builder(this);
        dialog.setView(dialogView);
        dialog.setTitle("Enter Signature");
        dialog.setCancelable(false);
        captureSignatureDialog = dialog.create();


        mClear.setOnClickListener(new OnClickListener() 
        {        
            public void onClick(View v) 
            {
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });

        mGetSign.setOnClickListener(new OnClickListener() 
        {        
            public void onClick(View v) 
            {
                mView.setDrawingCacheEnabled(true);
                mSignature.save(mView);
                Bundle b = new Bundle();
                b.putString("status", "done");
                b.putString("filename",current);
                Intent intent = new Intent();
                intent.putExtras(b);
                setResult(RESULT_OK,intent);
                captureSignatureDialog.dismiss();
                finish();
            }
        });

        mCancel.setOnClickListener(new OnClickListener() 
        {        
            public void onClick(View v) 
            {
                Bundle b = new Bundle();
                b.putString("status", "cancel");
                Intent intent = new Intent();
                intent.putExtras(b);
                setResult(RESULT_OK,intent);
                captureSignatureDialog.dismiss();
                finish();
            }
        });
        captureSignatureDialog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private String getTodaysDate() { 

        final Calendar c = Calendar.getInstance();
        int todaysDate =     (c.get(Calendar.YEAR) * 10000) + 
        ((c.get(Calendar.MONTH) + 1) * 100) + 
        (c.get(Calendar.DAY_OF_MONTH));
        return(String.valueOf(todaysDate));

    }

    private String getCurrentTime() {

        final Calendar c = Calendar.getInstance();
        int currentTime =     (c.get(Calendar.HOUR_OF_DAY) * 10000) + 
        (c.get(Calendar.MINUTE) * 100) + 
        (c.get(Calendar.SECOND));
        return(String.valueOf(currentTime));

    }

    public class Signature extends View 
    {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public Signature(Context context, AttributeSet attrs) 
        {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v) 
        {
            if(mBitmap == null)
            {
                mBitmap =  Bitmap.createBitmap (mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);;
            }
            Canvas canvas = new Canvas(mBitmap);
            try 
            {
                FileOutputStream mFileOutStream = new FileOutputStream(mypath);

                v.draw(canvas); 
                mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream); 
                mFileOutStream.flush();
                mFileOutStream.close();

            }
            catch(Exception e) 
            { 
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
            mGetSign.setEnabled(true);

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
}