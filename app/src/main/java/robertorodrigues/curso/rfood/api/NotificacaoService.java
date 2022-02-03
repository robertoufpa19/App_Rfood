package robertorodrigues.curso.rfood.api;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import robertorodrigues.curso.rfood.model.NotificacaoDados;

public interface NotificacaoService {

    @Headers({
            "Authorization:key=AAAAENQ62S0:APA91bESyQUAu4k-mUK7jf4OVzWkCPhmyOhgsvmAJisVRPhlaY2lY4YhSWJm01nmzDuQPDTljodUAN366PtmFOPMOENGLBUd4enr2KC2wonUSdk_xr46Nixjqp80yzR7nVXsc5L7FVZu",
            "Content-Type:application/json"
    })
    @POST("send")
    Call<NotificacaoDados> salvarNotificacao(@Body NotificacaoDados notificacaoDados);
}
