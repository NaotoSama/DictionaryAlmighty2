package com.example.android.dictionaryalmighty2;

/**
 * 設置客製化Spinner選單各項目要用的constructor(圖+文字)
 */

public class DictionaryItem {
    private int mSpinnerImage;
    private int mSpinnerText;

    public DictionaryItem(int SpinnerText, int SpinnerImage) {
        mSpinnerText = SpinnerText;
        mSpinnerImage = SpinnerImage;
    }

    public int getSpinnerImage() {
        return mSpinnerImage;
    }

    public int getSpinnerText() {
        return mSpinnerText;
    }
}
