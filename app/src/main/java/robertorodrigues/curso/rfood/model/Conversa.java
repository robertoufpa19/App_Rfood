package robertorodrigues.curso.rfood.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;

public class Conversa implements Serializable, Parcelable {

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

    protected Conversa(Parcel in) {
        idRemetente = in.readString();
        idDestinatario = in.readString();
        ultimaMensagem = in.readString();
        usuarioExibicaoPedido = in.readParcelable(Pedido.class.getClassLoader());
        empresaExibicao = in.readParcelable(Pedido.class.getClassLoader());
        isEmpresa = in.readString();

    }

    public static final Creator<Conversa> CREATOR = new Creator<Conversa>() {
        @Override
        public Conversa createFromParcel(Parcel in) {
            return new Conversa(in);
        }

        @Override
        public Conversa[] newArray(int size) {
            return new Conversa[size];
        }
    };

    public void salvar(){

        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference conversaRef = database.child("conversas");

        conversaRef.child( this.getIdRemetente() )
                .child( this.getIdDestinatario() )
                .setValue( this );

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idRemetente);
        dest.writeString(idDestinatario);
        dest.writeString(ultimaMensagem);
        dest.writeParcelable(usuarioExibicaoPedido, flags);
        dest.writeParcelable(empresaExibicao, flags);
        dest.writeString(isEmpresa);

    }
}
