package robertorodrigues.curso.rfood.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import robertorodrigues.curso.rfood.R;
import robertorodrigues.curso.rfood.model.Conversa;
import robertorodrigues.curso.rfood.model.Pedido;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.MyViewHolder> {

    private List<Conversa> conversas;
    private Context context;

    public ConversasAdapter(List<Conversa> lista, Context c) {
        this.conversas = lista;
        this.context = c;
    }

    public List<Conversa> getConversas(){
        return this.conversas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos, parent, false );
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Conversa conversa = conversas.get( position );
        holder.ultimaMensagem.setText( conversa.getUltimaMensagem() );

        if(conversa.getIsEmpresa().equals("true")){
            Pedido empresa = conversa.getUsuarioExibicaoPedido();
            holder.nome.setText( empresa.getNomeEmpresa());

            if ( empresa.getUrlImagemEmpresa() != null ){
                // Uri uri = Uri.parse( usuario.getUrlImagem() );
                //Carregar imagem
                String urlImagem = empresa.getUrlImagemEmpresa();
                Picasso.get().load( urlImagem ).into( holder.foto);

            }else {
                holder.foto.setImageResource(R.drawable.padrao);
            }


        } else if(conversa.getIsEmpresa().equals("false")){
            Pedido usuario = conversa.getUsuarioExibicaoPedido();
            holder.nome.setText( usuario.getNome());

            if ( usuario.getUrlImagem() != null ){
                // Uri uri = Uri.parse( usuario.getUrlImagem() );
                //Carregar imagem
                String urlImagem = usuario.getUrlImagem();
                Picasso.get().load( urlImagem ).into( holder.foto);

            }else {
                holder.foto.setImageResource(R.drawable.padrao);
            }
        }




         /*  if(conversa.getIsEmpresa().equals("true")){
              // Empresa empresa = conversa.getEmpresaExibicao();

           }else if(conversa.getIsEmpresa().equals("false")){
               Pedido usuario = conversa.getUsuarioExibicaoPedido();
               holder.nome.setText( usuario.getNome() );

               if ( usuario.getUrlImagem() != null ){
                   // Uri uri = Uri.parse( usuario.getUrlImagem() );
                   //Carregar imagem
                   String urlImagem = usuario.getUrlImagem();
                   Picasso.get().load( urlImagem ).into( holder.foto);

               }else {
                   holder.foto.setImageResource(R.drawable.padrao);
               }

           } */




    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView foto;
        TextView nome, ultimaMensagem;

        public MyViewHolder(View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageViewFotoContatos);
            nome = itemView.findViewById(R.id.textNomeContato);
            ultimaMensagem = itemView.findViewById(R.id.textEmailContato);

        }
    }


}
