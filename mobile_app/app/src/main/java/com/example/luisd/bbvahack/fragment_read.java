package com.example.luisd.bbvahack;

import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luisd.bbvahack.R;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class fragment_read extends android.app.DialogFragment {
    public static final String TAG = fragment_read.class.getSimpleName();

    public static fragment_read newInstance() {

        return new fragment_read();
    }

    private EditText txtclave;
    private TextView tvMessage;
    private TextView txtCuenta;
    private TextView txtFecha;
    private TextView txtMonto;
    private Listener mListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_read,container,false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        tvMessage = (TextView) view.findViewById(R.id.tv_message);
        txtclave = (EditText) view.findViewById(R.id.txtClave);
        txtCuenta = (TextView) view.findViewById(R.id.txtCuenta);
        txtFecha = (TextView) view.findViewById(R.id.txtFecha);
        txtMonto =  (TextView) view.findViewById(R.id.txtMonto);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (activity_main)context;
        mListener.onDialogDisplayed();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onDialogDismissed();
    }

    public void onNfcDetected(Ndef ndef){

        readFromNFC(ndef);
    }

    private void readFromNFC(Ndef ndef) {

        try {
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();

            try{
                String message = new String(ndefMessage.getRecords()[0].getPayload());
                message = message.replace("[","");
                message = message.replace("]","").replaceAll("\\s","");
                String[] split = message.split(",");
                byte[] mes = new byte[split.length];
                for (int i = 0; i<mes.length; i++){
                        mes[i] = Byte.parseByte(split[i]);
                }
                message=descifra(mes,txtclave.getText().toString());
                Log.d(TAG, "readFromNFC: "+message);
                txtCuenta.setText(message);
                tvMessage.setText("Datos de tranferencia guardados correctamente");
            }catch (Exception e){
                txtMonto.setText(e.getLocalizedMessage());
            }
            ndef.close();
        } catch (IOException | FormatException e) {
            e.printStackTrace();
        }
    }

    public String descifra(byte[] cifrado, String clave) throws Exception {
        final Cipher aes = obtieneCipher(false, clave);
        final byte[] bytes = aes.doFinal(cifrado);
        final String sinCifrar = new String(bytes, "UTF-8");
        return sinCifrar;
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
