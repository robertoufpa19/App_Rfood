package robertorodrigues.curso.rfood.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import robertorodrigues.curso.rfood.R;
import robertorodrigues.curso.rfood.adapter.MensagensAdapter;

import robertorodrigues.curso.rfood.api.NotificacaoService;
import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.rfood.helper.UsuarioFirebase;
import robertorodrigues.curso.rfood.model.Conversa;

import robertorodrigues.curso.rfood.model.Mensagem;
import robertorodrigues.curso.rfood.model.Notificacao;
import robertorodrigues.curso.rfood.model.NotificacaoDados;
import robertorodrigues.curso.rfood.model.Pedido;

public class ChatActivity extends AppCompatActivity {

   private TextView textViewNome;
   private ImageView circleImageViewFoto;

   private Pedido pedidoUsuarioDestinatario;
    private String token;
    private DatabaseReference usuarioRef;

    private EditText editMensagem;

    //identificador usuarios remetente e destinatario
    private String idUsuarioRemetente;
    private String  idUsuarioDestinatario;
    private DatabaseReference database;
    private StorageReference storage;
    private DatabaseReference mensagensRef;
    private ChildEventListener childEventListenerMensagens;

    private List<Mensagem> mensagens = new ArrayList<>();
    private MensagensAdapter adapter;

    private RecyclerView recyclerMensagens;

    // variares de clique
    private ImageView imageCamera;
    private static final int SELECAO_CAMERA = 100;
    private ImageView imageGaleria;
    private static final int SELECAO_GALERIA = 200;

    private String baseUrl;
    private Retrofit retrofit;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

         // configuracoes iniciais
        textViewNome = findViewById(R.id.textViewNomeChat);
        circleImageViewFoto = findViewById(R.id.circleImageFotoChat);
        editMensagem = findViewById(R.id.editMensagem);
        recyclerMensagens = findViewById(R.id.recyclerMensagens);
        imageCamera = findViewById(R.id.imageCamera); // recupera foto tirada na hora para enviar no chat
        imageGaleria      = findViewById(R.id.imageGaleria);// recupera foto da galeria para enviar no chat

        //recupera dados do usuario remetente id
        idUsuarioRemetente = UsuarioFirebase.getIdUsuario();
        //falta recuperar dados do remetente
      //  pedidoUsuarioRemetente = UsuarioFirebase.getDadosUsuarioLogado();



        // recuperar dados usuario destinatario  ( falta criar a codicao para ver se e usuario ou empresa)
        Bundle bundle = getIntent().getExtras();
          if(bundle != null){

              if(bundle.containsKey("chat")){
                  pedidoUsuarioDestinatario  = (Pedido) bundle.getSerializable("chat");
                  textViewNome.setText(pedidoUsuarioDestinatario.getNomeEmpresa());
                  //recuperar foto
                  String url = pedidoUsuarioDestinatario.getUrlImagemEmpresa();
                  Picasso.get().load(url).into(circleImageViewFoto);

                  idUsuarioDestinatario = pedidoUsuarioDestinatario.getIdEmpresa(); // id empresa
                  recuperarTokenDestinatario();


              }else  if(bundle.containsKey("pedidos")){

                  pedidoUsuarioDestinatario = (Pedido) bundle.getSerializable("pedidos");
                  textViewNome.setText(pedidoUsuarioDestinatario.getNome());

                  //  idUsuario = pedidoUsuarioDestinatario.getIdUsuario();// id do Usuario empresa
                  //recuperar foto
                  String url = pedidoUsuarioDestinatario.getUrlImagem();
                  Picasso.get().load(url).into(circleImageViewFoto);

                  //recuperar dados usuario destinatario
                  idUsuarioDestinatario = pedidoUsuarioDestinatario.getIdUsuario();// id do Usuario
                  recuperarTokenDestinatario();

              }


          }


        //Configuração adapter
        adapter = new MensagensAdapter(mensagens, getApplicationContext() );

        //Configuração recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMensagens.setLayoutManager( layoutManager );
        recyclerMensagens.setHasFixedSize( true );
        recyclerMensagens.setAdapter( adapter );

        database = ConfiguracaoFirebase.getFirebaseDatabase();
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        mensagensRef = database.child("mensagens")
                .child( idUsuarioRemetente )
                .child( idUsuarioDestinatario );

        // evento de click na camera do chat
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(intent.resolveActivity(getPackageManager())!= null){
                    startActivityForResult(intent, SELECAO_CAMERA);
                }
            }
        });
        // evento de click na galeria do chat

        imageGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager())!= null){
                    startActivityForResult(intent, SELECAO_GALERIA);
                }
            }
        });



        //Configuração da retrofit para enviar requisição ao firebase e então para ele enviar a notificação
        baseUrl = "https://fcm.googleapis.com/fcm/";
        retrofit = new Retrofit.Builder()
                .baseUrl( baseUrl )
                .addConverterFactory(GsonConverterFactory.create())
                .build();



    }
    public void enviarMensagem(View view) {


        Bundle bundleEnviarMensagem = getIntent().getExtras();

        if(bundleEnviarMensagem != null){
            String textoMensagem = editMensagem.getText().toString();
            if ( !textoMensagem.isEmpty() ){

                if(bundleEnviarMensagem.containsKey("pedidos")){
                    pedidoUsuarioDestinatario = (Pedido) bundleEnviarMensagem.getSerializable("pedidos");

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario( idUsuarioRemetente );
                    mensagem.setMensagem( textoMensagem );

                    //Salvar mensagem para o remetente
                    salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                    //Salvar mensagem para o destinatario(falta salvar pro destinatario)
                    salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

                    //Salvar conversa para o remetente
                    salvarConversa( idUsuarioRemetente, idUsuarioDestinatario, true, mensagem);
                    //Salvar conversa para o destinatario
                    salvarConversa(  idUsuarioDestinatario, idUsuarioRemetente, true, mensagem);

                    //Salvar conversa para o destinatario // falta salvar no de conversa no firebase
                    //  salvarConversa(mensagem, pedidoUsuarioRemetente);


                } else if(bundleEnviarMensagem.containsKey("chat")){
                    pedidoUsuarioDestinatario = (Pedido) bundleEnviarMensagem.getSerializable("chat");


                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario( idUsuarioRemetente );
                    mensagem.setMensagem( textoMensagem );

                    //Salvar mensagem para o remetente
                    salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                    //Salvar mensagem para o destinatario(falta salvar pro destinatario)
                    salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

                    //Salvar conversa para o remetente
                    salvarConversa( idUsuarioRemetente, idUsuarioDestinatario, true, mensagem);
                    //Salvar conversa para o destinatario
                    salvarConversa(  idUsuarioDestinatario, idUsuarioRemetente, true, mensagem);


                }




            }

        }

          enviarNotificacao();


    }

          // salva conversa para a empresa
    private void salvarConversa(String idRemetente, String idDestinatario, boolean isEmpresa, Mensagem msg){


        //Salvar conversa remetente
        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdRemetente( idRemetente);
        conversaRemetente.setIdDestinatario( idDestinatario );
        conversaRemetente.setUltimaMensagem(msg.getMensagem());
        conversaRemetente.setUsuarioExibicaoPedido(pedidoUsuarioDestinatario);


        if(isEmpresa){
            conversaRemetente.setIsEmpresa("true");
          //  conversaRemetente.setUsuarioExibicaoPedido(usuarioExibicao);
        }else{
            conversaRemetente.setIsEmpresa("false");
           // conversaRemetente.setUsuarioExibicaoPedido(usuarioExibicao);
        }


        conversaRemetente.salvar();

    }







    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem msg){

        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference mensagemRef = database.child("mensagens");

        mensagemRef.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(msg);

        //Limpar texto
        editMensagem.setText("");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recuperarTokenDestinatario();

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try {
                switch (requestCode){
                    case SELECAO_CAMERA:
                        imagem =(Bitmap) data.getExtras().get("data");
                        break;

                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);

                        break;
                }

                if(imagem != null){

                    //  imageGaleria.setImageBitmap(imagem);


                    // recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // cria nome que não se repete
                    String nomeImagem = UUID.randomUUID().toString();

                    // configurar referencia do firebase
                    final StorageReference imageRef = storage.child("imagens")
                            .child("fotos")
                            .child(idUsuarioRemetente)
                            .child(nomeImagem);

                    UploadTask uploadTask = imageRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Erro", "Erro ao fazer upload");

                            Toast.makeText(ChatActivity.this,
                                    "Erro ao fazer upload da imagem!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // String dowloadUrl = taskSnapshot.getDownloadUrl().toString();

                            //teste para nova versão
                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String dowloadUrl =  task.getResult().toString();

                                    Bundle bundleEnviarMensagem = getIntent().getExtras();


                                    if(bundleEnviarMensagem != null){

                                            if(bundleEnviarMensagem.containsKey("pedidos")){
                                                pedidoUsuarioDestinatario = (Pedido) bundleEnviarMensagem.getSerializable("pedidos");

                                                Mensagem mensagem = new Mensagem();
                                                mensagem.setIdUsuario( idUsuarioRemetente );
                                                mensagem.setMensagem("imagem.jpeg");
                                                mensagem.setImagem(dowloadUrl);

                                                //Salvar mensagem para o remetente
                                                salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                                                //Salvar mensagem para o destinatario(falta salvar pro destinatario)
                                                salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

                                                //Salvar conversa para o remetente
                                                salvarConversa( idUsuarioRemetente, idUsuarioDestinatario, true, mensagem);
                                                //Salvar conversa para o destinatario
                                                salvarConversa(  idUsuarioDestinatario, idUsuarioRemetente, true, mensagem);

                                                //Salvar conversa para o destinatario // falta salvar no de conversa no firebase
                                                //  salvarConversa(mensagem, pedidoUsuarioRemetente);

                                                enviarNotificacao();


                                            } else if(bundleEnviarMensagem.containsKey("chat")){
                                                pedidoUsuarioDestinatario = (Pedido) bundleEnviarMensagem.getSerializable("chat");


                                                Mensagem mensagem = new Mensagem();
                                                mensagem.setIdUsuario( idUsuarioRemetente );
                                                mensagem.setMensagem("imagem.jpeg");
                                                mensagem.setImagem(dowloadUrl);

                                                //Salvar mensagem para o remetente
                                                salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                                                //Salvar mensagem para o destinatario(falta salvar pro destinatario)
                                                salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

                                                //Salvar conversa para o remetente
                                                salvarConversa( idUsuarioRemetente, idUsuarioDestinatario, true, mensagem);
                                                //Salvar conversa para o destinatario
                                                salvarConversa(  idUsuarioDestinatario, idUsuarioRemetente, true, mensagem);

                                                    enviarNotificacao();

                                            }





                                    }


                                }
                            });



                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }


        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();
        recuperarTokenDestinatario();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagensRef.removeEventListener( childEventListenerMensagens );
    }

    private void recuperarMensagens(){

        mensagens.clear(); //teste
        recuperarTokenDestinatario();

        childEventListenerMensagens = mensagensRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Mensagem mensagem = dataSnapshot.getValue( Mensagem.class );
                mensagens.add( mensagem );
                adapter.notifyDataSetChanged();
                // da o foco na ultima mensagem enviada
                recyclerMensagens.scrollToPosition(mensagens.size() -1 );
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    public  void  recuperarTokenDestinatario(){

        Bundle bundleToken = getIntent().getExtras();
        if(bundleToken  != null){
            if(bundleToken.containsKey("pedidos")){

                pedidoUsuarioDestinatario  = (Pedido) bundleToken.getSerializable("pedidos");
                token = pedidoUsuarioDestinatario.getTokenUsuario();


            }else if(bundleToken.containsKey("chat")){

                pedidoUsuarioDestinatario  = (Pedido) bundleToken.getSerializable("chat");
                token = pedidoUsuarioDestinatario.getTokenEmpresa();
            }
        }
    }





    public void enviarNotificacao(){

        Bundle bundleNotificacao = getIntent().getExtras();
        if(bundleNotificacao.containsKey("pedidos")){
            pedidoUsuarioDestinatario  = (Pedido) bundleNotificacao.getSerializable("pedidos");
            token = pedidoUsuarioDestinatario.getTokenUsuario();

            String tokenDestinatario = token;
            String to = "";// para quem vou enviar a menssagem
            to = tokenDestinatario ;

            //Monta objeto notificação
            Notificacao notificacao = new Notificacao("Rfood", "Nova Mensagem " );
            NotificacaoDados notificacaoDados = new NotificacaoDados(to, notificacao );

            NotificacaoService service = retrofit.create(NotificacaoService.class);
            Call<NotificacaoDados> call = service.salvarNotificacao( notificacaoDados );

                call.enqueue(new Callback<NotificacaoDados>() {
                    @Override
                    public void onResponse(Call<NotificacaoDados> call, Response<NotificacaoDados> response) {
                        if( response.isSuccessful() ){

                            //teste para verificar se enviou a notificação
                           /*  Toast.makeText(getApplicationContext(),
                                     "codigo: " + response.code(),
                                     Toast.LENGTH_LONG ).show();

                            */

                        }
                    }

                    @Override
                    public void onFailure(Call<NotificacaoDados> call, Throwable t) {

                    }
                });

        }else if(bundleNotificacao.containsKey("chat")){

            pedidoUsuarioDestinatario  = (Pedido) bundleNotificacao.getSerializable("chat");
            token = pedidoUsuarioDestinatario.getTokenEmpresa();

            String tokenDestinatario = token;
            String to = "";// para quem vou enviar a menssagem
            to = tokenDestinatario ;

            //Monta objeto notificação
            Notificacao notificacao = new Notificacao("Rfood", "Nova Mensagem");
            NotificacaoDados notificacaoDados = new NotificacaoDados(to, notificacao );

            NotificacaoService service = retrofit.create(NotificacaoService.class);
            Call<NotificacaoDados> call = service.salvarNotificacao( notificacaoDados );

            call.enqueue(new Callback<NotificacaoDados>() {
                @Override
                public void onResponse(Call<NotificacaoDados> call, Response<NotificacaoDados> response) {
                    if( response.isSuccessful() ){

                        //teste para verificar se enviou a notificação
                           /*  Toast.makeText(getApplicationContext(),
                                     "codigo: " + response.code(),
                                     Toast.LENGTH_LONG ).show();

                            */

                    }
                }

                @Override
                public void onFailure(Call<NotificacaoDados> call, Throwable t) {

                }
            });
        }
    }
}