package robertorodrigues.curso.rfood.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.List;

import robertorodrigues.curso.rfood.R;
import robertorodrigues.curso.rfood.model.Produto;

/**
 * Created by Jamilton
 */


public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder>{

    private List<Produto> produtos;
    private Context context;

    public AdapterProduto(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Produto produto = produtos.get(i);
        holder.nome.setText(produto.getNome());
        holder.descricao.setText(produto.getDescricao());
        holder.valor.setText("R$ " + produto.getPreco());

        //Carregar imagem
       //  String urlImagem = produto.getUrlImagem();
       // Picasso.get().load( urlImagem ).into( holder.imagemProduto );



        ImageListener imageListener =  new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                String urlString = produto.getFotosProduto().get(position);
                Picasso.get().load(urlString).into(imageView);
            }
        };

        holder.carouselView.setPageCount(produto.getFotosProduto().size()); // pega a quantidades de fotos
        holder.carouselView.setImageListener(imageListener);
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imagemProduto;
        TextView nome;
        TextView descricao;
        TextView valor;
        CarouselView carouselView;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textNomeRefeicao);
            descricao = itemView.findViewById(R.id.textDescricaoRefeicao);
            valor = itemView.findViewById(R.id.textPreco);
            imagemProduto = itemView.findViewById(R.id.imageProdutoEmpresa2);
            carouselView = itemView.findViewById(R.id.carouselView);


        }
    }
}