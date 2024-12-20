package com.example.ciber_residuos.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ciber_residuos.R;
import com.example.ciber_residuos.helper.Anuncio;
import com.example.ciber_residuos.helper.ConfiguracaoFirebase;
import com.example.ciber_residuos.helper.Permissoes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class CadastrarAnuncioActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText campoTitulo, campoDescricao, campoValor, campoTelefone;
    private Spinner campoEstado, campoCategoria;
    private ImageView imagem1, imagem2, imagem3;
    private final String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private final List<String> listaFotosRecuperadas = new ArrayList<>();

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private int requestCode;
    private Anuncio anuncio;
    private StorageReference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);
        inicializarComponentes();

        carregarDadosSpinner();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Configurações iniciais
        storage = ConfiguracaoFirebase.getFirebaseStorage();

        // Validar permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        // Adicionar TextWatcher para formatar o telefone
        adicionarTextWatcherTelefone();

        // Inicializar ActivityResultLauncher
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri imagemSelecionada = data.getData();
                            String caminhoImagem = imagemSelecionada.toString();

                            // Configura imagem no ImageView
                            if (requestCode == 1) {
                                imagem1.setImageURI(imagemSelecionada);
                            } else if (requestCode == 2) {
                                imagem2.setImageURI(imagemSelecionada);
                            } else if (requestCode == 3) {
                                imagem3.setImageURI(imagemSelecionada);
                            }

                            listaFotosRecuperadas.add(caminhoImagem);
                        }
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imageCadastro1) {
            escolherImagem(1);
        } else if (id == R.id.imageCadastro2) {
            escolherImagem(2);
        } else if (id == R.id.imageCadastro3) {
            escolherImagem(3);
        } else {
            throw new IllegalStateException("Unexpected value: " + id);
        }
    }

    public void escolherImagem(int requestCode) {
        this.requestCode = requestCode;
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(i);
    }

    private void carregarDadosSpinner() {
        // Configura spinner de estados
        String[] estados = getResources().getStringArray(R.array.estados);
        List<String> listaEstados = new ArrayList<>();
        listaEstados.add("Estados"); // Título do spinner
        listaEstados.addAll(List.of(estados));

        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, listaEstados
        );
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoEstado.setAdapter(adapterEstado);
        campoEstado.setSelection(0);

        // Configura spinner de categorias
        String[] categorias = getResources().getStringArray(R.array.categorias);
        List<String> listaCategorias = new ArrayList<>();
        listaCategorias.add("Categorias"); // Título do spinner
        listaCategorias.addAll(List.of(categorias));

        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, listaCategorias
        );
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCategoria.setAdapter(adapterCategoria);
        campoCategoria.setSelection(0);
    }




    private void inicializarComponentes() {
        campoTitulo = findViewById(R.id.editTitulo);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);
        campoEstado = findViewById(R.id.spinnerEstado);
        campoCategoria = findViewById(R.id.spinnerCategoria);
        campoTelefone = findViewById(R.id.editTelefone);
        imagem1 = findViewById(R.id.imageCadastro1);
        imagem2 = findViewById(R.id.imageCadastro2);
        imagem3 = findViewById(R.id.imageCadastro3);
        imagem1.setOnClickListener(this);
        imagem2.setOnClickListener(this);
        imagem3.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", (dialog, which) -> finish());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void adicionarTextWatcherTelefone() {
        campoTelefone.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private boolean isDeleting = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isDeleting = count > 0;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    campoTelefone.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("\\D", "");
                    StringBuilder formatted = new StringBuilder();

                    if (!cleanString.isEmpty()) {
                        formatted.append("(").append(cleanString.substring(0, Math.min(cleanString.length(), 2)));
                    }

                    if (cleanString.length() > 2) {
                        formatted.append(") ").append(cleanString.substring(2, Math.min(cleanString.length(), 3)));
                    }

                    if (cleanString.length() > 3) {
                        formatted.append(cleanString.substring(3, Math.min(cleanString.length(), 7)));
                    }

                    if (cleanString.length() > 7) {
                        formatted.append("-").append(cleanString.substring(7, Math.min(cleanString.length(), 11)));
                    }

                    current = formatted.toString();
                    campoTelefone.setText(current);
                    campoTelefone.setSelection(current.length());

                    campoTelefone.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    //Metodo Responsavel por salvar o anuncio
    public void salvarAnuncio() {

//Salvar as imagens do anuncio
        for (int i = 0; i < listaFotosRecuperadas.size(); i++){
            String urlImagem = listaFotosRecuperadas.get(i);//Usando o numero "0" para o valor da primeira imagem pois fica mais intuitivo com relação a arrays
            int tamanhoLista = listaFotosRecuperadas.size();
            salvarFotosStorage(urlImagem,tamanhoLista,i);//salvamento da foto(o metodo sabera quantas fotos estão sendo salvas)
        }

        Toast.makeText(this, "Anúncio salvo!", Toast.LENGTH_SHORT).show();

        // Redirecionar para "Meus Anúncios"
        Intent intent = new Intent(CadastrarAnuncioActivity.this, MeusAnunciosActivity.class);
        startActivity(intent);
        finish();
    }

    private void salvarFotosStorage(String url, int totalFotos, int contador){
        //criar nó no Storage
        StorageReference imagemAnuncio = storage.child("imagem")
                .child("anuncio")
                .child(anuncio.getIdAnuncio())
                .child("imagem" + contador);

        //Fazer upload do arquivo
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(url)); // converte a (Url String) para "Uri"
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Aqui a correção é feita: agora usamos o "taskSnapshot" para obter a URL da imagem
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri -> {
                    // Agora você tem a URL da imagem no Firebase
                    Log.i("INFO", "URL da imagem: " + uri.toString());
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            //Mensagem de erro para o (Upload)
            public void onFailure(@NonNull Exception e) {
                exibirMensagemErro("Falha ao fazer upload");
                Log.i("INFO", "Falha ao fazer upload: " + e.getMessage());
            }
        });
    }

    //linha responsavel pela configuração/recuperção do anuncio
    private Anuncio configurarAnuncio(){

        String estado = campoEstado.getSelectedItem().toString();
        String categoria = campoCategoria.getSelectedItem().toString();
        String titulo = campoTitulo.getText().toString();
        String valor = campoValor.getText().toString();
        String telefone = campoTelefone.getText().toString();
        if( campoTelefone.getText() != null ){
        }
        String descricao = campoDescricao.getText().toString();

        // Para propósitos de depuração
        Log.d("salvar", "Titulo: " + titulo);
        Log.d("salvar", "Descricao: " + descricao);
        Log.d("salvar", "Telefone: " + telefone);

        Anuncio anuncio = new Anuncio();//cria um novo objeto Anuncio
        anuncio.setEstado( estado );
        anuncio.setCategoria( categoria );
        anuncio.setTitulo( titulo );
        anuncio.setValor( valor);
        anuncio.setTelefone( telefone );
        anuncio.setDescricao( descricao );
        return anuncio;// retorna ao objeto criado
    }

//Parte responsavel por validar o anuncio, conferindo se o usuario inseriu os dados em seu respectivos campos
    public void validarDadosAnuncio(View view){

        anuncio = configurarAnuncio();

        if( listaFotosRecuperadas.size() != 0 ){
            if (!anuncio.getEstado().equals("Estados") ){
                if (!anuncio.getCategoria().equals("Categorias") ){
                    if( !anuncio.getTitulo().isEmpty() ){
                        if( !anuncio.getValor().isEmpty() && !anuncio.getValor().equals("0") ){
                            if( !anuncio.getTelefone().isEmpty() && anuncio.getTelefone().length() >=10 ){
                                if( !anuncio.getDescricao().isEmpty() ){
                                    salvarAnuncio();
                                }else {
                                    exibirMensagemErro("Preencha o campo Descrição!");
                                }
                            }else {
                                exibirMensagemErro("Preencha o campo Telefone, digite ao menos 10 números!");
                            }
                        }else {
                            exibirMensagemErro("Preencha o campo Valor, numero maior que 0!");
                        }
                    }else {
                        exibirMensagemErro("Preencha o campo título!");
                    }
                }else {
                    exibirMensagemErro("Preencha o campo categoria!");
                }
            }else {
                exibirMensagemErro("Preencha o campo estado!");
            }
        }else {
            exibirMensagemErro("Selecione ao menos uma foto!");
        }

    }

    private void exibirMensagemErro(String mensagem){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }


}