package com.example.luisd.bbvahack;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.luisd.bbvahack.R;

public class fragment_howtowrite extends DialogFragment {
    public static final String TAG = fragment_howtowrite.class.getSimpleName();

    private TextView Instruccion1;
    private TextView Instruccion2;
    private TextView Instruccion3;
    private TextView Instruccion4;
    private TextView Instruccion5;
    private Listener mListener;

    public static fragment_howtowrite newInstance() {
        return new fragment_howtowrite();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_howtowrite,container,false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {

        Instruccion1 = (TextView) view.findViewById(R.id.txtInstruccion1);
        Instruccion2 = (TextView) view.findViewById(R.id.txtInstruccion15);
        Instruccion3 = (TextView) view.findViewById(R.id.txtInstruccion2);
        Instruccion4 = (TextView) view.findViewById(R.id.txtInstruccion3);
        Instruccion5 = (TextView) view.findViewById(R.id.txtInstruccion4);
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
}
