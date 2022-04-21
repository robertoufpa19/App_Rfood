package robertorodrigues.curso.rfood.model;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;

public class Produto {
    private String idUsuario;
    private String idProduto;
    private String nome;
    private String descricao;
    private String preco;  // mudar para Double depois


    String avaliacao ;
    String nomeVendedor;
    String fotoVendedor;
    String tokenVendedor;
    private Empresa empresaExibicao;
    private List<String> fotosProduto;

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

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }


    public String getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(String avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getNomeVendedor() {
        return nomeVendedor;
    }

    public void setNomeVendedor(String nomeVendedor) {
        this.nomeVendedor = nomeVendedor;
    }

    public String getFotoVendedor() {
        return fotoVendedor;
    }

    public void setFotoVendedor(String fotoVendedor) {
        this.fotoVendedor = fotoVendedor;
    }

    public String getTokenVendedor() {
        return tokenVendedor;
    }

    public void setTokenVendedor(String tokenVendedor) {
        this.tokenVendedor = tokenVendedor;
    }

    public Empresa getEmpresaExibicao() {
        return empresaExibicao;
    }

    public void setEmpresaExibicao(Empresa empresaExibicao) {
        this.empresaExibicao = empresaExibicao;
    }

    public List<String> getFotosProduto() {
        return fotosProduto;
    }

    public void setFotosProduto(List<String> fotosProduto) {
        this.fotosProduto = fotosProduto;
    }
}
