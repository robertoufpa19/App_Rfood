package robertorodrigues.curso.rfood.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import robertorodrigues.curso.rfood.R;
import robertorodrigues.curso.rfood.activity.ChatActivity;
import robertorodrigues.curso.rfood.adapter.ConversasAdapter;
import robertorodrigues.curso.rfood.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.rfood.helper.UsuarioFirebase;
import robertorodrigues.curso.rfood.listener.RecyclerItemClickListener;
import robertorodrigues.curso.rfood.model.Conversa;
import robertorodrigues.curso.rfood.model.Pedido;
import robertorodrigues.curso.rfood.model.Usuario;
import robertorodrigues.curso.rfood.util.DataAtual;
import robertorodrigues.curso.rfood.util.HoraAtual;

/**
 * A simple {@link Fragment} subclass.

 */
public class ConversasFragment extends Fragment {

    private RecyclerView recyclerViewConversas;
    private List<Conversa> listaConversas = new ArrayList<>();
    private ConversasAdapter adapter;
    private DatabaseReference database;
    private DatabaseReference conversasRef;
    private ChildEventListener childEventListenerConversas;


    public ConversasFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        recyclerViewConversas = view.findViewById(R.id.recyclerListaConversas); // lista de pedidos

        //Configurar adapter
        adapter = new ConversasAdapter(listaConversas, getActivity());

        //Configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewConversas.setLayoutManager( layoutManager );
        recyclerViewConversas.setHasFixedSize(true);
        recyclerViewConversas.setAdapter( adapter );

        //Configurar evento de clique nas conversas
        recyclerViewConversas.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewConversas,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                // criar uma codicao para verificar se e  conversaSelecionado.getEmpresaExibicao()
                                //ou getUsuarioExibicaoPedido()
                                List<Conversa> listaConversasAtualizada = adapter.getConversas();
                                Conversa conversaSelecionada = listaConversasAtualizada .get(position); // seleciona conversa que foi buscada de forma correta




                               if (conversaSelecionada.getIsEmpresa().equals("true")) {
                                   Intent intent = new Intent(getActivity(), ChatActivity.class);
                                   intent.putExtra("chat", (Parcelable) conversaSelecionada.getUsuarioExibicaoPedido());
                                   startActivity( intent );
                                   conversaSelecionada.setUltimaConversa("false");
                                   conversaSelecionada.salvar();


                                }else{

                                    Intent i = new Intent(getActivity(), ChatActivity.class);
                                    i.putExtra("pedidos", (Parcelable) conversaSelecionada.getUsuarioExibicaoPedido());
                                    startActivity( i );
                                    conversaSelecionada.setUltimaConversa("false");
                                    conversaSelecionada.salvar();
                                }







                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                // criar uma codicao para verificar se e  conversaSelecionado.getEmpresaExibicao()
                                //ou getUsuarioExibicaoPedido()
                                List<Conversa> listaConversasAtualizada = adapter.getConversas();
                                Conversa conversaSelecionada = listaConversasAtualizada .get(position); // seleciona conversa que foi buscada de forma correta


                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Excluir");
                                builder.setMessage("Tem certeza que deseja excluir essa conversa?");

                                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        conversaSelecionada.removerConversa();


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
                )
        );



        String identificadorUsuario = UsuarioFirebase.getIdUsuario();

       /* database = ConfiguracaoFirebase.getFirebaseDatabase();
        conversasRef = database.child("conversas")
                .child(String.valueOf(horaConversa))
                .child( identificadorUsuario ); */

        database = ConfiguracaoFirebase.getFirebaseDatabase();
        conversasRef = database.child("conversas")
                .child( identificadorUsuario );




        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConversas();

    }

    @Override
    public void onStop() {
        super.onStop();
        conversasRef.removeEventListener( childEventListenerConversas);
    }

    public void recuperarConversas(){

        listaConversas.clear();



        childEventListenerConversas = conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {



                // Recuperar conversas
               Conversa conversa = dataSnapshot.getValue( Conversa.class );

                if(conversa.getUltimaConversa().equals("true")){

                    listaConversas.add( conversa );
                    Collections.reverse(listaConversas);
                    adapter.notifyDataSetChanged();

                }else  if(conversa.getUltimaConversa().equals("false")){

                    listaConversas.add( conversa );
                    adapter.notifyDataSetChanged();


                }




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




}