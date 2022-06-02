package robertorodrigues.curso.rfood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import robertorodrigues.curso.rfood.R;
import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.rfood.helper.UsuarioFirebase;


public class AutenticacaoActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Switch  tipoAcesso, tipoUsuario;
    private Button botaoAcessar;
    private FirebaseAuth autenticacao;
    private LinearLayout linearTipoUsuario;

    /// autenticacao do dispositivo com a conta do google
    private LinearLayout buttonAcessoGoogle;
    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient; // Cliente de login do Google
    private static final String TAG = "GoogleActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);
       // getSupportActionBar().hide(); // esconde toolbar

        inicializarComponentes();

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
         verificarUsuarioLogado();

         tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if(isChecked){ // switch selecionado = empresa
                     linearTipoUsuario.setVisibility(View.VISIBLE);
                 }else{ // switch nao selecionado = usuario(consumidor)
                     linearTipoUsuario.setVisibility(View.GONE);
                 }
             }
         });



  botaoAcessar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          String email = campoEmail.getText().toString();
          String senha= campoSenha.getText().toString();

          if(!email.isEmpty()){
              if(!senha.isEmpty()){
                  // verificar se o Switch esta ligado ou desligado
                  if(tipoAcesso.isChecked()){ // se ligado faz o cadastro

                      autenticacao.createUserWithEmailAndPassword(
                              email, senha
                      ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                          @Override
                          public void onComplete(@NonNull Task<AuthResult> task) {
                              if(task.isSuccessful()){

                                  Toast.makeText(AutenticacaoActivity.this,
                                          "Cadastro realizado com Sucesso!",
                                          Toast.LENGTH_SHORT).show();
                                  String tipoUsuario = getTipoUsuario();
                                  UsuarioFirebase.atualizarTipoUsuario(tipoUsuario);
                                  abrirTelaPrincipal(tipoUsuario);

                              }else{
                                  String excecao = "";

                                  try{
                                      throw task.getException();
                                  }catch (FirebaseAuthWeakPasswordException e){
                                      excecao = "Digite uma senha mais forte!";
                                  }catch (FirebaseAuthInvalidCredentialsException e){
                                      excecao = "Digite uma email valido!";
                                  }catch (FirebaseAuthUserCollisionException e){
                                      excecao = "Esta conta ja foi cadastrada!";
                                  }catch (Exception e){
                                      excecao = "Erro ao cadastrar o usuario!"+ e.getMessage();
                                      e.printStackTrace();
                                  }

                                  Toast.makeText(AutenticacaoActivity.this,
                                          excecao,
                                          Toast.LENGTH_SHORT).show();



                              }
                          }
                      });

                  }else{ // senao ele faz login

                      autenticacao.signInWithEmailAndPassword(
                              email, senha
                      ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                          @Override
                          public void onComplete(@NonNull Task<AuthResult> task) {
                              if(task.isSuccessful()){
                                  Toast.makeText(AutenticacaoActivity.this,
                                          "Logado com Sucesso!",
                                          Toast.LENGTH_SHORT).show();
                                  String tipoUsuario = task.getResult().getUser().getDisplayName();
                                  abrirTelaPrincipal(tipoUsuario);

                              }else{
                                  Toast.makeText(AutenticacaoActivity.this,
                                          "Erro ao fazer Login!",
                                          Toast.LENGTH_SHORT).show();
                              }
                          }
                      });
                  }
              }else{
                  Toast.makeText(AutenticacaoActivity.this,
                          "Preencha a Senha",
                          Toast.LENGTH_SHORT).show();
              }

          }else{
              Toast.makeText(AutenticacaoActivity.this,
                      "Preencha o email",
                      Toast.LENGTH_SHORT).show();

          }
      }
  });

        buttonAcessoGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dadosUsuario();
            }
        });

    }

    private void verificarUsuarioLogado(){
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if(usuarioAtual != null){
            String tipoUsuario = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(tipoUsuario);
        }
    }

    private String getTipoUsuario(){
        // se o switch tipo usuario estiver selecionado sera cadasdastrado "E"(empresa)
        // senao sera cadasdastrado "U"(usuario-consumidor)
        return  tipoUsuario.isChecked() ? "E" : "U";
    }

    private  void inicializarComponentes(){
        campoEmail = findViewById(R.id.editEmailAutenticacao);
        campoSenha = findViewById(R.id.editSenhaAutenticacao);
        tipoAcesso = findViewById(R.id.switchAcesso);
        botaoAcessar= findViewById(R.id.buttonAcesso);
        tipoUsuario = findViewById(R.id.switchTipoUsuario);
        linearTipoUsuario = findViewById(R.id.linearTipoUsuario);

        buttonAcessoGoogle = findViewById(R.id.buttonAcessoGoogle);

    }

    private void abrirTelaPrincipal(String tipoUsuario){
        if(tipoUsuario.equals("E")){ // empresa
            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
        }else{ // usuario("U")
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

    }




    /// metodos de login com a conta do google inicio

    public void dadosUsuario(){

        // Configurar o Login do Google para autenticar dispositivo

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id2))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        entrar();  // abrir layouts de contas disponiveis no dispositivo


    }


    private void entrar() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // verifica se o usuário fez login (não nulo) e atualiza a interface de acordo.
        FirebaseUser currentUser = autenticacao.getCurrentUser();
        updateUI(currentUser);

        if(currentUser != null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

    }

    private void updateUI(FirebaseUser user) {

    }


    // [ inicio no resultado da atividade]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Resultado retornado ao iniciar o Intent de GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // O login do Google foi bem-sucedido, autentique-se com o Firebase
                GoogleSignInAccount contaLoginGoogle = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + contaLoginGoogle.getId());
                firebaseAuthWithGoogle(contaLoginGoogle.getIdToken());



            } catch (ApiException e) {

                // Falha no login do Google, atualize a interface do usuário adequadamente
                Log.w(TAG, "Falha no login do Google", e);

                exibirMensagem("Erro ao fazer Login");
            }
        }


    }
    // [fim no resultado da atividade]


    // autenticação do firebase com o Google
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        autenticacao.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login bem-sucedido, atualiza a interface do usuário com as informações do usuário conectado
                            Log.d(TAG, "entrar com credencial: sucesso");
                            FirebaseUser user = autenticacao.getCurrentUser();
                            updateUI(user);
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));


                           /*
                            // dados do usuario
                            String idUsuario = task.getResult().getUser().getUid();
                            String emailUsuario = user.getEmail();
                            String nomeUsuario = user.getDisplayName();
                            String fotoUsuario = String.valueOf(user.getPhotoUrl());

                            usuario = new Usuario();
                            usuario.setIdUsuario(idUsuario);
                            usuario.setEmail(emailUsuario);
                            usuario.setNome(nomeUsuario);
                            usuario.setUrlImagem(fotoUsuario);
                            usuario.setTipo("cliente");



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
                                            usuario.salvarUsuario();

                                            if(usuario != null){

                                                exibirMensagem("Sucesso ao fazer Login");
                                                startActivity(new Intent(getApplicationContext(), AnunciosActivity.class));
                                                // startActivity(new Intent(this, HomeActivity.class));
                                                finish();
                                            }


                                        }
                                    });    // fim cadastro do token  */


                        } else {
                            // Se o login falhar, exibe uma mensagem para o usuário.
                            Log.w(TAG, "entrar com credencial: falha", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    // [TERMINAR autenticação com o google]


    /// metodos de login com a conta do google fim
    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }


}