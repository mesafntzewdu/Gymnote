package com.mesi.gymusers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class home extends Fragment {
    private homeAdapter adapter;
    private DbHelper db;
    private ListView lv;
    EditText searchText;

    public home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_home, container, false);
        lv = v.findViewById(R.id.home_list);

        listUsersLv(v);
        return v;
    }

    private void listUsersLv(View v) {


        db = new DbHelper(getContext());

        adapter = new homeAdapter(getContext(),  db.getAllUsers());
        lv.setAdapter(adapter);

        //List view click listener for more detail
        lv.setOnItemClickListener((adapterV, view, i, l) -> {

            db.getSingleUser(String.valueOf(adapterV.getItemIdAtPosition(i)));
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_frame, new home_detail()).commit();
        });

        searchListener(v);

    }

    private void searchListener(View v) {
        searchText = v.findViewById(R.id.search_text);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                adFilter(s.toString());

            }
        });
    }

    private void adFilter(String val) {
        DbHelper db = new DbHelper(getContext());
        //All users are listed in
        ArrayList<UserDAO> users =db.getAllUsers();

        LinkedList<UserDAO> filterUser = new LinkedList<>();

        for (int i = 0; i < users.size(); i++) {

            if ( users.get(i).getFname().toLowerCase().contains(val.toLowerCase()))
            {
                filterUser.add(users.get(i));
            }

        }

        homeAdapter adapter = new homeAdapter(getContext(), filterUser);
        lv.setAdapter(adapter);
        lv.deferNotifyDataSetChanged();
    }

}