package robertorodrigues.curso.rfood.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import robertorodrigues.curso.rfood.model.Usuario;

public class UsuarioFirebase {

    public  static  String getIdUsuario(){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return  autenticacao.getCurrentUser().getUid();
    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return  usuario.getCurrentUser();
    }

    public  static Usuario getDadosUsuarioLogado(){

        FirebaseUser firebaseUser = getUsuarioAtual();
        Usuario usuario = new Usuario();
        //usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName());
        //  usuario.setToken(""); // falta recuperar token do usuario logado pra notificar no grupo



        if(firebaseUser.getPhotoUrl() == null){
            usuario.setUrlImagem("");
        }else{
            usuario.setUrlImagem(firebaseUser.getPhotoUrl().toString());

        }

        return  usuario;
    }




    public  static  boolean atualizarTipoUsuario(String tipo){

        try {
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(tipo)
                    .build();
            user.updateProfile(profile);
            return  true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }



}

