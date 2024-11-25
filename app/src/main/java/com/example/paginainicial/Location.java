package com.example.paginainicial;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class Location extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "LocationActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ArrayList<android.location.Location> locationLista = new ArrayList<>();
    private long fimTempo = 0;
    private Button bntInicio, btnFim, btnObter;
    private boolean isAtualizacaoLocalizacaoAtiva = false;
    private Handler manipuladorTempo = new Handler();
    private Runnable tempoCorrido;
    private long inicioTempo = 0;
    private float ultimaVelocidadeConhecida = 0; // Variável para armazenar a última velocidade conhecida
    private String unidadeVelocidade = "m/s"; // Variável de classe para armazenar a unidade de velocidade

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_main);
        configurarFragmentoMapa();
        inicializaLocalizacaoGerente();
        configuraBtn();

        ImageView imagemViewSeta = findViewById(R.id.imageViewSeta);
        imagemViewSeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Location.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnObter = findViewById(R.id.getButton);
        btnObter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    inicioAtualizacoesLocalizacao();
                }
            }
        });

        tempoCorrido = new Runnable() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis() - inicioTempo;
                int segundos = (int) (millis / 1000);
                int minutos = segundos / 60;
                segundos = segundos % 60;

                atualizarTempoCalorias(minutos, segundos);
                manipuladorTempo.postDelayed(this, 500);
            }
        };

        carregarPreferencias();  // Carrega as preferências inicialmente
    }

    private void configurarFragmentoMapa() {
        SupportMapFragment mapaFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapaFragment != null) {
            mapaFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "Map Fragment not found");
            Toast.makeText(this, "Map Fragment not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void inicializaLocalizacaoGerente() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull android.location.Location location) {
                atualizaLocalizacaoMapa(location);
                atualizaLocalizacaoTexto(location);
                ultimaVelocidadeConhecida = location.getSpeed(); // Atualiza a última velocidade conhecida
                locationLista.add(location); // Atualiza a última velocidade conhecida
            }


        };
    }

    private void configuraBtn() {
        bntInicio = findViewById(R.id.startButton);
        btnFim = findViewById(R.id.stopButton);

        bntInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicioAtualizacoesLocalizacao();
                inicioTempo = System.currentTimeMillis();
                manipuladorTempo.postDelayed(tempoCorrido, 0);
            }
        });

        btnFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pararAtualizacoesLocalizacao();
                fimTempo = System.currentTimeMillis();
                if (!locationLista.isEmpty()){
                    long duracao = fimTempo - inicioTempo; // Duração em milissegundos
                    double totalDistancia = calculateTotalDistance(locationLista);
                    double velocidadeMedia = totalDistancia / (duracao / 3600000.0); // Velocidade média em km/h
                    salvaDadosTrilha(inicioTempo, velocidadeMedia, totalDistancia, duracao);
                    locationLista.clear();
                }else {
                    Toast.makeText(Location.this, "Insufficient data for calculations.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private double calculateTotalDistance(ArrayList<android.location.Location> locations) {
        float totalDistancia = 0;
        android.location.Location previousLocation = null;
        for (android.location.Location location : locations) {
            if (previousLocation != null) {
                totalDistancia += previousLocation.distanceTo(location);
            }
            previousLocation = location;
        }
        return totalDistancia / 1000.0; // Retorna em quilômetros
    }

    private void salvaDadosTrilha(long inicioTempo, double velocidadeMedia, double totalDistancia, long duracao) {
        SQLiteDatabase db = new BancoDeDados(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BancoDeDados.COLUMN_START_DATA, inicioTempo);
        values.put(BancoDeDados.COLUMN_AVERAGE_SPEED, velocidadeMedia);
        values.put(BancoDeDados.COLUMN_KILOMETER, totalDistancia);
        values.put(BancoDeDados.COLUMN_TIME, duracao);
        db.insert(BancoDeDados.TABLE_NAME, null, values);
        db.close();
    }


    private void inicioAtualizacoesLocalizacao() {
        if (checkLocationPermission() && !isAtualizacaoLocalizacaoAtiva) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (mMap != null) {
                    mMap.setMyLocationEnabled(true);
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                isAtualizacaoLocalizacaoAtiva = true;
            } else {
                Toast.makeText(this, "GPS not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void pararAtualizacoesLocalizacao() {
        if (isAtualizacaoLocalizacaoAtiva) {
            locationManager.removeUpdates(locationListener);
            isAtualizacaoLocalizacaoAtiva = false;
            manipuladorTempo.removeCallbacks(tempoCorrido);
        }
    }

    private void atualizaLocalizacaoMapa(android.location.Location location) {
        if (mMap != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
        }
    }

    private void atualizaLocalizacaoTexto(android.location.Location location) {
        TextView textView = findViewById(R.id.textView);
        float velocidade = (location != null) ? location.getSpeed() : 0;
        if (unidadeVelocidade.equals("km/h")) {
            velocidade *= 3.6;
        }
        String texto = getString(R.string.latitude) + " " + location.getLatitude() +
                "\n" + getString(R.string.longitude) + " " + location.getLongitude() +
                "\n" + getString(R.string.velocity) + ": " + String.format("%.2f", velocidade) + " " + unidadeVelocidade;
        textView.setText(texto);
    }

    private void carregarPreferencias() {
        SharedPreferences sharedPref = getSharedPreferences("MyAppSettings", Context.MODE_PRIVATE);
        unidadeVelocidade = sharedPref.getString("SpeedUnit", "m/s");
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarPreferencias();
        if (locationManager != null && checkLocationPermission()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    private void atualizarTempoCalorias(int minutos, int segundos) {
        TextView textView = findViewById(R.id.textView);
        String textoQueExiste = textView.getText().toString();

        double calorias = calculateCaloriesBurned(ultimaVelocidadeConhecida, 80, minutos * 60 + segundos);

        String novaInfo = "Tempo: " + minutos + ":" + String.format("%02d", segundos) +
                "\nCalorias: " + String.format("%.2f", calorias) + " kcal";

        textView.setText(textoQueExiste + "\n" + novaInfo);
    }

    private double calculateCaloriesBurned(float velocidadeMps, int pesoKg, long tempoSeconds) {
        double velocidadeKmph = velocidadeMps * 3.6;
        return (velocidadeKmph * pesoKg * 0.0175) * (tempoSeconds / 3600.0);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
