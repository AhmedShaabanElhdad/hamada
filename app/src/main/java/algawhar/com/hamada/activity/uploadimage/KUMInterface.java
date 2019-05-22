package algawhar.com.hamada.activity.uploadimage;



import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface KUMInterface {



    @FormUrlEncoded
    @POST("classify-system")
    Call<ResultModel> uploadImage(
            @Field("imageurl") String imageurl
    );


    @FormUrlEncoded
    @POST("classify-system")
    Call<ResultModel> recognizeImage(
            @Field("imageurl") String imageurl
    );



}
