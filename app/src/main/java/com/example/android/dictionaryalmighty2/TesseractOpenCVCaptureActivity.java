package com.example.android.dictionaryalmighty2;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class TesseractOpenCVCaptureActivity extends AppCompatActivity {
    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static final int PHOTOALBUM = 1;   // 相簿
    Button photo_album = null;                // 相簿
    ImageView imageView = null;               // 截取圖像
    EditText OcrTextView = null;                 // OCR 識別結果

    Bitmap m_phone;                           // Bitmap圖像
    String m_ocrOfBitmap;                     // Bitmap圖像OCR識別結果
    InputStream m_instream;
    Uri image;                                //相簿中的原始圖檔
    File tempOutputFile;                      //裁切圖片後的暫存位址

    public static String UrlKey;  //用於自動翻譯OcrSelectedText的網址識別key

    private static final String TAG = "TesseractOpenCVCapture";  //用來記錄日誌的事件



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tesseract_opencv_capture);

        imageView = (ImageView) findViewById(R.id.imageID);
        photo_album = (Button) findViewById(R.id.photo_album);
        OcrTextView = (EditText) findViewById(R.id.OCRTextView);


        photo_album.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                startActivityForResult(intent, PHOTOALBUM);
            }
        });
        Toast.makeText(this, getString(R.string.performing_crop_and_OCR_please_wait), Toast.LENGTH_LONG).show();

        //get access to AssetManager
        AssetManager assetManager = getAssets();
        //open byte streams for reading/writing
        try {

            switch (MainActivity.tesseract_lang_code) {
                case "eng":
                    m_instream = assetManager.open("tessdata/eng.traineddata");

                    break;
                case "chi_tra":
                    m_instream = assetManager.open("tessdata/chi_tra.traineddata");

                    break;
                case "chi_sim":
                    m_instream = assetManager.open("tessdata/chi_sim.traineddata");

                    break;
                case "jpn":
                    m_instream = assetManager.open("tessdata/jpn.traineddata");

                    break;
                case "kor":
                    m_instream = assetManager.open("tessdata/kor.traineddata");

                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        /**
         * 設定用戶選取OCR識別之文字時彈跳出的客製選單
         */
        OcrTextView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.extra_process_text, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.Pass_to_main_page) {
                    int selectionStart = OcrTextView.getSelectionStart();
                    int selectionEnd = OcrTextView.getSelectionEnd();
                    CharSequence OcrSelectedText = OcrTextView.getText().subSequence(selectionStart, selectionEnd);
                    MainActivity.wordInputView.setText(OcrSelectedText);
                    Toast.makeText(getApplicationContext(), getString(R.string.Passed_OcrSelectedText_to_main_page_toast) + "「"+ OcrSelectedText + "」", Toast.LENGTH_LONG).show();
                    return true;

                }else if (item.getItemId() == R.id.Auto_translate_to_CH_TW) {
                    int selectionStart = OcrTextView.getSelectionStart();
                    int selectionEnd = OcrTextView.getSelectionEnd();
                    CharSequence OcrSelectedText = OcrTextView.getText().subSequence(selectionStart, selectionEnd);
                    Toast.makeText(getApplicationContext(), getString(R.string.OCR_auto_translating_toast), Toast.LENGTH_LONG).show();
                    UrlKey="Translate OcrSelectedText to CHTW";
                    Intent autoTranslateOcrSelectedTextToCHTW = new Intent(TesseractOpenCVCaptureActivity.this, MainActivity.class);
                    autoTranslateOcrSelectedTextToCHTW.putExtra(UrlKey, "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-TW&text="+OcrSelectedText);
                    startActivity(autoTranslateOcrSelectedTextToCHTW);
                    return true;

                }else if (item.getItemId() == R.id.Auto_translate_to_CH_CN) {
                    int selectionStart = OcrTextView.getSelectionStart();
                    int selectionEnd = OcrTextView.getSelectionEnd();
                    CharSequence OcrSelectedText = OcrTextView.getText().subSequence(selectionStart, selectionEnd);
                    Toast.makeText(getApplicationContext(), getString(R.string.OCR_auto_translating_toast), Toast.LENGTH_LONG).show();
                    UrlKey="Translate OcrSelectedText to CHCN";
                    Intent autoTranslateOcrSelectedTextToCHCN = new Intent(TesseractOpenCVCaptureActivity.this, MainActivity.class);
                    autoTranslateOcrSelectedTextToCHCN.putExtra(UrlKey, "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=auto&tl=zh-CN&text="+OcrSelectedText);
                    startActivity(autoTranslateOcrSelectedTextToCHCN);
                    return true;

                }else if (item.getItemId() == R.id.Auto_translate_to_EN) {
                    int selectionStart = OcrTextView.getSelectionStart();
                    int selectionEnd = OcrTextView.getSelectionEnd();
                    CharSequence OcrSelectedText = OcrTextView.getText().subSequence(selectionStart, selectionEnd);
                    Toast.makeText(getApplicationContext(), getString(R.string.OCR_auto_translating_toast), Toast.LENGTH_LONG).show();
                    UrlKey="Translate OcrSelectedText to EN";
                    Intent autoTranslateOcrSelectedTextToEN = new Intent(TesseractOpenCVCaptureActivity.this, MainActivity.class);
                    autoTranslateOcrSelectedTextToEN.putExtra(UrlKey, "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=auto&tl=en&text="+OcrSelectedText);
                    startActivity(autoTranslateOcrSelectedTextToEN);
                    return true;

                }else if (item.getItemId() == R.id.Auto_translate_to_JP) {
                    int selectionStart = OcrTextView.getSelectionStart();
                    int selectionEnd = OcrTextView.getSelectionEnd();
                    CharSequence OcrSelectedText = OcrTextView.getText().subSequence(selectionStart, selectionEnd);
                    Toast.makeText(getApplicationContext(), getString(R.string.OCR_auto_translating_toast), Toast.LENGTH_LONG).show();
                    UrlKey="Translate OcrSelectedText to JP";
                    Intent autoTranslateOcrSelectedTextToJP = new Intent(TesseractOpenCVCaptureActivity.this, MainActivity.class);
                    autoTranslateOcrSelectedTextToJP.putExtra(UrlKey, "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=auto&tl=ja&text="+OcrSelectedText);
                    startActivity(autoTranslateOcrSelectedTextToJP);
                    return true;

                }else if (item.getItemId() == R.id.Auto_translate_to_KR) {
                    int selectionStart = OcrTextView.getSelectionStart();
                    int selectionEnd = OcrTextView.getSelectionEnd();
                    CharSequence OcrSelectedText = OcrTextView.getText().subSequence(selectionStart, selectionEnd);
                    Toast.makeText(getApplicationContext(), getString(R.string.OCR_auto_translating_toast), Toast.LENGTH_LONG).show();
                    UrlKey="Translate OcrSelectedText to KR";
                    Intent autoTranslateOcrSelectedTextToKR = new Intent(TesseractOpenCVCaptureActivity.this, MainActivity.class);
                    autoTranslateOcrSelectedTextToKR.putExtra(UrlKey, "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=auto&tl=ko&text="+OcrSelectedText);
                    startActivity(autoTranslateOcrSelectedTextToKR);
                    return true;

                }else if (item.getItemId() == R.id.Auto_translate_to_SP) {
                    int selectionStart = OcrTextView.getSelectionStart();
                    int selectionEnd = OcrTextView.getSelectionEnd();
                    CharSequence OcrSelectedText = OcrTextView.getText().subSequence(selectionStart, selectionEnd);
                    Toast.makeText(getApplicationContext(), getString(R.string.OCR_auto_translating_toast), Toast.LENGTH_LONG).show();
                    UrlKey="Translate OcrSelectedText to SP";
                    Intent autoTranslateOcrSelectedTextToSP = new Intent(TesseractOpenCVCaptureActivity.this, MainActivity.class);
                    autoTranslateOcrSelectedTextToSP.putExtra(UrlKey, "https://translate.google.com.tw/?hl=zh-TW#view=home&op=translate&sl=auto&tl=es&text="+OcrSelectedText);
                    startActivity(autoTranslateOcrSelectedTextToSP);
                    return true;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                Log.d(TAG, "onDestroyActionMode");

            }
        });
    }


    public void cropRawPhoto(Uri image) {

        // 修改設定
        UCrop.Options options = new UCrop.Options();

        // 圖片格式
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        // 設定圖片壓縮質量
        options.setCompressionQuality(100);

        // 允許手指縮放、旋轉圖片，開放所有裁切框的長寬比例
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);

        // 是否讓使用者調整範圍(預設false)，如果開啟，可能會造成剪下的圖片的長寬比不是設定的
        // 如果不開啟，使用者不能拖動選框，只能縮放圖片
        options.setFreeStyleCropEnabled(true);

        // 設定原圖及目標暫存位置
        UCrop.of(image, Uri.fromFile(tempOutputFile))
                // 導入客製化設定
                .withOptions(options)
                .start(this);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0 || data == null) {
            return;
        }
        // 相簿
        if (requestCode == PHOTOALBUM) {
            image = data.getData();
            try {
                tempOutputFile = new File(getExternalCacheDir(), "temp-profile_image.jpg");;
                m_phone = MediaStore.Images.Media.getBitmap(getContentResolver(), image);

                cropRawPhoto(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
         }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            try {
                Bitmap croppedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                m_phone = croppedBitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }

        // 處理結果
        imageView.setImageBitmap(m_phone);
        if (OpenCVLoader.initDebug()) {
            // do some opencv stuff
            tesscv jmi = new tesscv(m_phone, m_instream);
            m_ocrOfBitmap = jmi.getOcrOfBitmap();
        }
        OcrTextView.setText(m_ocrOfBitmap);
        super.onActivityResult(requestCode, resultCode, data);
    }
}