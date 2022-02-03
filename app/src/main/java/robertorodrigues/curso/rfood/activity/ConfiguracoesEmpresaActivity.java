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
import me.abhinay.input.CurrencyEditText;
import robertorodrigues.curso.rfood.R;
import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.rfood.helper.UsuarioFirebase;
import robertorodrigues.curso.rfood.model.Empresa;

public class ConfiguracoesEmpresaActivity extends AppCompatActivity {

    private EditText editEmpresaNome, editEmpresaCategoria, editEmpresaTempo;
    private ImageView imagePerfilEmpresa;
    private CurrencyEditText editEmpresaTaxa;

    private static  final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_empresa);

        //configuracoes iniciais
        inicializarComponentes();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        // configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configuracoes");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagePerfilEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                 if(i.resolveActivity(getPackageManager()) != null){
                      startActivityForResult(i, SELECAO_GALERIA);

                 }

            }
        });

        //recuperar dados da empresa
        recuperarDadosEmpresa();


    }

    private  void recuperarDadosEmpresa(){
        DatabaseReference empresaRef = firebaseRef
                .child("empresas")
                .child(idUsuarioLogado);
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue() != null){
                     Empresa empresa = snapshot.getValue(Empresa.class);
                     editEmpresaNome.setText(empresa.getNome());
                     editEmpresaCategoria.setText(empresa.getCategoria());
                     editEmpresaTaxa.setText(empresa.getPrecoEntrega());
                     editEmpresaTempo.setText(empresa.getTempo());
                     //recuperar imagem de perfil da empresa
                     urlImagemSelecionada = empresa.getUrlImagem();
                     if (urlImagemSelecionada != ""){
                         Picasso.get()
                                 .load(urlImagemSelecionada)
                                 .into(imagePerfilEmpresa);
                     }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

     public  void validarDadosEmpresa(View view){

          // validar os campos que foram preenchidos
         String nome = editEmpresaNome.getText().toString();
         String taxa = editEmpresaTaxa.getText().toString();
         String categoria = editEmpresaCategoria.getText().toString();
         String tempo = editEmpresaTempo.getText().toString();
         String foto = urlImagemSelecionada;

         if(foto != "") {
             if (!nome.isEmpty()) {
                 if (!taxa.isEmpty()) {
                     if (!categoria.isEmpty()) {
                         if (!tempo.isEmpty()) {

                             Empresa empresa = new Empresa();
                             empresa.setIdUsuario(idUsuarioLogado);
                             empresa.setNome(nome);
                             // empresa.setPrecoEntrega(Double.parseDouble(taxa));
                             empresa.setPrecoEntrega(taxa);
                             empresa.setCategoria(categoria);
                             empresa.setTempo(tempo);
                             empresa.setUrlImagem(urlImagemSelecionada);


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

                                             empresa.setTokenEmpresa(token);

                                             empresa.salvar();
                                             finish();
                                             abrirInicioEmpresa();
                                             exibirMensagem("Perfil atualizado!");


                                         }
                                     });    // fim cadastro do token


                         } else {
                             exibirMensagem("Digite um tempo de entrega!");
                         }

                     } else {
                         exibirMensagem("Digite uma categoria!");
                     }

                 } else {
                     exibirMensagem("Digite uma taxa de entrega!");
                 }

             } else {
                 exibirMensagem("Digite um nome para a empresa!");
             }

         }else{
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
                    imagePerfilEmpresa.setImageBitmap(imagem);

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
                            .child("empresas")
                            .child(idUsuarioLogado + "jpeg");

                    UploadTask uploadTask = imageRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
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
                            Toast.makeText(ConfiguracoesEmpresaActivity.this,
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

    private void abrirInicioEmpresa(){
        startActivity(new Intent(ConfiguracoesEmpresaActivity.this, EmpresaActivity.class));
    }

    private void inicializarComponentes(){
        editEmpresaNome = findViewById(R.id.editEmpresaNome);
        editEmpresaCategoria = findViewById(R.id.editEmpresaCategoria);
        editEmpresaTaxa = findViewById(R.id.editTaxaEntrega);
        editEmpresaTempo = findViewById(R.id.editEmpresaTempoEntrega);
        imagePerfilEmpresa = findViewById(R.id.imagePerfilEmpresa);


    }


}