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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import robertorodrigues.curso.rfood.R;
import robertorodrigues.curso.rfood.adapter.AdapterProduto;
import robertorodrigues.curso.rfood.api.NotificacaoService;
import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.rfood.helper.UsuarioFirebase;
import robertorodrigues.curso.rfood.listener.RecyclerItemClickListener;
import robertorodrigues.curso.rfood.model.Empresa;
import robertorodrigues.curso.rfood.model.ItemPedido;
import robertorodrigues.curso.rfood.model.Notificacao;
import robertorodrigues.curso.rfood.model.NotificacaoDados;
import robertorodrigues.curso.rfood.model.Pedido;
import robertorodrigues.curso.rfood.model.Produto;
import robertorodrigues.curso.rfood.model.Usuario;

public class CardapioActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutosCardapio;
    private ImageView imageEmpresaCradapio;
    private TextView textNomeEmpresaCardapio;
    private Empresa empresaSelecionada;
    private String idEmpresa;
    private String idUsuarioLogado;
    private Usuario usuario;

    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private DatabaseReference usuarioRef;
    // notificação
    private String token;
    private Retrofit retrofit;
    private String baseUrl;

    private AlertDialog dialog;
    private Pedido pedidoRecuperado;

    private int qtdItensCarrinho;
    private Double totalCarrinho;

    private  TextView textCarrinhoQtd, textCarrinhoTotal;
    private int metodoPagamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);

        // configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cardapio");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // configuracoes iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        // recuperar empresa selecionada
          Bundle bundle = getIntent().getExtras();
          if(bundle != null){
              empresaSelecionada = (Empresa) bundle.getSerializable("empresa");

              textNomeEmpresaCardapio.setText(empresaSelecionada.getNome());
              idEmpresa = empresaSelecionada.getIdUsuario(); // id do Usuario empresa
              //recuperar foto
              String url = empresaSelecionada.getUrlImagem();
              Picasso.get().load(url).into(imageEmpresaCradapio);

              recuperarTokenDestinatarioEmpresa();
          }

        // configurar recyclerview
        recyclerProdutosCardapio.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutosCardapio.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutosCardapio.setAdapter(adapterProduto);

        // configurar evento de clique
        recyclerProdutosCardapio.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerProdutosCardapio,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                               //condicao para verificar se o usuario configurou seus dados(perfil)
                                if(usuario != null){ //teste
                                    confirmarQuantidade(position);
                                }else{
                                    exibirMensagem("Configure seu Perfil, antes de fazer seu pedido!");
                                    abrirConfiguracoes();
                                }


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

        // recuperar produtos da empresa do firebase e dados do usuario
        recuperarProdutos();
        recuperarDadosUsuario();



        //Configuração da retrofit para enviar requisição ao firebase e então para ele enviar a notificação
        baseUrl = "https://fcm.googleapis.com/fcm/";
        retrofit = new Retrofit.Builder()
                .baseUrl( baseUrl )
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    private void confirmarQuantidade(int posicao){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade");
        //configurar campo de texto no alert
        EditText editQuantidade = new EditText(this);
        editQuantidade.setText("1");
        builder.setView(editQuantidade);



        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                String quantidade = editQuantidade.getText().toString();
                Produto produtoSelecionado = produtos.get(posicao);
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setIdProduto(produtoSelecionado.getIdProduto());
                itemPedido.setNomeProduto(produtoSelecionado.getNome());
                itemPedido.setPreco(produtoSelecionado.getPreco());
                itemPedido.setQuantidade(Integer.parseInt(quantidade));
                itensCarrinho.add(itemPedido);

                if(pedidoRecuperado == null){
                    pedidoRecuperado = new Pedido(idUsuarioLogado, idEmpresa);
                }

                pedidoRecuperado.setNome(usuario.getNome());
                pedidoRecuperado.setCidade(usuario.getCidade());
                pedidoRecuperado.setBairro(usuario.getBairro());
                pedidoRecuperado.setRua(usuario.getRua());
                pedidoRecuperado.setNumero(usuario.getNumero());
                pedidoRecuperado.setTelefone(usuario.getTelefone());
                pedidoRecuperado.setItens(itensCarrinho);
                pedidoRecuperado.setUrlImagem(usuario.getUrlImagem());
                pedidoRecuperado.setTokenUsuario(usuario.getTokenUsuario());

                pedidoRecuperado.setNomeEmpresa(empresaSelecionada.getNome());
                pedidoRecuperado.setUrlImagemEmpresa(empresaSelecionada.getUrlImagem());
                pedidoRecuperado.setTokenEmpresa(empresaSelecionada.getTokenEmpresa());


                pedidoRecuperado.salvar();



            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void recuperarDadosUsuario(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuarioLogado);
        // recupera dados uma unica vez
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                  if(snapshot.getValue() != null){
                      usuario = snapshot.getValue(Usuario.class);
                  }
                  recuperarPedido();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void recuperarPedido(){
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuarios")
                .child(idEmpresa)
                .child(idUsuarioLogado);
        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                qtdItensCarrinho =0;
                totalCarrinho =0.0;
                itensCarrinho = new ArrayList<>(); // zerando a lista caso nao tenha pedidos

                if(snapshot.getValue() != null){
                    pedidoRecuperado = snapshot.getValue(Pedido.class);
                    itensCarrinho = pedidoRecuperado.getItens();

                     for (ItemPedido itemPedido : itensCarrinho) {
                          int qtde = itemPedido.getQuantidade();
                          String  preco = itemPedido.getPreco();
                         // calculo da compra do usuario
                           //totalCarrinho += (qtde * preco);  //erro corrigir
                           totalCarrinho += (qtde);
                           qtdItensCarrinho += qtde;

                     }

                }

                DecimalFormat df = new DecimalFormat("0.00");

                textCarrinhoQtd.setText("qtd: " + String.valueOf(qtdItensCarrinho));
                textCarrinhoTotal.setText("R$"+ df.format(totalCarrinho));

                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void recuperarProdutos(){
        DatabaseReference produtosRef = firebaseRef.
                child("produtos").
                child(idEmpresa);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cardapio, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuPedido:
                if(pedidoRecuperado != null){
                    confirmarPedido();
                }else{
                    exibirMensagem("Selecione um produto!");
                }

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private  void confirmarPedido(){

         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle("Selecione uma forma de pagamento: ");

         CharSequence[] itens = new CharSequence[]{
            "Dinheiro", "Cartão"
         };

         builder.setSingleChoiceItems(itens, 0, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                       metodoPagamento = which; // 0 = dinheiro e 1 = Cartao
             }
         });

         EditText editObservacao = new EditText(this);
         editObservacao.setHint("Digite uma observação ou seu troco");
         builder.setView(editObservacao);

         builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                      String observacao = editObservacao.getText().toString();
                      pedidoRecuperado.setMetodoPagamento(metodoPagamento);
                      pedidoRecuperado.setObservacao(observacao);
                      pedidoRecuperado.setStatus("confirmado");


                      pedidoRecuperado.confirmar();
                      pedidoRecuperado.removerPedido(); // remove o "NO"(pedidos_usuarios)
                      pedidoRecuperado = null; // zera o pedido recuperado
                      exibirMensagem("Pedido realizado com sucesso!");
                      finish();
                      abrirMeusPedidos();

                      // enviar notificação para a loja
                      enviarNotificacao();

             }
         });
         builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {

             }
         });

         AlertDialog dialog = builder.create();
         dialog.show();
    }


    public  void  recuperarTokenDestinatarioEmpresa(){

        Bundle bundleToken = getIntent().getExtras();
        if(bundleToken  != null){

       if(bundleToken.containsKey("empresa")){

                empresaSelecionada = (Empresa) bundleToken.getSerializable("empresa");
                // token = usuarioDestinatario.getTokenUsuario();
                // recuperar token do NO usuarios
                usuarioRef =  ConfiguracaoFirebase.getFirebaseDatabase()
                        .child("empresas")
                        .child(empresaSelecionada.getIdUsuario())
                        .child("tokenEmpresa");
                usuarioRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String tokenEmpresa =  snapshot.getValue().toString();
                        token = tokenEmpresa;

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        }
    }


    public void enviarNotificacao(){

        Bundle bundleNotificacao = getIntent().getExtras();
         if(bundleNotificacao.containsKey("empresa")){

            empresaSelecionada  = (Empresa) bundleNotificacao.getSerializable("empresa");
            token = empresaSelecionada.getTokenEmpresa();
            // token = usuarioDestinatario.getTokenUsuario();
            // recuperar token do NO usuarios
          /*  usuarioRef =  ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("empresas")
                    .child(empresaSelecionada.getIdUsuario())
                    .child("tokenEmpresa");
            usuarioRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String tokenUsuario =  snapshot.getValue().toString();
                    token = tokenUsuario;

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }); */

            String tokenDestinatario = token;
            String to = "";// para quem vou enviar a menssagem
            to = tokenDestinatario ;

            //Monta objeto notificação
            Notificacao notificacao = new Notificacao("CeV", "Novo Pedido " + usuario.getNome());
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


    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void abrirConfiguracoes(){
        startActivity(new Intent(CardapioActivity.this, ConfiguracoesUsuarioActivity.class));
    }
    private void abrirMeusPedidos(){
        startActivity(new Intent(CardapioActivity.this, MeusPedidosActivity.class));
    }

   private void  inicializarComponentes(){
         recyclerProdutosCardapio = findViewById(R.id.recyclerProdutosCardapio);
         imageEmpresaCradapio = findViewById(R.id.imageEmpresaCardapio);
         textNomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);
         textCarrinhoQtd = findViewById(R.id.textCarrinhoQtd);
         textCarrinhoTotal = findViewById(R.id.textCarrinhoTotal);

    }


}