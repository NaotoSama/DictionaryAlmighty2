package com.example.android.dictionaryalmighty2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.Text;
import java.util.List;


/*以下功能廢除不使用了*/


/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
public class OcrGraphic extends GraphicOverlay.Graphic {

    private int id;

    private static final int TEXT_COLOR = Color.YELLOW;
    private static final int RECT_COLOR = Color.BLACK;


    private static Paint rectPaint;
    private static Paint textPaint;
    private final TextBlock textBlock;

    OcrGraphic(GraphicOverlay overlay, TextBlock text) {
        super(overlay);

        textBlock = text;

        //繪製文字辨識的矩形框
        if (rectPaint == null) {
            rectPaint = new Paint();
            rectPaint.setColor(RECT_COLOR);
            rectPaint.setStyle(Paint.Style.FILL);  //FILL是填滿內框；若只要設置外框顏色則用STROKE
            rectPaint.setStrokeWidth(4.0f);
        }

        //繪製矩形框內的文字
        if (textPaint == null) {
            textPaint = new Paint();
            textPaint.setColor(TEXT_COLOR);
            textPaint.setTextSize(54.0f);
        }
        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TextBlock getTextBlock() {
        return textBlock;
    }

    /**
     * Checks whether a point is within the bounding box of this graphic.
     * The provided point should be relative to this graphic's containing overlay.
     * @param x An x parameter in the relative context of the canvas.
     * @param y A y parameter in the relative context of the canvas.
     * @return True if the provided point is contained within this graphic's bounding box.
     */
    public boolean contains(float x, float y) {
        if (textBlock == null) {
            return false;
        }
        RectF rect = new RectF(textBlock.getBoundingBox());
        rect = translateRect(rect);
        return rect.contains(x, y);
    }

    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        if (textBlock == null) {
            return;
        }

        // Draws the bounding box around the TextBlock.
        RectF rect = new RectF(textBlock.getBoundingBox());
        rect = translateRect(rect);
        canvas.drawRect(rect, rectPaint);

        // Break the text into multiple lines and draw each one according to its own bounding box.
        List<? extends Text> textComponents = (List<? extends Text>) textBlock.getComponents();
        for(Text currentText : textComponents) {
            float left = translateX(currentText.getBoundingBox().left);
            float bottom = translateY(currentText.getBoundingBox().bottom);
            canvas.drawText(currentText.getValue(), left, bottom, textPaint);
        }
    }
}
