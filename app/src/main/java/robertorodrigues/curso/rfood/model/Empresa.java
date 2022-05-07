package robertorodrigues.curso.rfood.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;

public class Empresa implements Serializable{

    private String idUsuario; // id empresa
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

}
