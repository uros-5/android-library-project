package com.example.knjizara.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knjizara.Klijent;
import com.example.knjizara.NotificationHelper;
import com.example.knjizara.R;
import com.example.knjizara.adapter.TopLevelRVAdapter;
import com.example.knjizara.model.Korisnik;
import com.example.knjizara.viewmodel.KnjizaraInfo;
import com.example.knjizara.viewmodel.KorisnikSP;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;

public class Tab1 extends Fragment {
    View rootView;
    KnjizaraInfo knjizaraInfo;

    //klijent
    Klijent klijent;
    //mysql data
    JSONArray niz00 ;
    JSONArray niz11;
    JSONArray hronoloskaLista2 ;

    Gson gson;
    SharedPreferences pref;
    SharedPreferences.Editor prefsEditor;
    public final String CHANNEL_ID = "PlacanjeActivity";
    public int NOTIFICATION_ID = 001;
    NotificationHelper notificationHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_tab1, container, false);
        knjizaraInfo = new KnjizaraInfo(getActivity());

        Klijent klijent = new Klijent();

        niz00 = klijent.sendM("najpKnjige");
        niz11 = klijent.sendM("besplKnjige");
        hronoloskaLista2 = klijent.sendM("hronLista");

        notificationHelper = new NotificationHelper(getContext());


        pref = getActivity().getSharedPreferences("com.example.knjizara", getActivity().MODE_PRIVATE);
        prefsEditor = pref.edit();
        gson = new Gson();
        if(!isInitAppDataDone()) {
            initAppData();
            Log.i("MainActivity ","Setup appdata upravo gotov.");
        }
        else {
            Log.i("MainActivity ","Setup appdata uradjen ranije. ");
        }
        requestNotification();
        try {
            knjizaraInfo.unzipAll();
        } catch (IOException e) {
            e.printStackTrace();
        }


        LinearLayout najpKnjige = rootView.findViewById(R.id.najpopularnijeKnjige);
        Runnable runnablePopularne = new Runnable() {
            @Override
            public void run() {
                initRecyclerView("popularne");
            };
        };
        Thread threadPopularne = new Thread(runnablePopularne);
        threadPopularne.start();

        Runnable runnableBesplatne = new Runnable() {
            @Override
            public void run() {
                initRecyclerView("besplatne");
            };
        };
        Thread threadBesplatne= new Thread(runnableBesplatne);
        threadBesplatne.start();

        Runnable runnableHronoloska = new Runnable() {
            @Override
            public void run() {
                initRecyclerView("hronoloskaLista");
            };
        };
        Thread threadHronoloska = new Thread(runnableHronoloska);
        threadHronoloska.start();


        return rootView;
    }

    public void initRecyclerView (String kat) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        if(kat == "popularne") {
            RecyclerView recyclerView = rootView.findViewById(R.id.najpopularnijeKnjige0);
            recyclerView.setLayoutManager(layoutManager);
            TopLevelRVAdapter adapter = new TopLevelRVAdapter(getActivity(),niz00);
            recyclerView.setAdapter(adapter);
            layoutManager.smoothScrollToPosition(recyclerView,new RecyclerView.State(), recyclerView.getAdapter().getItemCount());
        }
        else if (kat == "besplatne") {

            RecyclerView recyclerView = rootView.findViewById(R.id.besplatneKnjige0);
            recyclerView.setLayoutManager(layoutManager);
            TopLevelRVAdapter adapter = new TopLevelRVAdapter(getActivity(),niz11);
            recyclerView.setAdapter(adapter);
            layoutManager.smoothScrollToPosition(recyclerView,new RecyclerView.State(), recyclerView.getAdapter().getItemCount());
        }
        else if (kat == "hronoloskaLista") {

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
            RecyclerView recyclerView = rootView.findViewById(R.id.hronoloskaLista0);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(gridLayoutManager);
            TopLevelRVAdapter adapter = new TopLevelRVAdapter(getActivity(),hronoloskaLista2);
            adapter.setDuzina(hronoloskaLista2.length());
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);
        }
    }

    public void initAppData() {

        Korisnik korisnik = new Korisnik();
        ArrayList<String> korpa = new ArrayList<String>();

        String jsonKorisnik = gson.toJson(korisnik);
        String jsonKorpa = gson.toJson(korpa);

        prefsEditor.putString("korisnik",jsonKorisnik);
        prefsEditor.putString("korpa",jsonKorpa);
        prefsEditor.putBoolean("korisnikBack",false);
        prefsEditor.putInt("currentTab",0);

        prefsEditor.apply();

    }

    public boolean isInitAppDataDone () {
        if(pref.contains("korisnik") && pref.contains("korpa")) {
            return true;
        }
        return false;
    }

    public void requestNotification() {
        KorisnikSP korisnikSP = new KorisnikSP(getContext());
        if(!korisnikSP.isNull()) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    notificationHelper.createNotification("Prijavite se.","Vise mogucnosti sa jednim klikom!","LoginActivity");
                }
            }, 6000);
        }
        korisnikSP = null;
    }

//    public void exitApp () {
//
//        this.getActivity().finishAffinity();
//        System.exit(0);
//
//    }
}