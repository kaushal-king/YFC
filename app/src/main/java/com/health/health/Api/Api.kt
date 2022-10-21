import com.health.health.dataclass.gymlist
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers


interface Api {


    @GET("gyms")
    fun gymlist(
        @Header("Authorization")  token:String
    ): Call<gymlist>



}