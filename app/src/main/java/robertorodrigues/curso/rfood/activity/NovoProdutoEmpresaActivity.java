package robertorodrigues.curso.rfood.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskedittext.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import me.abhinay.input.CurrencyEditText;
import robertorodrigues.curso.rfood.R;
import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.rfood.helper.Permissoes;
import robertorodrigues.curso.rfood.helper.UsuarioFirebase;
import robertorodrigues.curso.rfood.model.Empresa;
import robertorodrigues.curso.rfood.model.Produto;

public class NovoProdutoEmpresaActivity extends AppCompatActivity   implements View.OnClickListener{

   /* private EditText editProdutoNome, editProdutoDescricao, editProdutoPreco;

    private String idUsuarioLogado;
    private String idProduto ;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String urlImagemSelecionada = "";
    private static  final int SELECAO_GALERIA = 200; */



    /// novos

    private EditText campoTitulo, campoDescricao;
    private CurrencyEditText campoValor;
    private ImageView imagem1, imagem2, imagem3, imagem4;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private List<String> listaFotosRecuperadas = new ArrayList<>(); // lista o caminho de fotos no dispositivo do usuario
    private List<String> listaURLFotos = new ArrayList<>(); // lista o caminho de fotos no firebase

    private Produto produto;
    private StorageReference storage;
    private AlertDialog dialog;

    private Empresa empresa;
    private String idEmpresaLogado;
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        // configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inicializarComponentes();

        //configuracoes iniciais
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        idEmpresaLogado = UsuarioFirebase.getIdUsuario();

        //validar permissoes
        Permissoes.validarPermissoes(permissoes, this, 1);

        recuperarDadosEmpresa();

        empresa = UsuarioFirebase.getDadosEmpresaLogado();



    }

    public void salvarAnuncio(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Produto!")
                .setCancelable(false)
                .build();
        dialog.show();

        // salvar a imagem no storage
        // o indice ZERO corresponde a imagem 1
        for(int i =0; i < listaFotosRecuperadas.size(); i++){
            String urlImagem = listaFotosRecuperadas.get(i);
            int tamanhoLIsta = listaFotosRecuperadas.size();

            salvarFotoStorage( urlImagem, tamanhoLIsta, i);

        }

    }
    private void salvarFotoStorage(String urlString, int totalFotos, int contador){

        //criar no no storage
        final StorageReference imagemAnuncio = storage.child("imagens")
                .child("produtos")
                .child(produto.getIdProduto())
                .child("imagem"+contador); //imagem1, imagem2, imagem3

        //fazer upload das imagens
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imagemAnuncio.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        Uri firebaseUrl = task.getResult();

                        String urlConvertida = firebaseUrl.toString();
                        listaURLFotos.add(urlConvertida);

                        if(totalFotos == listaURLFotos.size()){
                            produto.setFotosProduto(listaURLFotos);
                            produto.salvar();
                            dialog.dismiss(); // fecha dialog
                            finish(); // finaliza a activity

                        }
                    }
                });

                exibirMensagem("Sucesso ao cadastrar produto!");


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                exibirMensagem("Erro ao fazer upload das imagem!");
            }
        });

    }



    private Produto configurarAnuncio(){

        String titulo = campoTitulo.getText().toString();
        String valor = campoValor.getText().toString();
        String descricao = campoDescricao.getText().toString();

        produto = new Produto();
        produto.setNome(titulo);
        produto.setPreco( valor);
        produto.setDescricao(descricao);
        //falta salvar foto e nome do vendedor
        if(empresa != null){
            String  nomeVendedor =  empresa.getNome();
            String  fotoVendedor = empresa.getUrlImagem();
            String idUsuario = ConfiguracaoFirebase.getIdUsuario();
            produto.setNomeVendedor(nomeVendedor);
            produto.setFotoVendedor(fotoVendedor);
            produto.setIdUsuario(idUsuario);
            produto.setEmpresaExibicao(empresa);
        }



        return  produto;

    }




    private void recuperarDadosEmpresa(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference usuarioRef = firebaseRef
                .child("empresas")
                .child(idEmpresaLogado);
        // recupera dados uma unica vez
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                    empresa = snapshot.getValue(Empresa.class);
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




    public  void validarDadosProduto(View view){

        produto = configurarAnuncio();

        if(listaFotosRecuperadas.size() != 0){

                    if(!produto.getNome().isEmpty()){
                        if(!produto.getPreco().equals("0")){
                                if(!produto.getDescricao().isEmpty()){

                                    salvarAnuncio();

                                }else {
                                    exibirMensagem("Digite uma descricao");
                                }

                        }else{
                            exibirMensagem("Digite um valor");
                        }

                    }else{
                        exibirMensagem("Digite um titulo");
                    }
        }else{
            exibirMensagem("Selecione uma foto!");
        }

    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onClick(View v) {


        switch (v.getId()){
            case R.id.imageCadastro1:
                escolherImagem(1);
                break;

            case R.id.imageCadastro2:
                escolherImagem(2);
                break;

            case R.id.imageCadastro3:
                escolherImagem(3);
                break;

            case R.id.imageCadastro4:
                escolherImagem(4);
                break;


        }

    }

    public  void escolherImagem(int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    // captura a imagem escolhida
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            //recuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //configiurar imagem no ImageView
            if(requestCode == 1){
                imagem1.setImageURI(imagemSelecionada);

            }else if(requestCode == 2){
                imagem2.setImageURI(imagemSelecionada);
            }else if(requestCode == 3){
                imagem3.setImageURI(imagemSelecionada);
            }else if(requestCode == 4){
                imagem4.setImageURI(imagemSelecionada);
            }

            listaFotosRecuperadas.add(caminhoImagem);
        }
    }



    private  void inicializarComponentes(){
        campoDescricao = findViewById(R.id.editDescricao);
        campoTitulo = findViewById(R.id.editTitulo);
        campoValor = findViewById(R.id.editValor);
        imagem1 = findViewById(R.id.imageCadastro1);
        imagem2 = findViewById(R.id.imageCadastro2);
        imagem3 = findViewById(R.id.imageCadastro3);
        imagem4 = findViewById(R.id.imageCadastro4);


        imagem1.setOnClickListener(this);
        imagem2.setOnClickListener(this);
        imagem3.setOnClickListener(this);
        imagem4.setOnClickListener(this);

        // configurar localidade para pt -> portugues BR -> Brasil
        Locale locale = new Locale("pt", "BR");
        campoValor.setTextLocale(locale);
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for( int permissaoResultado : grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidarPermissao();
            }
        }
    }


    private void alertaValidarPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissoes Negadas");
        builder.setMessage("Para utilizar o app e necessario aceitar as permissoes");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}