package com.example.paginainicial;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class Trails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trails);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewTrails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Configura o layout manager

        BancoDeDados db = new BancoDeDados(this);
        List<Trilha> trails = db.pegarTodasTrilhas(); // Recupera as trilhas do banco de dados

        if (trails != null && !trails.isEmpty()) {
            TrilhaAdaptada adapter = new TrilhaAdaptada(this, trails);
            recyclerView.setAdapter(adapter); // Configura o adapter com as trilhas recuperadas
        } else {
            // Caso não haja trilhas
            Toast.makeText(this, "Nenhuma Trilha Encontrada", Toast.LENGTH_SHORT).show();
        }
    }
}
