package algawhar.com.hamada.activity.uploadimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


import algawhar.com.hamada.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AddCustomProductActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int IMAGE_REQUEST = 120;
    ImageButton product_image;
    TextView txt_name;

    String image_base64;
    Bitmap bitmap;
    KUMInterface kumInterface;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadimage);


        product_image = findViewById(R.id.product_image);
        Button btn_upload = findViewById(R.id.btn_upload);
        Button btn_recognize = findViewById(R.id.btn_recognize);


        product_image.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
        btn_recognize.setOnClickListener(this);


        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://192.168.2.20:4000/")
                .build();



        kumInterface = retrofit.create(KUMInterface.class);


        txt_name = findViewById(R.id.txt_name);

    }




    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_upload:

                Call<ResultModel> connection = kumInterface.uploadImage(image_base64);
                connection.enqueue(new Callback<ResultModel>() {
                    @Override
                    public void onResponse(Call<ResultModel> call, retrofit2.Response<ResultModel> response) {

                        Log.e("response", new GsonBuilder().setPrettyPrinting().create().toJson(response));

                        int status = response.body().getStatus();
                        if (status == 1) {
                            Toast.makeText(AddCustomProductActivity.this, "تم رفع الصورة بنجاح", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddCustomProductActivity.this, "مشكلة فى ال api", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultModel> call, Throwable t) {
                        Toast.makeText(AddCustomProductActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                break;

            case R.id.btn_recognize:
                Call<ResultModel> recognizeConnection = kumInterface.uploadImage(image_base64);
                recognizeConnection.enqueue(new Callback<ResultModel>() {
                    @Override
                    public void onResponse(Call<ResultModel> call, retrofit2.Response<ResultModel> response) {

                        Log.e("response", new GsonBuilder().setPrettyPrinting().create().toJson(response));

                        int status = response.body().getStatus();
                        if (status == 1) {
                            if (response.body().getName()==null)
                                Toast.makeText(AddCustomProductActivity.this, "مشكلة فى ال json", Toast.LENGTH_SHORT).show();
                            else
                                txt_name.setText(response.body().getName());
                        } else {
                            Toast.makeText(AddCustomProductActivity.this, "مشكلة فى ال api", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultModel> call, Throwable t) {
                        Toast.makeText(AddCustomProductActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                break;

            case R.id.product_image:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_REQUEST);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_REQUEST) {
            Uri Imageuri = data.getData();
            try {
                ConvertImageToBitMab(Imageuri);
            } catch (Exception e) {
            }

        }
    }


    void ConvertImageToBitMab(Uri uri) throws IOException {

        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        //Toast.makeText(this, encodeTobase64(bitmap) + "", Toast.LENGTH_SHORT).show();
        product_image.setImageBitmap(bitmap);

        image_base64 = encodeTobase64(bitmap);
    }

    public String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }
}
