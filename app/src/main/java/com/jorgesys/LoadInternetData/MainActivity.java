package com.jorgesys.LoadInternetData;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

//https://trantor.is/search/?q=el+principito

public class MainActivity extends AppCompatActivity {
        Button buscar;
        EditText textoBusqueda;

        private RecyclerView recyclerView;
        private ParseAdapter adapter;
        private ArrayList<ParseItem> parseItems = new ArrayList<>();
        private ProgressBar progressBar;

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        }

        @Override
        protected void onResume() {
            super.onResume();

            progressBar = findViewById(R.id.progressBar);
            recyclerView = findViewById(R.id.recyclerView);

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ParseAdapter(parseItems, this);
            recyclerView.setAdapter(adapter);



            textoBusqueda = (EditText)findViewById(R.id.etBuscar);

            String texto = textoBusqueda.getText().toString();
            buscar = (Button)findViewById(R.id.btnBusqueda);

            buscar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextUtils.isEmpty(textoBusqueda.getText())){
                        Toast.makeText(MainActivity.this, "Debe ingresar el nombre del libro.", Toast.LENGTH_LONG).show();
                        textoBusqueda.setFocusable(true);
                    }else{
                        Content content = new Content();
                        content.execute();
                    }
                }
            });
        }

        private class Content extends AsyncTask<Void,Void,Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
                progressBar.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressBar.setVisibility(View.GONE);
                progressBar.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_out));
                adapter.notifyDataSetChanged();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                String url = "https://trantor.is/search/?q="+ textoBusqueda.getText().toString();
                try {
                    Document doc = Jsoup.connect(url).get();

                    Elements data = doc.select("div.span1");
                    int size = data.size();
                    Log.d("doc", "doc: "+doc);
                    Log.d("data", "data: "+data);
                    Log.d("size", ""+size);
                    for (int i = 0; i < size; i++) {
                        String imgUrl = data.select("div.span1")
                                .select("img")
                                .eq(i)
                                .attr("src");

                        String title = data.select("div.span7")
                                .select("span")
                                .eq(i)
                                .text();

                        String detailUrl = data.select("div.span7")
                                .select("a")
                                .eq(i)
                                .attr("href");

                        parseItems.add(new ParseItem(imgUrl, title, detailUrl));
                        Log.d("items", "img: " + imgUrl + " . title: " + title);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
    }