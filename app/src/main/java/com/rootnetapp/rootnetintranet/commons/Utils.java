package com.rootnetapp.rootnetintranet.commons;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.rootnetapp.rootnetintranet.BuildConfig;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.login.JWToken;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import retrofit2.HttpException;

public class Utils {

    public static final String URL = BuildConfig.BASE_URL;

    public static final String remainderOfDomain = ".rootnetapp.com";

    public static String domain;
    private static final String WEB_PROTOCOL = "https://";

    public static final String SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String SERVER_DATE_FORMAT_SHORT = "yyyy-MM-dd";
    public static final String SERVER_DATE_FORMAT_NO_TIMEZONE = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String STANDARD_DATE_DISPLAY_FORMAT = "MMMM dd, yyyy";
    public static final String SHORT_DATE_DISPLAY_FORMAT = "dd/MM/yy";
    public static final String SHORT_DATE_NO_YEAR_FORMAT = "dd MMM";

    public static final String[] ALLOWED_MIME_TYPES = {
            "text/*",
            "image/*",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"
    };

    public static final String VIDEO_MIME_TYPE_CHECK = "video";

    public static String getImgDomain() {
        return imgDomain;
    }

    public static void setImgDomain(String newImgDomain) {
        if (TextUtils.isEmpty(Utils.imgDomain)) {
            Utils.imgDomain = WEB_PROTOCOL + newImgDomain;
            Utils.imgDomain = Utils.imgDomain.replace("v1/", "");
        }
    }

    public static String imgDomain;

    private static ProgressDialog progress;

    private static final String TAG = "Utils.Rootnet";

    private static final String ENCODING_UTF_8 = "UTF-8";

    public static void showLoading(Context ctx) {
        if (progress == null) {
            progress = new ProgressDialog(ctx);
        } else {
            if (progress.getContext() != ctx) {
                if (progress.isShowing()) progress.dismiss();
                progress = new ProgressDialog(ctx);
            }
        }

        if (!progress.isShowing()) {
            progress.show();
            progress.setCancelable(false);
            progress.setMessage(ctx.getString(R.string.loading_message));
        }
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

    /**
     * Receives ISO 8601 Date format and returns a UI ready date format for comments.
     *
     * @param isoStringDate Iso String from server.
     *
     * @return returns formatted Date
     */
    public static String serverFormatToFormat(String isoStringDate, String format) {
        if (isoStringDate == null) {
            return "";
        }
        String uiDateString;
        int semicolonPosition = isoStringDate.length() - 3;
        if (isoStringDate.charAt(semicolonPosition) != ':') {
            // Adjust semicolon position to string without semicolon.
            semicolonPosition = isoStringDate.length() - 2;
            String first = isoStringDate.substring(0, semicolonPosition);
            String second = isoStringDate.substring(semicolonPosition, semicolonPosition + 2);
            StringBuilder stringBuilder = new StringBuilder();
            isoStringDate = stringBuilder.append(first).append(":").append(second).toString();
        }

        uiDateString = DateTimeFormatter
                .ofPattern(format)
                .format(getDate(isoStringDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        return uiDateString;
    }

    public static String standardServerFormatTo(String isoStringDate, String format) {
        String uiDateString;
        uiDateString = DateTimeFormatter
                .ofPattern(format)
                .format(getDate(isoStringDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        return uiDateString;
    }

    private static ZoneId getZoneId() {
        // TODO we need the time zone of the server were the backend is hosted at.
        return ZoneId.of("UTC");
    }

    public static ZonedDateTime getDate(@NonNull String isoDateString,
                                        DateTimeFormatter dateTimeFormatter) {
        return ZonedDateTime
                .parse(isoDateString, dateTimeFormatter)
                .withZoneSameInstant(getZoneId());
    }

    /**
     * Converts a file formed from an URI into an array of bytes.
     *
     * @param contentResolver context ContentResolver, this is used to open the correct file.
     * @param uri             file URI.
     *
     * @return array of bytes that represents the file.
     *
     * @throws IOException thrown if any reading/writing operation fails.
     */
    public static byte[] fileToByte(ContentResolver contentResolver, Uri uri) throws IOException {
        try (InputStream in = contentResolver.openInputStream(uri)) {

            int size = in.available();

            byte bytes[] = new byte[size];
            byte tmpBuff[] = new byte[size];

            int read = in.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = in.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }

            return bytes;
        }
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

    public static String getCurrentFormattedDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(SERVER_DATE_FORMAT,
                Locale.getDefault());
        return df.format(c);
    }

    public static String getFormattedDateFromIntegers(int year, int month, int day, int hour,
                                                      int minute, int second) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        Date date = c.getTime();
        SimpleDateFormat df = new SimpleDateFormat(SERVER_DATE_FORMAT,
                Locale.getDefault());
        return df.format(date);
    }

    public static String getCurrentFormattedDateDaysDiff(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, days);
        Date date = c.getTime();
        SimpleDateFormat df = new SimpleDateFormat(SERVER_DATE_FORMAT,
                Locale.getDefault());
        return df.format(date);
    }

    public static String getMonthDay(int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        Date c = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return df.format(c);
    }

    public static Date getWeekStartDate(Calendar calendar) {
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getWeekEndDate(Calendar calendar) {
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static String replaceLast(String string, String substring, String replacement) {
        int index = string.lastIndexOf(substring);
        if (index == -1) {
            return string;
        }
        return string.substring(0, index) + replacement
                + string.substring(index + substring.length());
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
            byte[] bytes = Base64
                    .decode(string, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
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

    /**
     * Transforms a Base64 encoded string into a PDF {@link File} object. Also, saves the file
     * locally on the external downloads folder.
     *
     * @param base64   encoded string.
     * @param fileName name of the file to be saved.
     *
     * @return the file object that was created.
     *
     * @throws IOException exception caused by the decoding/saving operations.
     */
    public static File decodePdfFromBase64Binary(String base64,
                                                 String fileName) throws IOException {
        String downloadsPath = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";

        final File pdfFile = new File(downloadsPath + fileName + ".pdf");
        byte[] pdfAsBytes = Base64.decode(base64, Base64.DEFAULT);
        FileOutputStream os;
        os = new FileOutputStream(pdfFile, false);
        os.write(pdfAsBytes);
        os.flush();
        os.close();

        return pdfFile;
    }

    public static Uri saveBase64PdfToDownloads(ContentResolver contentResolver, String base64, String fileName) throws IOException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Downloads.IS_PENDING, 1);
        }

        Uri collection;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            collection = MediaStore.Downloads.getContentUri("external");
        }
        Uri item = contentResolver.insert(collection, contentValues);
       if (item == null) {
           throw new FileNotFoundException("Unable to reserve an URI for the downloaded base64 pdf content.");
       }
       try {
           ParcelFileDescriptor pfd = contentResolver.openFileDescriptor(item, "w");
           FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
           byte[] pdfAsBytes = Base64.decode(base64, Base64.DEFAULT);
           fileOutputStream.write(pdfAsBytes);
           // Let the document provider know you're done by closing the stream.
           fileOutputStream.close();
           pfd.close();

           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
               contentValues.clear();
               contentValues.put(MediaStore.Downloads.IS_PENDING, 0);
               contentResolver.update(item, contentValues, null, null);
           }

           return item;
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
       return null;
    }

    public static File decodePdfFromByteStream(InputStream inputStream,String fileName) throws IOException {
        int count;
        byte[] data = new byte[1024 * 4];
        InputStream bis = new BufferedInputStream(inputStream, 1024 * 8);
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        OutputStream output = new FileOutputStream(outputFile);
        while ((count = bis.read(data)) != -1) {
            output.write(data, 0, count);
        }
        output.flush();
        output.close();
        bis.close();
        return outputFile;
    }

    /**
     * Transforms a Base64 encoded string into a {@link File} object. Also, saves the file locally
     * on the external downloads folder.
     *
     * @param base64   encoded string.
     * @param fileName name of the file to be saved.
     *
     * @return the file object that was created.
     *
     * @throws IOException exception caused by the decoding/saving operations.
     */
    public static File decodeFileFromBase64Binary(String base64,
                                                  String fileName) throws IOException {
        String downloadsPath = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";

        final File file = new File(downloadsPath + fileName);
        byte[] fileAsBytes = Base64.decode(base64, Base64.DEFAULT);
        FileOutputStream os;
        os = new FileOutputStream(file, false);
        os.write(fileAsBytes);
        os.flush();
        os.close();

        return file;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

    public static Date getDateFromString(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            Log.d(TAG, "getDateFromString: ", e);
            return null;
        }
    }

    public static long getDateInMillisFromString(String date, String format) {
        Date dateObject = getDateFromString(date, format);
        if (dateObject == null) {
            return 0;
        } else {
            return dateObject.getTime();
        }
    }

    /**
     * Creates the given date in integers as a Date object
     *
     * @param year        year in integer
     * @param monthOfYear 0-11
     * @param dayOfMonth  1-31
     *
     * @return Date object
     */
    public static Date getDateFromIntegers(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return calendar.getTime();
    }

    /**
     * Formats the given date object to the required WS format.
     *
     * @param date date to format
     *
     * @return formatted date.
     */
    public static String getDatePostFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }

    public static String getDateFilterFormat(Date date, boolean isStart) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String result = sdf.format(date);
        if (isStart) {
            result = String.format("%s 00:00:00", result);
        } else {
            result = String.format("%s 23:59:59", result);
        }
        return result;
    }

    /**
     * Formats the given date object to the given format
     *
     * @param date date to format
     *
     * @return formatted date.
     */
    public static String getFormattedDate(Date date, String outputFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(outputFormat, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Formats the given date String to the specified format.
     *
     * @param strDate      date in String to format.
     * @param inputFormat  the format that the date is in.
     * @param outputFormat the output format for the given date.
     *
     * @return formatted date.
     */
    public static String getFormattedDate(String strDate, String inputFormat, String outputFormat) {
        SimpleDateFormat inputFormatter = new SimpleDateFormat(inputFormat, Locale.getDefault());
        try {
            Date date = inputFormatter.parse(strDate);
            SimpleDateFormat outputFormatter = new SimpleDateFormat(outputFormat,
                    Locale.getDefault());
            return outputFormatter.format(date);

        } catch (ParseException e) {
            Log.d(TAG, "getFormattedDate: ", e);
            return null;
        }
    }

    public static int getOnFailureStringRes(Throwable throwable) {
        int stringRes = R.string.failure_connect;

        if (throwable instanceof HttpException) {
            int httpCode = ((HttpException) throwable).code();
            if (httpCode == 403) stringRes = R.string.failure_connect_forbidden_access;
        }

        return stringRes;
    }

    public static boolean isInteger(String integerToTest) {
        return integerToTest.matches("-?\\d+");
    }

    /**
     * Converts a vector asset (xml) into a BitmapDescriptor object with the specified tint color.
     *
     * @param context      any context.
     * @param vectorResId  vector asset resource
     * @param tintColorRes color resource to tint the vector with.
     *
     * @return a BitmapDescriptor object of the tinted vector.
     */
    public static BitmapDescriptor bitmapDescriptorFromVector(Context context,
                                                              @DrawableRes int vectorResId,
                                                              @ColorRes int tintColorRes) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.mutate(); //avoid modifying all references of this drawable
        vectorDrawable.setTint(ContextCompat.getColor(context, tintColorRes));
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Resets the user configuration upon logout. This needs to be called when the user logs out.
     *
     * @param sharedPreferences preferences object.
     */
    public static void logout(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", "").apply();
        editor.putString("password", "").apply();
    }

    public static String getWebProtocol(String domain) {
        return WEB_PROTOCOL;
    }

    public static int secondsToHours(long seconds) {
        return (int) (seconds / (60 * 60));
    }

    public static byte[] decodeImageUri(String imageUri) {
        String base64EncodedString = imageUri.substring(imageUri.indexOf(",") + 1);
        return Base64.decode(base64EncodedString, Base64.DEFAULT);
    }
}
