package com.example.paginainicial;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Perfil extends AppCompatActivity {

    private RadioGroup rGSexo;
    private EditText editaTextoPeso, editaTextoAltura, editaTextoDataNascimento;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_main);

        rGSexo = findViewById(R.id.radioGroupSexo);
        editaTextoPeso = findViewById(R.id.editTextPeso);
        editaTextoAltura = findViewById(R.id.editTextAltura);
        editaTextoDataNascimento = findViewById(R.id.editTextDataNascimento);

        sharedPreferences = getSharedPreferences("PerfilPrefs", Context.MODE_PRIVATE);

        TextView textoViewVoltar = findViewById(R.id.textViewVoltar);

        textoViewVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Perfil.this, MainActivity.class);
                startActivity(intent);
            }
        });
        carregarDadosSalvos();

        Button btnCadastrar = findViewById(R.id.buttonCadastrar);
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrarPerfil();
            }
        });
    }
    private void carregarDadosSalvos() {
        String sexo = sharedPreferences.getString("sexo", "");
        String peso = sharedPreferences.getString("peso", "");
        String altura = sharedPreferences.getString("altura", "");
        String dataNascimento = sharedPreferences.getString("dataNascimento", "");

        if (!TextUtils.isEmpty(sexo)) {
            if (sexo.equals("Masculino")) {
                ((RadioButton) findViewById(R.id.radioButtonMasculino)).setChecked(true);
            } else if (sexo.equals("Feminino")) {
                ((RadioButton) findViewById(R.id.radioButtonFeminino)).setChecked(true);
            }
        }

        editaTextoPeso.setText(peso);
        editaTextoAltura.setText(altura);
        editaTextoDataNascimento.setText(dataNascimento);
    }
    private void cadastrarPerfil() {
        int selecionarRGId = rGSexo.getCheckedRadioButtonId();
        String sexo = (selecionarRGId == R.id.radioButtonMasculino) ? "Masculino" : "Feminino";

        String peso = editaTextoPeso.getText().toString();
        String altura = editaTextoAltura.getText().toString();
        String dataNascimento = editaTextoDataNascimento.getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sexo", sexo);
        editor.putString("peso", peso);
        editor.putString("altura", altura);
        editor.putString("dataNascimento", dataNascimento);
        editor.apply();
        
        Toast.makeText(this, "Dados salvos com sucesso.", Toast.LENGTH_SHORT).show();
    }

}
