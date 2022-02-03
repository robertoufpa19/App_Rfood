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
import android.util.Log;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import dmax.dialog.SpotsDialog;
import robertorodrigues.curso.rfood.R;
import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.rfood.helper.UsuarioFirebase;
import robertorodrigues.curso.rfood.model.Usuario;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {
    private EditText editUsuarioNome, editUsuarioCidade, editUsuarioBairro,
            editUsuarioRua, editUsuarioNumeroCasa,editUsuarioTelefone;
    
    private ImageView imagePerfilUsuario;

    private static  final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuario);
        // configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configuracoes");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //configuracoes iniciais
        inicializarComponentes();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        imagePerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);

                }

            }
        });



        //recuperar dados do usuario
        recuperarDadosUsuario();
    }

    private  void recuperarDadosUsuario(){
        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuarioLogado);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue() != null){
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    editUsuarioNome.setText(usuario.getNome());
                    editUsuarioCidade.setText(usuario.getCidade());
                    editUsuarioBairro.setText(usuario.getBairro());
                    editUsuarioRua.setText(usuario.getRua());
                    editUsuarioNumeroCasa.setText(usuario.getNumero());
                    editUsuarioTelefone.setText(usuario.getTelefone());

                    //recuperar imagem de perfil da empresa
                    urlImagemSelecionada = usuario.getUrlImagem();
                    if (urlImagemSelecionada != ""){
                        Picasso.get()
                                .load(urlImagemSelecionada)
                                .into(imagePerfilUsuario);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void validarDadosUsuario(View view){  // metodo salvar

        // validar os campos que foram preenchidos
        String nome = editUsuarioNome.getText().toString();
        String cidade = editUsuarioCidade.getText().toString();
        String bairro = editUsuarioBairro.getText().toString();
        String rua = editUsuarioRua.getText().toString();
        String numero = editUsuarioNumeroCasa.getText().toString();
        String telefone = editUsuarioTelefone.getText().toString();

        String foto = urlImagemSelecionada;


        if(foto != "") {
            if (!nome.isEmpty()) {
                if (!cidade.isEmpty()) {
                    if (!bairro.isEmpty()) {
                        if (!rua.isEmpty()) {
                            if (!numero.isEmpty()) {
                                if (!telefone.isEmpty()) {


                                    Usuario usuario = new Usuario();
                                    usuario.setIdUsuario(idUsuarioLogado);
                                    usuario.setNome(nome);
                                    usuario.setCidade(cidade);
                                    usuario.setBairro(bairro);
                                    usuario.setRua(rua);
                                    usuario.setNumero(numero);
                                    usuario.setTelefone(telefone);
                                    usuario.setUrlImagem(urlImagemSelecionada);

                                    // indentificador Usuario Token para enviar notificação para um usuario
                                    // inicio cadastro do token usuario
                                    FirebaseMessaging.getInstance().getToken()
                                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                                @Override
                                                public void onComplete(@NonNull Task<String> task) {
                                                    if (!task.isSuccessful()) {
                                                        Log.w("Cadastro token", "Fetching FCM registration token failed", task.getException());
                                                        return;
                                                    }

                                                    // Get new FCM registration token
                                                    String token = task.getResult();
                                                    usuario.setTokenUsuario(token);

                                                    usuario.salvar();
                                                    exibirMensagem("Dados atualizados");
                                                    finish();//
                                                    abrirHome(); // para dar tempo de recarregar os dados do usuario


                                                }
                                            });    // fim cadastro do token


                                } else { //telefone
                                    exibirMensagem("Digite seu numero de telefone!");
                                }

                            } else {
                                exibirMensagem("Digite o numero da sua casa!");
                            }

                        } else {
                            exibirMensagem("Digite o nome da sua rua!");
                        }

                    } else {
                        exibirMensagem("Digite o nome do seu bairro!");
                    }

                } else {
                    exibirMensagem("Digite o nome da sua cidade!");
                }


            } else {
                exibirMensagem("Digite seu nome!");
            }

        }else {
            exibirMensagem("Configure uma foto de Perfil!");
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
                    imagePerfilUsuario.setImageBitmap(imagem);

                    // fazer upload da imagem para o firebase storage
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // fazer upload da imagem antes de preencher outros campos
                    dialog = new SpotsDialog.Builder()
                            .setContext(this)
                            .setMessage("Carregando dados")
                            .setCancelable(false)
                            .build();
                    dialog.show();

                    final  StorageReference imageRef = storageReference
                            .child("imagens")
                            .child("usuarios")
                            .child(idUsuarioLogado + "jpeg");

                    UploadTask uploadTask = imageRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesUsuarioActivity.this,
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
                            Toast.makeText(ConfiguracoesUsuarioActivity.this,
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
        editUsuarioNome = findViewById(R.id.nomeNovoProduto);
        editUsuarioCidade = findViewById(R.id.editDescricaoNovoProduto);
        editUsuarioBairro = findViewById(R.id.editUsuarioBairro);
        editUsuarioRua = findViewById(R.id.editPrecoNovoProduto);
        editUsuarioNumeroCasa = findViewById(R.id.editUsuarioNumeroCasa);
        editUsuarioTelefone = findViewById(R.id.editUsuarioTelefone);
        imagePerfilUsuario = findViewById(R.id.imagePerfilUsuario);
    }

    private void abrirHome(){
        startActivity(new Intent(ConfiguracoesUsuarioActivity.this, HomeActivity.class));
    }

}