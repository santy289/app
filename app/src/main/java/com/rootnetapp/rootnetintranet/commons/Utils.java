package com.rootnetapp.rootnetintranet.commons;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.rootnetapp.rootnetintranet.BuildConfig;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.login.JWToken;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static final String URL = BuildConfig.BASE_URL;

    public static final String remainderOfDomain = ".rootnetapp.com";

    public static String domain;

    public static String imgDomain;

    private static ProgressDialog progress;

    private static final String TAG = "Utils.Rootnet";

    private static final String ENCODING_UTF_8 = "UTF-8";


    public static void showLoading(Context ctx) {
        progress = new ProgressDialog(ctx);
        progress.show();
        progress.setMessage(ctx.getString(R.string.loading_message));
        progress.setCancelable(false);
    }

    public static void hideLoading() {
        if (null != progress && progress.isShowing()) {
            progress.dismiss();
        }
    }

    public static boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec(command).waitFor() == 0);
    }

    public static File byteToFile(byte[] bytearray) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytearray);
        ObjectInputStream ois = new ObjectInputStream(bis);
        File fileFromBytes = null;
        fileFromBytes = (File) ois.readObject();
        bis.close();
        ois.close();
        return fileFromBytes;
    }

    public static byte[] fileToByte(File file) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(file);
        bos.close();
        oos.close();
        return bos.toByteArray();
    }

    public static String getMimeType(Uri uri, Context appCtx) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = appCtx.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static String getCurrentDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c);
    }

    public static String getMonthDay(int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH,  day);
        Date c = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c);
    }

    public static String getWeekStart(){
        Calendar calendar = Calendar.getInstance();
        while(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
            calendar.add(Calendar.DAY_OF_YEAR, -1) ;
        }
        Date c = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c);
    }

    public static String getWeekEnd(){
        Calendar calendar = Calendar.getInstance();
        while(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY){
            calendar.add(Calendar.DAY_OF_YEAR, 1) ;
        }
        Date c = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c);
    }

    public static String replaceLast(String string, String substring, String replacement) {
        int index = string.lastIndexOf(substring);
        if (index == -1)
            return string;
        return string.substring(0, index) + replacement
                + string.substring(index+substring.length());
    }

    public static JWToken decode(String token) {
        final String[] parts = splitToken(token);
        JWToken payload = parseJson(base64Decode(parts[1]));
        return payload;
    }

    private static String[] splitToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            Log.d(TAG, "we dont have 3 parts");
            return parts;
        }
        return parts;
    }

    @Nullable
    private static String base64Decode(String string) {
        String decoded = "";
        try {
            byte[] bytes = Base64.decode(string, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
            decoded = new String(bytes, ENCODING_UTF_8);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Not valid base 64 encoding");
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "Device doesn't support UTF-8 charset encoding.");
        }
        return decoded;
    }

    private static JWToken parseJson(String jsonString) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<JWToken> jsonAdapter = moshi.adapter(JWToken.class);
        JWToken jwToken = null;
        try {
            jwToken = jsonAdapter.fromJson(jsonString);
        } catch (IOException e) {
            Log.d(TAG, "parseJson: error - " + e.getMessage());
        }
        return jwToken;
    }

    public static boolean checkFileSize(int limitMb, File file) {
        long length = file.length();
        long lengthMb = length / (1024 * 1024);
        if (lengthMb > limitMb) {
            return false;
        }
        return true;
    }

    public static String encodeFileToBase64Binary(File file)
            throws IOException {
        byte[] bytes = loadFile(file);
//        byte[] bytes = fileToByte(file);
        String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
        return encodedString;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int)length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        is.close();
        return bytes;
    }

}
