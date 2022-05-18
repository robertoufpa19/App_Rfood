package robertorodrigues.curso.rfood.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.rfood.helper.UsuarioFirebase;

public class Conversa implements Serializable{

    private String idRemetente;
    private String idDestinatario;
    private String ultimaMensagem;
    private Usuario usuarioExibicao;
    private Pedido usuarioExibicaoPedido;
    private Empresa empresaExibicao;
    private String isEmpresa;


  //  private Grupo grupo;

    public Conversa() {
       // this.setIsEmpresa("false");
      //  this.setIsGrupo("false");
    }




    public void salvar(){

        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference conversaRef = database.child("conversas");

        conversaRef.child( this.getIdRemetente() )
                .child( this.getIdDestinatario() )
                .setValue( this );

    }


    public void removerConversa() {


        String identificadorUsuario = UsuarioFirebase.getIdUsuario();

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference conversasRef = firebaseRef
                .child("conversas")
                .child(identificadorUsuario);
        conversasRef.removeValue();

    }

    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public Usuario getUsuarioExibicao() {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(Usuario usuarioExibicao) {
        this.usuarioExibicao = usuarioExibicao;
    }

    public Empresa getEmpresaExibicao() {
        return empresaExibicao;
    }

    public void setEmpresaExibicao(Empresa empresaExibicao) {
        this.empresaExibicao = empresaExibicao;
    }

    public Pedido getUsuarioExibicaoPedido() {
        return usuarioExibicaoPedido;
    }

    public void setUsuarioExibicaoPedido(Pedido usuarioExibicaoPedido) {
        this.usuarioExibicaoPedido = usuarioExibicaoPedido;
    }

    public String getIsEmpresa() {
        return isEmpresa;
    }

    public void setIsEmpresa(String isEmpresa) {
        this.isEmpresa = isEmpresa;
    }


}
