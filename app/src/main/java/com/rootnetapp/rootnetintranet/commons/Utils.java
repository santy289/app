package com.rootnetapp.rootnetintranet.commons;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.rootnetapp.rootnetintranet.BuildConfig;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.login.JWToken;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Utils {

    public static final String URL = BuildConfig.BASE_URL;

    public static final String remainderOfDomain = ".rootnetapp.com";

    public static String domain;

    public static final String SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String STANDARD_DATE_DISPLAY_FORMAT = "MMMM dd, yyyy";

    public static String getImgDomain() {
        return imgDomain;
    }

    public static void setImgDomain(String newImgDomain) {
        if (TextUtils.isEmpty(Utils.imgDomain)) {
            Utils.imgDomain = "http://" + newImgDomain;
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

    public static String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return df.format(c);
    }

    public static String getMonthDay(int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        Date c = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return df.format(c);
    }

    public static String getWeekStart() {
        Calendar calendar = Calendar.getInstance();
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        Date c = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return df.format(c);
    }

    public static String getWeekEnd() {
        Calendar calendar = Calendar.getInstance();
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        Date c = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return df.format(c);
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
        byte[] pdfAsBytes = Base64.decode(base64, 0);
        FileOutputStream os;
        os = new FileOutputStream(pdfFile, false);
        os.write(pdfAsBytes);
        os.flush();
        os.close();

        return pdfFile;
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

        final File pdfFile = new File(downloadsPath + fileName);
        byte[] pdfAsBytes = Base64.decode(base64, 0);
        FileOutputStream os;
        os = new FileOutputStream(pdfFile, false);
        os.write(pdfAsBytes);
        os.flush();
        os.close();

        return pdfFile;
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
     * @param strDate date in String to format.
     * @param inputFormat the format that the date is in.
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
}
