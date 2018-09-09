package com.example.luisd.bbvahack;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class activity_main extends AppCompatActivity implements Listener {

    //TAG PARA IDENTIFICAR LA CLASE
    public static final String TAG = activity_main.class.getSimpleName();

    //COMPONENTES DEL ACTIVITY
    private EditText txtMonto;
    private Button mBtWrite;
    private Button mBtRead;
    private Button btnInstrucciones;

    //VARIABLES DE DATOS PARA LA TARJETA
    private String Fecha;
    private String Passcode;

    //FRAGMENTS PARA ESCRITURA Y LECTURA DE NFC
    private fragment_write mNfcWriteFragment;
    private fragment_read mNfcReadFragment;

    //ESTADOS DENTRO DE LA APLICACION
    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;

    //ADAPTADOR NFC
    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SE INICIALIZAN LOS COMPONENTES DEL ACTIVITY
        initViews();
        initNFC();
    }

    //CONECCION DE COMPONENTES Y FUNCIONES DE LOS BOTONES
    private void initViews() {

        //EDIT TEXT
        txtMonto = (EditText) findViewById(R.id.txtMonto);
        //BUTTONS
        mBtWrite = (Button) findViewById(R.id.btn_write);
        mBtRead = (Button) findViewById(R.id.btn_read);
        btnInstrucciones = (Button) findViewById(R.id.btnInstructions);

        //FUNCIONES DE LOS BUTTONS
        mBtRead.setOnClickListener(view -> showReadFragment());
        mBtWrite.setOnClickListener(view -> showWriteFragment());
        btnInstrucciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intrucciones intrucciones = new Intrucciones();
                intrucciones.show(getSupportFragmentManager(),"Instrucciones");
            }
        });
    }
    private void initNFC(){

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    //FUNCION PARA ABRIR LA PANTALLA DE CONFIGURACIONES DEL DISPOSITIVO PARA ACTIVAR EL NFC Y USAR LA APLICACION
    private void showWirelessSettings() {
        Toast.makeText(this, "Necesitas habilitar tu NFC !", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }

    //SE INICIA EL FRAGMENT PARA LEER DATOS DE LA TARJETA
    private void showReadFragment() {

        mNfcReadFragment = (fragment_read) getFragmentManager().findFragmentByTag(fragment_read.TAG);

        if (mNfcReadFragment == null) {

            mNfcReadFragment = fragment_read.newInstance();
        }
        mNfcReadFragment.show(getFragmentManager(),fragment_read.TAG);

    }

    //SE INICIA EL FRAGMENT ESCRIBIR DATOS A LA TARJETA
    private void showWriteFragment() {
        isWrite = true;

        mNfcWriteFragment = (fragment_write) getFragmentManager().findFragmentByTag(fragment_write.TAG);

        if (mNfcWriteFragment == null) {

            mNfcWriteFragment = fragment_write.newInstance();
        }
        mNfcWriteFragment.show(getFragmentManager(),fragment_write.TAG);

    }

    //METODOS DE LA INTERFAZ LISTENER PARA MANEJO DE FRAGMENTS
    @Override
    public void onDialogDisplayed() {

        isDialogDisplayed = true;
    }
    @Override
    public void onDialogDismissed() {

        isDialogDisplayed = false;
        isWrite = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //ACCIONES PARA CUANDO SE DETECTA UNA TARJETA NFC
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected,tagDetected,ndefDetected};
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        if(mNfcAdapter!= null) {
            //SI LA NFC NO ESTA ENCENDIDA SE LLAMA AL METODO PARA ABRIR LAS CONFIGURACIONES DEL DISPOSITIVO
            if (!mNfcAdapter.isEnabled())
                showWirelessSettings();
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mNfcAdapter!= null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    //ACCIONES PARA ESCRIBIR EN LA TARJETA
    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if(tag != null) {
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);

            if (isDialogDisplayed) {

                if (isWrite) {
                    //CAPTURAMOS LA FE
                    Date anotherCurDate = new Date();
                    Fecha = anotherCurDate.toString();

                    String cuenta = "5544554488778877";
                    StringBuilder builder;

                    //Obtenemos los datos de las cajas de texto
                    //Clave y valor para Nombre
                    builder = new StringBuilder();
                    builder.append("Monto:").append(txtMonto.getText().toString()).append("\n").
                            append("Fecha:").append(Fecha).append("\n").
                            append("Cuenta:").append(cuenta).append("\n");


                    String Datos = builder.toString();

                    String messageToWrite = Datos;

                    Integer clave = ThreadLocalRandom.current().nextInt(100000,999999);
                    //CIFRAR AQUI messageToWrite
                    try{
                        byte[] cifrado = cifra(messageToWrite,clave.toString());
                        messageToWrite = Arrays.toString(cifrado);
                    } catch (Exception e) {
                        Toast.makeText(this,e.getMessage().toString(),Toast.LENGTH_LONG);
                    }

                    mNfcWriteFragment = (fragment_write) getFragmentManager().findFragmentByTag(fragment_write.TAG);
                    mNfcWriteFragment.onNfcDetected(ndef,messageToWrite,clave.toString());

                } else {

                    mNfcReadFragment = (fragment_read) getFragmentManager().findFragmentByTag(fragment_read.TAG);
                    mNfcReadFragment.onNfcDetected(ndef);
                }
            }
        }
    }

    public byte[] cifra(String sinCifrar, String clave) throws Exception {
        final byte[] bytes = sinCifrar.getBytes("UTF-8");
        final Cipher aes = obtieneCipher(true, clave);
        final byte[] cifrado = aes.doFinal(bytes);
        return cifrado;
    }

    private Cipher obtieneCipher(boolean paraCifrar, String clave) throws Exception {
        final String frase = clave;
        final MessageDigest digest = MessageDigest.getInstance("SHA");
        digest.update(frase.getBytes("UTF-8"));
        final SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");

        final Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
        if (paraCifrar) {
            aes.init(Cipher.ENCRYPT_MODE, key);
        } else {
            aes.init(Cipher.DECRYPT_MODE, key);
        }

        return aes;
    }
}
