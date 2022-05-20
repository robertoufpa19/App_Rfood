package robertorodrigues.curso.rfood.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
    private String ultimaConversa;

    private String horaConversa;
    private String dataConversa;


    public Conversa() {

    }



    public void salvarHora(){

        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference conversaRef = database.child("conversas_hora");

        conversaRef.child(this.getHoraConversa())
                .child( this.getIdRemetente() )
                .child( this.getIdDestinatario() )
                .setValue( this );

    }



    public void salvar(){

        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference conversaRef = database.child("conversas");

        conversaRef.child( this.getIdRemetente() )
                .child( this.getIdDestinatario() )
                .setValue( this );

    }


    public void removerConversaHora() {


        String identificadorUsuario = UsuarioFirebase.getIdUsuario();

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference conversasRef = firebaseRef
                .child("conversas")
                .child(getHoraConversa())
                .child(identificadorUsuario);
        conversasRef.removeValue();



    }

    public void removerConversa() {


        String identificadorUsuario = UsuarioFirebase.getIdUsuario();

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference conversasRef = firebaseRef
                .child("conversas")
                .child(identificadorUsuario);
        conversasRef.removeValue();



    }


    public void atualizar(){

        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuariosRef = database.child("conversas")
                .child(this.getIdRemetente())
                .child(this.getIdDestinatario());
        Map<String, Object> valoresUsuario =  converterParaMap();
        usuariosRef.updateChildren(valoresUsuario);


    }




    @Exclude
    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("idRemetente", getIdRemetente());
        usuarioMap.put("idDestinatario", getIdDestinatario());
        usuarioMap.put("ultimaMensagem", getUltimaMensagem());
        usuarioMap.put("usuarioExibicao", getUsuarioExibicao());
        usuarioMap.put("usuarioExibicaoPedido", getUsuarioExibicaoPedido());
        usuarioMap.put("isEmpresa", getIsEmpresa());
        usuarioMap.put("horaConversa", getHoraConversa());
        usuarioMap.put("dataConversa", getDataConversa());

        return  usuarioMap;

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

    public String getUltimaConversa() {
        return ultimaConversa;
    }

    public void setUltimaConversa(String ultimaConversa) {
        this.ultimaConversa = ultimaConversa;
    }

    public String getHoraConversa() {
        return horaConversa;
    }

    public void setHoraConversa(String horaConversa) {
        this.horaConversa = horaConversa;
    }

    public String getDataConversa() {
        return dataConversa;
    }

    public void setDataConversa(String dataConversa) {
        this.dataConversa = dataConversa;
    }
}
