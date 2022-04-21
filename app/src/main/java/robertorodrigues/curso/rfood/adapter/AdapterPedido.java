package robertorodrigues.curso.rfood.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import robertorodrigues.curso.rfood.R;
import robertorodrigues.curso.rfood.model.ItemPedido;
import robertorodrigues.curso.rfood.model.Pedido;

public class AdapterPedido extends RecyclerView.Adapter<AdapterPedido.MyViewHolder> {

    private List<Pedido> pedidos;
    private Context context;

    public AdapterPedido(List<Pedido> pedidos, Context context) {
        this.pedidos = pedidos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pedidos, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        Pedido pedido = pedidos.get(i);
        holder.nome.setText( pedido.getNome() );
        holder.cidade.setText( "Cidade: "+pedido.getCidade() );
        holder.bairro.setText("Bairro: "+pedido.getBairro());
        holder.rua.setText("Rua: "+pedido.getRua());
        holder.numero.setText("N° Casa: "+pedido.getNumero());
        holder.telefone.setText("Telefone: "+pedido.getTelefone());

        holder.observacao.setText( "Obs: "+ pedido.getObservacao() );

        List<ItemPedido> itens = new ArrayList<>();
        itens = pedido.getItens();
        String descricaoItens = "";

        int numeroItem = 1;
        Double total = 0.0;
        for( ItemPedido itemPedido : itens ){

            int qtde = itemPedido.getQuantidade();
            String preco = itemPedido.getPreco();
           // total += (qtde * Double.parseDouble(preco) );
            total += (qtde );

            String nome = itemPedido.getNomeProduto();
            descricaoItens += numeroItem + ") " + nome + " / (" + qtde + " x R$ " + preco + ") \n";
            numeroItem++;
        }
        descricaoItens += "Total: R$ " + total;
        holder.itens.setText(descricaoItens);

        int metodoPagamento = pedido.getMetodoPagamento();
        String pagamento = metodoPagamento == 0 ? "Dinheiro" : "Máquina cartão" ;
        holder.pgto.setText( "pgto: " + pagamento );

    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView cidade;
        TextView bairro;
        TextView rua;
        TextView numero;
        TextView telefone;
        TextView pgto;
        TextView observacao;
        TextView itens;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome        = itemView.findViewById(R.id.textPedidoNome);
            cidade    = itemView.findViewById(R.id.textPedidoCidade);
            bairro    = itemView.findViewById(R.id.textPedidoBairro);
            rua    = itemView.findViewById(R.id.textPedidoRua);
            numero    = itemView.findViewById(R.id.textPedidoNumero);
            telefone    = itemView.findViewById(R.id.textPedidoTelefone);
            pgto        = itemView.findViewById(R.id.textPedidoPgto);
            observacao  = itemView.findViewById(R.id.textPedidoObs);
            itens       = itemView.findViewById(R.id.textPedidoItens);
        }
    }

}

