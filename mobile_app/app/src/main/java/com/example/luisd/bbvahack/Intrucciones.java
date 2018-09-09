package com.example.luisd.bbvahack;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.luisd.bbvahack.R;

public class Intrucciones extends BottomSheetDialogFragment implements Listener {
    private Listener mlistener;
    private fragment_howtowrite mfragment_howtowrite;
    private fragment_howtoread mfragment_howtoread;
    private boolean isDialogDisplayed = false;
    private boolean isWrite = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.instrucciones_layout,container,false);

        Button btnHowtoread = v.findViewById(R.id.btnHowToRead);
        Button btnHowtowrite = v.findViewById(R.id.btnHowToWrite);

        btnHowtowrite.setOnClickListener(view -> showHowToWriteFragment());
        btnHowtoread.setOnClickListener(view -> showHowToReadFragment());

        return v;
    }

    private void showHowToWriteFragment() {
        mfragment_howtowrite = (fragment_howtowrite) getFragmentManager().findFragmentByTag(fragment_howtowrite.TAG);

        if (mfragment_howtowrite == null) {

            mfragment_howtowrite = fragment_howtowrite.newInstance();
        }
        mfragment_howtowrite.show(getFragmentManager(),fragment_howtowrite.TAG);
    }
    private void showHowToReadFragment() {
        mfragment_howtoread = (fragment_howtoread) getFragmentManager().findFragmentByTag(fragment_howtoread.TAG);

        if (mfragment_howtoread == null) {

            mfragment_howtoread = fragment_howtoread.newInstance();
        }
        mfragment_howtoread.show(getFragmentManager(),fragment_howtoread.TAG);
    }

    @Override
    public void onDialogDisplayed() {

        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {

        isDialogDisplayed = false;
        isWrite = false;
    }
}
