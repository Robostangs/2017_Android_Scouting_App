package me.a548.qrdisplay1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static me.a548.qrdisplay1.MainActivity.QRcodeWidth;

public class DisplayActivity extends AppCompatActivity {

    private ImageView iv;
    private Button nextMatch;
    Bitmap bitmap;
    private static final String IMAGE_DIRECTORY = "/QRcodes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent dataSend =  getIntent();
        String data = dataSend.getExtras().getString("dat");

        iv = (ImageView) findViewById(R.id.iv);
        nextMatch = (Button) findViewById(R.id.nextMatch);

        try {
            //Takes inputs and turns them into a qr code
            bitmap = TextToImageEncode(data);
            iv.setImageBitmap(bitmap);

            String path = saveImage(bitmap);
            Toast.makeText(DisplayActivity.this, "QRCode saved to -> "+path, Toast.LENGTH_LONG).show();
        } catch (WriterException e) {
            e.printStackTrace();
        }

        nextMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(DisplayActivity.this, MainActivity.class);
                startActivity(back);
            }
        });

    }

    public String saveImage(Bitmap myBitmap) {
        Intent dataSend =  getIntent();
        String data = dataSend.getExtras().getString("dat");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists()) {
            Log.d("dirrrrrr", "" + wallpaperDirectory.mkdirs());
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, data + ".jpg");
            f.createNewFile();   //give read write permission
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";

    }


    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

}
