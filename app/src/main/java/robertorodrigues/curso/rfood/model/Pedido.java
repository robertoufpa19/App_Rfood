package robertorodrigues.curso.rfood.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;

// corrige o erro ao seleciona um pedido e abrir o chat
public class Pedido implements Serializable, Parcelable {

    private String idUsuario; // usuario
    private String nome; // usuario
    private String nomeEmpresa; // usuario
    private String cidade ;
    private String bairro ;
    private String rua ;
    private String numero ;
    private String telefone;
    private String urlImagem; //foto do usuario
    private String urlImagemEmpresa; //foto da empresa


    private String idEmpresa;
    private String idPedido;
    private List<ItemPedido> itens;
    private Double total;
    private String Status = "pendente";
    private int metodoPagamento;
    private String observacao;

    private String tokenEmpresa;
    private String tokenUsuario;


    public Pedido() {
    }
                   // idUsuario e idEmpresa
    public Pedido(String idUsu, String idEmp){
           setIdUsuario(idUsu);
           setIdEmpresa(idEmp);

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuarios")
                .child(idEmp)
                .child(idUsu);
        setIdPedido(pedidoRef.push().getKey());
    }

    protected Pedido(Parcel in) {
        idUsuario = in.readString();
        nome = in.readString();
        nomeEmpresa = in.readString();
        tokenEmpresa = in.readString();
        tokenUsuario = in.readString();
        cidade = in.readString();
        bairro = in.readString();
        rua = in.readString();
        numero = in.readString();
        telefone = in.readString();
        urlImagem = in.readString();
        urlImagemEmpresa = in.readString();
        idEmpresa = in.readString();
        idPedido = in.readString();
        if (in.readByte() == 0) {
            total = null;
        } else {
            total = in.readDouble();
        }
        Status = in.readString();
        metodoPagamento = in.readInt();
        observacao = in.readString();
    }

    public static final Creator<Pedido> CREATOR = new Creator<Pedido>() {
        @Override
        public Pedido createFromParcel(Parcel in) {
            return new Pedido(in);
        }

        @Override
        public Pedido[] newArray(int size) {
            return new Pedido[size];
        }
    };

    public  void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuarios")   // "NO" temporario para salvar pedidos do usurario
                .child(getIdEmpresa())
                .child(getIdUsuario());
        pedidoRef.setValue(this);
    }
  // nao preciso remover. Pois quero que o usuario veja o que pediu
    public  void removerPedido(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuarios")
                .child(getIdEmpresa())
                .child(getIdUsuario());
       pedidoRef.removeValue();
    }

    public  void confirmar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child(getIdEmpresa())
                .child(getIdPedido());
        pedidoRef.setValue(this);

        confirmarPedidoUsuario();

    }

    public  void confirmarPedidoUsuario(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pedidoRef = firebaseRef
                .child("meus_pedidos")
                .child(getIdUsuario())
                .child(getIdPedido());
        pedidoRef.setValue(this);
    }

    public void atualizarStatus(){

        HashMap<String, Object> status = new HashMap<>();
        status.put("status", getStatus());

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child(getIdEmpresa())
                .child(getIdPedido());
        pedidoRef.updateChildren(status);
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(int metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getUrlImagemEmpresa() {
        return urlImagemEmpresa;
    }

    public void setUrlImagemEmpresa(String urlImagemEmpresa) {
        this.urlImagemEmpresa = urlImagemEmpresa;
    }

    public String getTokenEmpresa() {
        return tokenEmpresa;
    }

    public void setTokenEmpresa(String tokenEmpresa) {
        this.tokenEmpresa = tokenEmpresa;
    }

    public String getTokenUsuario() {
        return tokenUsuario;
    }

    public void setTokenUsuario(String tokenUsuario) {
        this.tokenUsuario = tokenUsuario;
    }

                                                 // corrige o erro ao seleciona um pedido e abrir o chat

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idUsuario);
        dest.writeString(nome);
        dest.writeString(nomeEmpresa);
        dest.writeString(tokenEmpresa);
        dest.writeString(tokenUsuario);
        dest.writeString(cidade);
        dest.writeString(bairro);
        dest.writeString(rua);
        dest.writeString(numero);
        dest.writeString(telefone);
        dest.writeString(urlImagem);
        dest.writeString(urlImagemEmpresa);
        dest.writeString(idEmpresa);
        dest.writeString(idPedido);
        if (total == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(total);
        }
        dest.writeString(Status);
        dest.writeInt(metodoPagamento);
        dest.writeString(observacao);
    }
}
