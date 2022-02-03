package robertorodrigues.curso.rfood.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import robertorodrigues.curso.rfood.R;
import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.rfood.helper.UsuarioFirebase;
import robertorodrigues.curso.rfood.model.Produto;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editProdutoNome, editProdutoDescricao, editProdutoPreco;
    private ImageView imagePerfilProduto;
    private String idUsuarioLogado;
    private String idProduto ;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String urlImagemSelecionada = "";
    private static  final int SELECAO_GALERIA = 200;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        // configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // configuracoes iniciais
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        inicializarComponentes();

        imagePerfilProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);

                }

            }
        });

    }




    public  void validarDadosProduto(View view){

        // validar os campos que foram preenchidos
        String nome = editProdutoNome.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();
        String preco = editProdutoPreco.getText().toString();
        String foto = urlImagemSelecionada;

        if(foto != "") {
            if (!nome.isEmpty()) {
                if (!descricao.isEmpty()) {
                    if (!preco.isEmpty()) {

                        Produto produto = new Produto();
                        produto.setIdUsuario(idUsuarioLogado);
                        produto.setNome(nome);
                        produto.setDescricao(descricao);
                        produto.setPreco(Double.parseDouble(preco)); //teste
                        produto.setUrlImagem(urlImagemSelecionada);
                        produto.salvar();
                        finish();
                        exibirMensagem("Produto Salvo com sucesso");

                    } else {
                        exibirMensagem("Digite um preco para o produto!!");
                    }

                } else {
                    exibirMensagem("Digite uma descricao para o produto!!");
                }

            } else {
                exibirMensagem("Digite um nome para o produto!");
            }

        }else{
            exibirMensagem("Configure uma foto para o Produto!");
        }

    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                switch (requestCode){
                    case  SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagem);
                        break;
                }

                if(imagem != null){
                    imagePerfilProduto.setImageBitmap(imagem);


                    // fazer upload da imagem para o firebase storage
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // cria nome que não se repete
                    String nomeImagem = UUID.randomUUID().toString();

                    dialog = new SpotsDialog.Builder()
                            .setContext(this)
                            .setMessage("Carregando dados")
                            .setCancelable(false)
                            .build();
                    dialog.show();

                    final  StorageReference imageRef = storageReference
                            .child("imagens")
                            .child("produtos")
                            .child(nomeImagem + "jpeg");



                    UploadTask uploadTask = imageRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NovoProdutoEmpresaActivity.this,
                                    "Erro ao fazer upload da imagem!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // recupera a url da imagem (versao atualizada do firebase)
                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url =   task.getResult();
                                    urlImagemSelecionada = url.toString();

                                }
                            });

                            dialog.dismiss(); // fecha o carregando

                            Toast.makeText(NovoProdutoEmpresaActivity.this,
                                    "Sucesso ao fazer upload da imagem!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void inicializarComponentes(){
        editProdutoDescricao = findViewById(R.id.editDescricaoNovoProduto);
        editProdutoNome = findViewById(R.id.nomeNovoProduto);
        editProdutoPreco = findViewById(R.id.editPrecoNovoProduto);
        imagePerfilProduto = findViewById(R.id.imagePerfilProduto);



    }
}