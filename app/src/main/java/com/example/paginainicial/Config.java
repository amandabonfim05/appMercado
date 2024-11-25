package com.example.paginainicial;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Config extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private RadioGroup rGExercicio, rGVelocidade, rGOrientacaoMapa, rGTipoMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_main);

        sharedPreferences = getSharedPreferences("ConfigPrefs", Context.MODE_PRIVATE);

        rGExercicio = findViewById(R.id.radioGroupExercicio);
        rGVelocidade = findViewById(R.id.radioGroupVelocidade);
        rGOrientacaoMapa = findViewById(R.id.radioGroupOrientacaoMapa);
        rGTipoMapa = findViewById(R.id.radioGroupTipoMapa);

        ImageView imagemViewVoltar = findViewById(R.id.imageViewSeta);
        imagemViewVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Config.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button btnSalvarConfig = findViewById(R.id.buttonSaveSettings);
        btnSalvarConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarTodasPreferencias();
                Toast.makeText(Config.this, "Configurações salvas!", Toast.LENGTH_SHORT).show();
            }
        });

        carregarPreferenciasSalvas();
    }

    private void carregarPreferenciasSalvas() {
        // Carregar a preferência do tipo de exercício
        String tipoExercicio = sharedPreferences.getString("ExerciseType", "");
        if (!tipoExercicio.isEmpty()) {
            int id = tipoExercicio.equals(getString(R.string.caminhada)) ? R.id.radioButtonCaminhada :
                    tipoExercicio.equals(getString(R.string.corrida)) ? R.id.radioButtonCorrida :
                            R.id.radioButtonBicicleta;
            ((RadioButton)findViewById(id)).setChecked(true);
        }

        // Carregar a preferência de unidade de velocidade
        String unidadeVelocidade = sharedPreferences.getString("SpeedUnit", "");
        if (unidadeVelocidade.equals("m/s")) {
            ((RadioButton)findViewById(R.id.radioButtonMs)).setChecked(true);
        } else if (unidadeVelocidade.equals("km/h")) {
            ((RadioButton)findViewById(R.id.radioButtonKmH)).setChecked(true);
        }

        // Carregar a preferência de orientação do mapa
        String orientacaoMapa = sharedPreferences.getString("MapOrientation", "");
        if (orientacaoMapa.equals(getString(R.string.nenhuma_manual))) {
            ((RadioButton)findViewById(R.id.radioButtonNenhuma)).setChecked(true);
        } else if (orientacaoMapa.equals(getString(R.string.north_up))) {
            ((RadioButton)findViewById(R.id.radioButtonNorthUp)).setChecked(true);
        } else if (orientacaoMapa.equals(getString(R.string.course_up))) {
            ((RadioButton)findViewById(R.id.radioButtonCourseUp)).setChecked(true);
        }

        // Carregar a preferência do tipo de mapa
        String tipoMapa = sharedPreferences.getString("MapType", "");
        if (tipoMapa.equals(getString(R.string.vetorial))) {
            ((RadioButton)findViewById(R.id.radioButtonVetorial)).setChecked(true);
        } else if (tipoMapa.equals(getString(R.string.sat_lite))) {
            ((RadioButton)findViewById(R.id.radioButtonSatelite)).setChecked(true);
        }
    }

    private void salvarTodasPreferencias() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int exercicioSelecionadoId = rGExercicio.getCheckedRadioButtonId();
        String tipoExercicio = ((RadioButton)findViewById(exercicioSelecionadoId)).getText().toString();
        editor.putString("ExerciseType", tipoExercicio);

        int velocidadeSelecionadaId = rGVelocidade.getCheckedRadioButtonId();
        String unidadeVelocidade = velocidadeSelecionadaId == R.id.radioButtonMs ? "m/s" : "km/h";
        editor.putString("SpeedUnit", unidadeVelocidade);

        int orientacaoMapaSelecionadaId = rGOrientacaoMapa.getCheckedRadioButtonId();
        String orientacaoMapa = ((RadioButton)findViewById(orientacaoMapaSelecionadaId)).getText().toString();
        editor.putString("MapOrientation", orientacaoMapa);

        int tipoMapaSelecionadoId = rGTipoMapa.getCheckedRadioButtonId();
        String tipoMapa = ((RadioButton)findViewById(tipoMapaSelecionadoId)).getText().toString();
        editor.putString("MapType", tipoMapa);

        editor.apply();
    }
}
