package com.example.paginainicial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TrilhaAdaptada extends RecyclerView.Adapter<TrilhaAdaptada.ViewHolder> {

    private List<Trilha> trailList;

    private Context context;
    public TrilhaAdaptada(Context context, List<Trilha> trailList) {
        this.trailList = trailList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Trilha trail = trailList.get(position);

        // Formatar a data
        SimpleDateFormat formatarData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String dataFormatada = formatarData.format(new Date(trail.getStartDate()));

        long totalSegundos = trail.getDuracao() / 1000; // Convertendo milissegundos em segundos
        long minutos = totalSegundos / 60; // Calculando os minutos
        long segundos = totalSegundos % 60; // Calculando os segundos restantes

        holder.startDate.setText("Start Date: " + dataFormatada);
        holder.velocidadeMedia.setText(String.format(Locale.US, "Avg Speed: %.2f km/h", trail.getVelocidadeMedia()));
        holder.distancia.setText(String.format(Locale.US, "Distance: %.2f km", trail.getDistancia()));
        holder.duracao.setText(String.format(Locale.US, "Duration: %d min, %d sec", minutos, segundos));
        holder.btnDeletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    deletaTrilha(trailList.get(currentPosition).getId());
                    trailList.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                }
            }
        });
    }

    private void deletaTrilha(int trilhaId) {
        BancoDeDados db = new BancoDeDados(context);
        db.deletarTrilha(trilhaId);
    }

    @Override
    public int getItemCount() {
        return trailList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView startDate, velocidadeMedia, distancia, duracao;
        public Button btnDeletar;
        public ViewHolder(View view) {
            super(view);
            startDate = view.findViewById(R.id.startDate);
            velocidadeMedia = view.findViewById(R.id.averageSpeed);
            distancia = view.findViewById(R.id.distance);
            duracao = view.findViewById(R.id.duration);
            btnDeletar = itemView.findViewById(R.id.deleteButton);
        }
    }
}
