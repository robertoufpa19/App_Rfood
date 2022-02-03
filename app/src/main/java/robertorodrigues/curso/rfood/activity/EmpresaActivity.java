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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import robertorodrigues.curso.rfood.R;
import robertorodrigues.curso.rfood.adapter.AdapterProduto;
import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.rfood.helper.UsuarioFirebase;
import robertorodrigues.curso.rfood.listener.RecyclerItemClickListener;
import robertorodrigues.curso.rfood.model.Empresa;
import robertorodrigues.curso.rfood.model.Produto;

public class EmpresaActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerProdutos;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private Empresa empresa;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        // inicializar componentes
        inicializarComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();


        // configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Rfood - Empresa");
                setSupportActionBar(toolbar);

                // configurar recyclerview
        recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutos.setAdapter(adapterProduto);

        // recuperar produtos da empresa do firebase
           recuperarProdutos();
           recuperarDadosEmpresa();





           // adicionar evento de clique no recyclerView
          recyclerProdutos.addOnItemTouchListener(new RecyclerItemClickListener(
                  this,
                  recyclerProdutos,
                  new RecyclerItemClickListener.OnItemClickListener() {
                      @Override
                      public void onItemClick(View view, int position) {

                      }

                      @Override
                      public void onLongItemClick(View view, int position) {

                          AlertDialog.Builder builder = new AlertDialog.Builder(EmpresaActivity.this);
                          builder.setTitle("Excluir");
                          builder.setMessage("Excluir esse produto?");

                          builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {

                                  Produto produtoSelecionado = produtos.get(position);
                                  produtoSelecionado.remover();

                                  Toast.makeText(EmpresaActivity.this,
                                          "Produto excluido com sucesso!",
                                          Toast.LENGTH_SHORT).show();


                              }
                          });
                          builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {


                              }
                          });

                          AlertDialog dialog = builder.create();
                          dialog.show();


                      }

                      @Override
                      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                      }
                  }
          ));

    }

    private  void recuperarProdutos(){
      DatabaseReference produtosRef = firebaseRef.
              child("produtos").
              child(idUsuarioLogado);
      produtosRef.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              produtos.clear();
              for(DataSnapshot ds: snapshot.getChildren() ){
                    produtos.add(ds.getValue(Produto.class));
              }
              adapterProduto.notifyDataSetChanged();
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });
    }

    private  void recuperarDadosEmpresa(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference empresaRef = firebaseRef
                .child("empresas")
                .child(idUsuarioLogado);
        empresaRef.addValueEventListener(new ValueEventListener() {
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

    private void  inicializarComponentes(){
        recyclerProdutos = findViewById(R.id.recyclerProdutos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
                     inflater.inflate(R.menu.menu_empresa, menu);

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
              case R.id.menuNovoProduto:
                  if(empresa != null){
                      abrirNovoProduto();
                  }else{
                      exibirMensagem("Antes de adicionar produtos,configure o perfil da empresa!");
                      abrirConfiguracoes();
                  }

                  break;
              case R.id.menuPedidos:
                  abrirPedidos();
                  break;

             /* case R.id.menuConversasEmpresa:
                  abrirConversas();
                  break; */
          }

        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
            finish();

        }catch (Exception  e){
            e.printStackTrace();
        }
    }

    private void abrirConfiguracoes(){
        startActivity(new Intent(EmpresaActivity.this, ConfiguracoesEmpresaActivity.class));
    }

    private void abrirConversas(){
        startActivity(new Intent(EmpresaActivity.this, MainActivity.class));
    }

    private void abrirPedidos(){
        startActivity(new Intent(EmpresaActivity.this, PedidosActivity.class));
    }


    private void abrirNovoProduto(){
        startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
    }
    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
}