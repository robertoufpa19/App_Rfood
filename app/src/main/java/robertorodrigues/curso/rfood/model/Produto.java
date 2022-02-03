package robertorodrigues.curso.rfood.model;

import com.google.firebase.database.DatabaseReference;

import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;

public class Produto {
    private String idUsuario;
    private String idProduto;
    private String nome;
    private String descricao;
    private Double preco;  // mudar para Double depois
    private String urlImagem;

    public Produto() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference produtoRef = firebaseRef.child("produtos");
         setIdProduto(produtoRef.push().getKey());
    }

    public  void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference produtoRef = firebaseRef.child("produtos")
                .child(getIdUsuario()) // idEmpresa
                .child(getIdProduto());
        produtoRef.setValue(this);
    }
    public  void remover(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference produtoRef = firebaseRef.child("produtos")
                .child(getIdUsuario())
                .child(getIdProduto());
        produtoRef.removeValue();
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }
}
