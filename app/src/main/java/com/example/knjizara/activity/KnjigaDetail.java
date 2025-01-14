package com.example.knjizara.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.knjizara.Klijent;
import com.example.knjizara.R;
import com.example.knjizara.adapter.KomentariAdapter;
import com.example.knjizara.adapter.TopLevelRVAdapter;
import com.example.knjizara.model.Knjiga;
import com.example.knjizara.viewmodel.CurrentTabSP;
import com.example.knjizara.viewmodel.KorisnikSP;
import com.example.knjizara.viewmodel.KorpaSP;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;

public class KnjigaDetail  extends AppCompatActivity {
    private static final String TAG = "KnjigaDetail";

    public static final String EXTRA_ID = "knjigaID";

    public String isbn;

    public String id;
    public Klijent klijent;

    JSONArray knjiga;

    public KorpaSP korpaSP;

    JSONArray niz00;

    CurrentTabSP currentTabSP;

    public KorisnikSP korisnikSP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.knjiga_detail);

        String info = (String)getIntent().getExtras().get(EXTRA_ID);

        this.id = info;

        klijent = new Klijent();

        korpaSP = new KorpaSP(this);

        knjiga = klijent.sendM("id "+this.id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_knjiga);
        setSupportActionBar(toolbar);

        niz00 = klijent.sendM("komentari " + this.id);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        try {

            this.isbn = knjiga.getJSONArray(0).get(5).toString();
            currentTabSP = new CurrentTabSP(this);
            korisnikSP = new KorisnikSP(this);


            String naslov = getSupportActionBar().getTitle().toString();
            getSupportActionBar().setTitle(naslov + " "+knjiga.getJSONArray(0).get(1).toString());

            TextView naslovView = (TextView) findViewById(R.id.naslovKnjige);
            naslovView.setText(knjiga.getJSONArray(0).get(1).toString());

            TextView autorView = (TextView) findViewById(R.id.autorKnjige);
            autorView.setText(knjiga.getJSONArray(0).get(7).toString());

            Button kategorijaView = (Button) findViewById(R.id.kategorijaKnjige);
            kategorijaView.setText(knjiga.getJSONArray(0).get(6).toString());

            TextView cenaView = (TextView) findViewById(R.id.cenaKnjige);
            cenaView.setText("Cena knjige: "+String.valueOf(knjiga.getJSONArray(0).get(3).toString()));


            TextView izdavacView = (TextView) findViewById(R.id.textView4);
            izdavacView.setText("IZDAVAC: "+knjiga.getJSONArray(0).get(8).toString());

            ImageView imageView = (ImageView) findViewById(R.id.imageView);



            String fajl = this.isbn+".jpg";
            setSlika(imageView,fajl);

            initRVKomentari();



        } catch (Exception e) {

            System.out.println(e.getStackTrace());
        }



    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_top_level,menu);
//        return true;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        onBackPressed();
        return true;
    }

    public void setSlika(ImageView slika,String fajl) {
        File fileName = new File(getFilesDir().getAbsolutePath()+"/slike_knjiga/"+fajl);
        Picasso.get().load(fileName).into(slika);
    }

    public void dodajUKorpu(View view) {

        korpaSP.dodaj(id);

    }

    public void vidiKategoriju (View view) {
        Intent intent = new Intent(KnjigaDetail.this,KategorijaActivity.class);
        try {
            intent.putExtra("Kategorija", knjiga.getJSONArray(0).get(6).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSwipeRight(this);
    }
    public void onPause() {
        currentTabSP.setCT(0);
        finish();
        super.onPause();
    }

    public void postComment(View view) {
        TextView textView3 = this.findViewById(R.id.editText3);
        String komentar = textView3.getText().toString();
        String korisnikID = korisnikSP.getKorisnik().id;


        int duzinaKomentara = komentar.length();
        if(duzinaKomentara>0 && duzinaKomentara < 150) {
            String porukaZaServer = String.format("koMentarisi %s %s komentar:%s",id,korisnikID,komentar);
            klijent.sendM(porukaZaServer);
            Toast.makeText(this,"Komentar ceka na odobrenje.",Toast.LENGTH_LONG).show();
            textView3.setText("");
        }
        else if(duzinaKomentara>150) {
            Toast.makeText(this,"Komentar je predugacak.",Toast.LENGTH_LONG).show();
        }
        else if(duzinaKomentara==0) {
            Toast.makeText(this,"Unesite komentar..",Toast.LENGTH_LONG).show();
        }


    }

    public void initRVKomentari() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        RecyclerView recyclerView = this.findViewById(R.id.komentariZaKnjige);
        recyclerView.setLayoutManager(layoutManager);
        KomentariAdapter adapter = new KomentariAdapter(this,niz00);
        recyclerView.setAdapter(adapter);
        layoutManager.smoothScrollToPosition(recyclerView,new RecyclerView.State(), recyclerView.getAdapter().getItemCount());
    }
}
