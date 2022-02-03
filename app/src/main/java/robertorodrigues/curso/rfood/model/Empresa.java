package robertorodrigues.curso.rfood.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;

public class Empresa implements Serializable, Parcelable {

    private String idUsuario;
    private String urlImagem;
    private String nome;
    private String tempo; // tempo de entrega
    private String categoria;
    private String precoEntrega;  // mudar para double depois
    private String tokenEmpresa;

    public Empresa() {
    }

    protected Empresa(Parcel in) {
        idUsuario = in.readString();
        tokenEmpresa = in.readString();
        urlImagem = in.readString();
        nome = in.readString();
        tempo = in.readString();
        categoria = in.readString();
        precoEntrega = in.readString();
    }

    public static final Creator<Empresa> CREATOR = new Creator<Empresa>() {
        @Override
        public Empresa createFromParcel(Parcel in) {
            return new Empresa(in);
        }

        @Override
        public Empresa[] newArray(int size) {
            return new Empresa[size];
        }
    };

    public  void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference empresaRef = firebaseRef.child("empresas")
                .child(getIdUsuario());
        empresaRef.setValue(this);
    }



    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPrecoEntrega() {
        return precoEntrega;
    }

    public void setPrecoEntrega(String precoEntrega) {
        this.precoEntrega = precoEntrega;
    }

    public String getTokenEmpresa() {
        return tokenEmpresa;
    }

    public void setTokenEmpresa(String tokenEmpresa) {
        this.tokenEmpresa = tokenEmpresa;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idUsuario);
        dest.writeString(tokenEmpresa);
        dest.writeString(urlImagem);
        dest.writeString(nome);
        dest.writeString(tempo);
        dest.writeString(categoria);
        dest.writeString(precoEntrega);
    }
}
