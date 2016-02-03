package denys.hangman;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Denys on 03-Feb-16.
 */
public class GameView extends View {

    private Paint polePaint;
    private Paint manPaint;
    private  int right,top,width,height;
    private int i=0;

    public GameView(Context context) {
        super(context);
        init(null,0);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs,0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle){
        i=0;

        polePaint = new Paint();
        polePaint.setColor(Color.RED);
        polePaint.setStrokeWidth(10);

        manPaint = new Paint();
        manPaint.setColor(Color.BLACK);
        manPaint.setStrokeWidth(15);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        right = getPaddingRight();
        top = getPaddingTop();
        width = w - (getPaddingLeft() + getPaddingRight());
        height = h - (getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        // draw hangpole
        canvas.drawLine(width / 4, height - height * 7 / 8, (width / 4) + (width / 2), height - height * 7 / 8, polePaint);
        canvas.drawLine(width / 4, height - height * 7 / 8, width / 4, height - height * 2 / 8, polePaint);
        canvas.drawLine((width/4)+(width/2), height-height*7/8, (width/4)+(width/2), height - height * 6 / 8, polePaint);
        canvas.drawLine(width / 4, height - height * 2 / 8, width/8, height - height * 1 / 8, polePaint);
        canvas.drawLine(width / 4, height - height * 2 / 8, width/4, height - height * 1 / 8, polePaint);
        canvas.drawLine(width / 4, height - height * 2 / 8, width/4 + width/8, height - height * 1 / 8, polePaint);

        // draw man
        if(i>0)
        {
            // draw head
            canvas.drawCircle((width / 4) + (width / 2), height - height * 11 / 16, height * 1 / 16, manPaint);
            if(i>1)
            {
                // draw body
                canvas.drawLine((width / 4) + (width / 2), height - height * 10 / 16, (width / 4) + (width / 2), height - height * 3 / 8, manPaint);
                if(i>2)
                {
                    // draw left hand
                    canvas.drawLine((width / 4) + (width / 2), height - height * 10 / 16, width/2 + width/8, height - height * 4 / 8, manPaint);
                    if(i>3)
                    {
                        // draw right hand
                        canvas.drawLine((width / 4) + (width / 2), height - height * 10 / 16, width/2 + width/4 + width/8, height - height * 4 / 8, manPaint);
                        if(i>4)
                        {
                            // draw left leg
                            canvas.drawLine((width / 4) + (width / 2), height - height * 3 / 8, width/2 + width/8, height - height * 1 / 4 , manPaint);
                            if(i>5)
                            {
                                // draw right leg
                                canvas.drawLine((width / 4) + (width / 2), height - height * 3 / 8, width/2 + width/4 + width/8, height - height * 1 / 4, manPaint);
                            }
                        }
                    }
                }
            }
        }


    }

    public void increaseI()
    {
        i++;
    }
}
