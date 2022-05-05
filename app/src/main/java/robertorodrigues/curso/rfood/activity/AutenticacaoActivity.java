package robertorodrigues.curso.rfood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import robertorodrigues.curso.rfood.R;
import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.rfood.helper.UsuarioFirebase;


public class AutenticacaoActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Switch  tipoAcesso, tipoUsuario;
    private Button botaoAcessar;
    private FirebaseAuth autenticacao;
    private LinearLayout linearTipoUsuario;

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



    }

    private void abrirTelaPrincipal(String tipoUsuario){
        if(tipoUsuario.equals("E")){ // empresa
            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
        }else{ // usuario("U")
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

    }



}