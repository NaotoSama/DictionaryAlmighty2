package com.example.android.dictionaryalmighty2;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



public class tesscv {
    private final static String TAG = "TessCV";
    private Bitmap m_phone;                      // The path of phone image
    private TessBaseAPI m_tessApi;               // Tesseract API reference
    private String m_datapath;                   // The path to folder containing language data file
    private InputStream m_instream;
    private String datafilepath;
    private String resInPath;
    private String resOutPath;

    public tesscv(Bitmap phone, InputStream instream) {
        m_phone = phone;
        m_instream = instream;

        /// initial tesseract-ocr
        m_datapath = Environment.getExternalStorageDirectory().toString() + "/MyLibApp/tesscv/tesseract";
        // make sure training data has been copied
        checkFile(new File(m_datapath + "/tessdata"));

        m_tessApi = new TessBaseAPI();
        switch (MainActivity.tesseract_lang_code) {
            case "eng":
                m_tessApi.init(m_datapath, "eng");

                break;
            case "chi_tra":
                m_tessApi.init(m_datapath, "chi_tra");

                break;
            case "chi_sim":
                m_tessApi.init(m_datapath, "chi_sim");

                break;
            case "jpn":
                m_tessApi.init(m_datapath, "jpn");

                break;
            case "kor":
                m_tessApi.init(m_datapath, "kor");

                break;
        }
        // 设置psm模式
        //m_tessApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK);
        // 设置白名单
        //m_tessApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        //m_tessApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789");
    }


    private void saveTmpImage(String name, Mat image) {
        Mat img = image.clone();
        if (img.channels() ==3 ) {
            Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2RGBA);
        }

        Bitmap bmp = null;
        try {
            bmp = Bitmap.createBitmap(img.cols(), img.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(img, bmp);
        } catch (CvException e) {
            Log.d("mat2bitmap", e.getMessage());
        }
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MyLibApp/tesscv");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("saveTmpImage", "failed to create directory");
                return;
            }
        }
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //File dest = new File(mediaStorageDir.getPath() + File.separator + name + timeStamp + ".png");
        File dest = new File(mediaStorageDir.getPath() + File.separator + name + ".jpg");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(dest);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            // bmp is your Bitmap instance
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public String getOcrOfBitmap() {
        if (m_phone == null) {
            return "";
        }

        Mat imgBgra = new Mat(m_phone.getHeight(), m_phone.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(m_phone, imgBgra);
        Mat imgBgr = new Mat();
        Imgproc.cvtColor(imgBgra, imgBgr, Imgproc.COLOR_RGBA2BGR);
        Mat img = imgBgr;
        saveTmpImage("srcInputBitmap", img);
        if (img.empty()) {
            return "";
        }
        if (img.channels()==3) {
            Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
        }
        return getResOfTesseractReg(img);
    }


    private String getResOfTesseractReg(Mat img) {
        String res;
        if (img.empty()) {
            return "";
        }
        byte[] bytes = new byte[(int)(img.total()*img.channels())];
        img.get(0, 0, bytes);
        m_tessApi.setImage(bytes, img.cols(), img.rows(), 1, img.cols());
        res = m_tessApi.getUTF8Text();
        return res;
    }


    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists() && dir.mkdirs()){
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()) {

            switch (MainActivity.tesseract_lang_code) {
                case "eng":
                    datafilepath = dir.toString() + "/eng.traineddata";

                    break;
                case "chi_tra":
                    datafilepath = dir.toString() + "/chi_tra.traineddata";

                    break;
                case "chi_sim":
                    datafilepath = dir.toString() + "/chi_sim.traineddata";

                    break;
                case "jpn":
                    datafilepath = dir.toString() + "/jpn.traineddata";

                    break;
                case "kor":
                    datafilepath = dir.toString() + "/kor.traineddata";

                    break;
            }

            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }


    private void copyFiles() {
        try {
            if (m_instream == null) {

                switch (MainActivity.tesseract_lang_code) {
                    case "eng":
                        resInPath = "/tessdata/eng.traineddata";

                        break;
                    case "chi_tra":
                        resInPath = "/tessdata/chi_tra.traineddata";

                        break;
                    case "chi_sim":
                        resInPath = "/tessdata/chi_sim.traineddata";

                        break;
                    case "jpn":
                        resInPath = "/tessdata/jpn.traineddata";

                        break;
                    case "kor":
                        resInPath = "/tessdata/kor.traineddata";

                        break;
                }

                //Log.d(TAG, "copyFiles: resInPath " + resInPath);
                m_instream = new FileInputStream(resInPath);
            }



            switch (MainActivity.tesseract_lang_code) {
                case "eng":
                    //location we want the file to be a
                    resOutPath = m_datapath + "/tessdata/eng.traineddata";

                    break;
                case "chi_tra":
                    resOutPath = m_datapath + "/tessdata/chi_tra.traineddata";

                    break;
                case "chi_sim":
                    resOutPath = m_datapath + "/tessdata/chi_sim.traineddata";

                    break;
                case "jpn":
                    resOutPath = m_datapath + "/tessdata/jpn.traineddata";

                    break;
                case "kor":
                    resOutPath = m_datapath + "/tessdata/kor.traineddata";

                    break;
            }


            //open byte streams for writing
            OutputStream outstream = new FileOutputStream(resOutPath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = m_instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            m_instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
