package com.pierre.biojoux.project;


import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import GarbageBin.GarbageBin;

import java.util.Date;
import java.util.Locale;

public class SeeDetails extends Fragment {

    private GarbageBin Bin = null;
    private TextView EmptiedView;


    public SeeDetails() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_details, container, false);
        Bin = (GarbageBin) getActivity().getIntent().getExtras().getSerializable("Bin");
        ImageView statusImg = (ImageView) v.findViewById(R.id.detailsimg);
        String status = Bin.getStatus();

        if (status.equals("Full")) {
            statusImg.setImageResource(R.drawable.fullbin);
        }
        else if (status.equals("Medium")) {
            statusImg.setImageResource(R.drawable.medbin);
        }
        else
        {
            statusImg.setImageResource(R.drawable.emptybin);
        }

        TextView detailID = (TextView) v.findViewById(R.id.detailID);
        detailID.setText(String.format(Locale.ENGLISH, "%d", Bin.getID()));

        EmptiedView = (TextView) v.findViewById(R.id.detailTime);
        EmptiedView.setText(Bin.getEmptied());

        TextView fullView = (TextView) v.findViewById(R.id.detailStatus);
        fullView.setText(Bin.getStatus());

        Button buttonEmpty = (Button) v.findViewById(R.id.btnEmpty);
        buttonEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                empty();
            }
        });

        Button buttonReturn = (Button) v.findViewById(R.id.btnReturn);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quit();
            }
        });

        Button buttonMap = (Button) v.findViewById(R.id.btnGoto);
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewOnMap();
            }
        });
        return v;
    }

    private void viewOnMap() {
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        String doIt = "OneGarbageBin";
        intent.putExtra("doIt", doIt);
        intent.putExtra("Bin", Bin);
        startActivity(intent);
    }


    private void quit(){
        getActivity().finish();
    }

    private void empty(){
        String emptied = java.text.SimpleDateFormat.getDateInstance().toString();
        EmptiedView.setText(emptied);
        Bin.setEmptied(emptied);
    }
}
