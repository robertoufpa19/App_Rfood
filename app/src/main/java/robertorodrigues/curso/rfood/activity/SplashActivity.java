package robertorodrigues.curso.rfood.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import robertorodrigues.curso.rfood.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
       // getSupportActionBar().hide(); // esconde toolbar

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
             abriAutenticacao();
            }
        }, 3000); // 3 segundos
    }

    private void abriAutenticacao(){
        Intent intent = new Intent(SplashActivity.this, AutenticacaoActivity.class);
         startActivity(intent);
         finish();
    }
}