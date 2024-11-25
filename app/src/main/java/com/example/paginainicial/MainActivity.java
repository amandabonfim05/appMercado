package com.example.paginainicial;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLocalizacao = findViewById(R.id.buttonLocation);
        btnLocalizacao.setOnClickListener(view -> checaPermissaoLocalizacao());

        ImageView imagemViewPerfil = findViewById(R.id.imageViewPerfil);
        imagemViewPerfil.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Perfil.class);
            startActivity(intent);
        });

        ImageView imagemViewEngrenagem = findViewById(R.id.imageViewEngrenagem);
        imagemViewEngrenagem.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Config.class);
            startActivity(intent);
        });

        // Adicionando o listener para o botão Descrição
        Button btnDescription = findViewById(R.id.buttonDescription);
        btnDescription.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Description.class);
            startActivity(intent);
        });

        Button btnTrilhas = findViewById(R.id.buttonTrails);
        btnTrilhas.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Trails.class);
            startActivity(intent);
        });
    }

    private void checaPermissaoLocalizacao() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            openLocationActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openLocationActivity();
            } else {
                Toast.makeText(this, "Permissão de localização necessária", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openLocationActivity() {
        Intent intent = new Intent(this, Location.class);
        startActivity(intent);
    }
}
