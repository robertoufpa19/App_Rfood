package robertorodrigues.curso.rfood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import robertorodrigues.curso.rfood.R;
import robertorodrigues.curso.rfood.adapter.AdapterEmpresa;
import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.rfood.listener.RecyclerItemClickListener;
import robertorodrigues.curso.rfood.model.Empresa;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;
    private RecyclerView recyclerEmpresas;
    private List<Empresa> empresas = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AdapterEmpresa adapterEmpresa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // inicializar componentes
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        // configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Rfood");
        setSupportActionBar(toolbar);

        // configurar recyclerview
        recyclerEmpresas.setLayoutManager(new LinearLayoutManager(this));
        recyclerEmpresas.setHasFixedSize(true);
        adapterEmpresa = new AdapterEmpresa(empresas);
        recyclerEmpresas.setAdapter(adapterEmpresa);

        // recuperar  empresas do firebase
        recuperarEmpresas();

        // Configuracao do search view(busca de restaurantes)
         searchView.setHint("Buscar restaurante");
         searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
             @Override
             public boolean onQueryTextSubmit(String query) {
                 return false; //usuario tem que digitar o nome da empresa e da enter para fazer a busca
             }

             @Override
             public boolean onQueryTextChange(String newText) {
                 pesquisarEmpresas(newText);
                 return true; // conforme o usuario vai digitando aparece a empresa
             }
         });

         // Configurar evento de clique
          recyclerEmpresas.addOnItemTouchListener(
                  new RecyclerItemClickListener(
                          this,
                          recyclerEmpresas,
                          new RecyclerItemClickListener.OnItemClickListener() {
                              @Override
                              public void onItemClick(View view, int position) {

                                    Empresa empresaSelecionada = empresas.get(position);
                                    Intent i = new Intent(HomeActivity.this, CardapioActivity.class);
                                     i.putExtra("empresa",  empresaSelecionada);
                                     startActivity(i);
                              }

                              @Override
                              public void onLongItemClick(View view, int position) {

                              }

                              @Override
                              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                              }
                          }
                  )
          );



    }
    private void pesquisarEmpresas(String pesquisa){
        DatabaseReference empresasRef = firebaseRef.child("empresas");
        Query query = empresasRef.orderByChild("nome")
                .startAt(pesquisa)
                .endAt(pesquisa + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresas.clear();
                for(DataSnapshot ds: snapshot.getChildren() ){
                    empresas.add(ds.getValue(Empresa.class));
                }
                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperarEmpresas(){
        DatabaseReference empresaRef = firebaseRef.child("empresas");
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                empresas.clear();
                for(DataSnapshot ds: snapshot.getChildren() ){
                    empresas.add(ds.getValue(Empresa.class));
                }
                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario, menu);

        // configurar o botao de pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                deslogarUsuario();
                break;
            case R.id.menuConfiguracoes:
                abrirConfiguracoes();
                break;
            case R.id.menuConversasUsuario:
                abrirConversas();
                break;

            case R.id.menuMeusPedidos:
                abrirMeusPedidos();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
    private void inicializarComponentes(){
        searchView = findViewById(R.id.materialSearchView);
        recyclerEmpresas = findViewById(R.id.recyclerEmpresas);
    }

    private void deslogarUsuario(){
        try {
            //autenticacao.signOut();
           // finish();

            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("Sair");
            builder.setMessage("Tem certeza que deseja sair?");

            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        autenticacao.signOut();
                        finish();

                    }catch (Exception  e){
                        e.printStackTrace();
                    }

                    invalidateOptionsMenu(); // invalidar os menus de 3 pontinhos ao deslogar usuario
                    finish();
                    startActivity(new Intent(HomeActivity.this, AutenticacaoActivity.class));
                }
            });
            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }catch (Exception  e){
            e.printStackTrace();
        }
    }

    private void abrirConversas(){
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
    }
    private void abrirMeusPedidos(){
        startActivity(new Intent(HomeActivity.this, MeusPedidosActivity.class));
    }

    private void abrirConfiguracoes(){
        startActivity(new Intent(HomeActivity.this, ConfiguracoesUsuarioActivity.class));
    }

    @Override
    public void finish() {
        finishAffinity();
        super.finish();
    }
}