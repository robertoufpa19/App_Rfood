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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import robertorodrigues.curso.rfood.R;
import robertorodrigues.curso.rfood.adapter.AdapterPedido;

import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.rfood.helper.UsuarioFirebase;
import robertorodrigues.curso.rfood.listener.RecyclerItemClickListener;
import robertorodrigues.curso.rfood.model.Pedido;

public class MeusPedidosActivity extends AppCompatActivity {


    private RecyclerView recyclerPedidos;
    private AdapterPedido adapterPedido;
    private List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_pedidos);

        // configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Meus Pedidos");
        setSupportActionBar(toolbar);

        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        idUsuario = UsuarioFirebase.getIdUsuario();

        //falta recuperar os pedidos do usuario

        // configurar recyclerview
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.setHasFixedSize(true);
        adapterPedido = new AdapterPedido(pedidos, this);
        recyclerPedidos.setAdapter(adapterPedido);

        recuperarPedidos();


        // adicionar evento de clique no recyclerview
        recyclerPedidos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerPedidos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //configurar clique para abri um chat(iniciar conversas)



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


    private void recuperarPedidos(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference pedidoRef = firebaseRef
                .child("meus_pedidos")
                .child(idUsuario);

        Query pedidoPesquisa = pedidoRef.orderByChild("status")
                .equalTo("confirmado");

        pedidoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pedidos.clear();
                if(snapshot.getValue() != null){
                    for (DataSnapshot ds: snapshot.getChildren()){
                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidos.add(pedido);
                    }
                    adapterPedido.notifyDataSetChanged();
                    dialog.dismiss();
                }else if(snapshot.getValue() == null){ // senao tiver pedidos
                    exibirMensagem("Você não tem pedidos!");

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }


    private void  inicializarComponentes(){
        recyclerPedidos = findViewById(R.id.recyclerMeusPedidosFragment);

    }


}