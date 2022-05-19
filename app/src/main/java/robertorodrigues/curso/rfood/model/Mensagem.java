package robertorodrigues.curso.rfood.model;

public class Mensagem {
    private String idUsuario;
    private String nome;
    private String mensagem;
    private String imagem;
    private String tokenUsuario;

    private String horaMensagem;
    private String dataMensagem;

    public Mensagem() {
        this.setNome("");
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

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getTokenUsuario() {
        return tokenUsuario;
    }

    public void setTokenUsuario(String tokenUsuario) {
        this.tokenUsuario = tokenUsuario;
    }

    public String getHoraMensagem() {
        return horaMensagem;
    }

    public void setHoraMensagem(String horaMensagem) {
        this.horaMensagem = horaMensagem;
    }

    public String getDataMensagem() {
        return dataMensagem;
    }

    public void setDataMensagem(String dataMensagem) {
        this.dataMensagem = dataMensagem;
    }
}
