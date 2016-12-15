package com.pierre.biojoux.project;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import GarbageBin.GarbageBin;
import GarbageBin.GarbageHandler;

public class CheckStatus extends ListFragment {


    private ArrayList<GarbageBin> binList = null;
    private GarbageHandler db = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        Button buttonBack = (Button) view.findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quit();
            }
        });
        db = new GarbageHandler(getActivity());

        binList = (ArrayList<GarbageBin>) getActivity().getIntent().getExtras().getSerializable("binList");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
        GarbageAdapter adapter = new GarbageAdapter(getActivity(), R.layout.fragment_status, binList);
        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        binList.set(1, db.getBin(1));
        GarbageAdapter adapter = new GarbageAdapter(getActivity(), R.layout.fragment_status, binList);
        setListAdapter(adapter);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra("Bin", position);
        startActivity(intent);
        super.onListItemClick(l, v, position, id);
    }

    private void quit(){
        getActivity().finish();
    }
}
