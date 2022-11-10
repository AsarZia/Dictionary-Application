package com.example.dictionary;

import static android.os.Build.VERSION_CODES.M;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextView word, meaning, example, origin;
    ImageButton button;
    EditText editText;
    ProgressDialog progressDialog;
    String audioUri;
    ImageView speaker, meaning_speak;
    TextToSpeech T1;
    StringBuilder stringBuilder;
    String exam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.edit_text);
        button = findViewById(R.id.button);

        stringBuilder=new StringBuilder();
        //progressBar = findViewById(R.id.pr)
        word = findViewById(R.id.word_id);
        meaning = findViewById(R.id.meaning_id);
        example = findViewById(R.id.example_id);
        origin = findViewById(R.id.origin_id);
        speaker = findViewById(R.id.imageView);
        meaning_speak = findViewById(R.id.speak);
        //origin.setSelected(true);
        getMeaning("Hello");
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Loading....");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 2100);


        T1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.SUCCESS) {
                    T1.setLanguage(Locale.ENGLISH);
                }
            }
        });
        meaning_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = meaning.getText().toString();
                T1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String searchword = editText.getText().toString().trim();

                getMeaning(searchword);

                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("Fetching Word....");
                progressDialog.setMessage("Please Wait");
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 2000);


            }


        });

        speaker.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = word.getText().toString();
                T1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });


    }

    private void getMeaning(String Main) {
        String URL = "https://api.dictionaryapi.dev/api/v2/entries/en/"+Main;



        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    for (int i=0;i<response.length();i++){
                        try {
                            JSONObject jsonObject =response.getJSONObject(i);
                            String word_x = jsonObject.getString("word");
                            word.setText(word_x);

                            JSONArray meaningArray =jsonObject.getJSONArray("meanings");
                            for(int j= 1; j<meaningArray.length();j++){
                                JSONObject  meaningObject = meaningArray.getJSONObject(j);
                                JSONArray definiArray = meaningObject.getJSONArray("definitions");

                                for(int k=0; k < definiArray.length();k++) {
                                    JSONObject definiObject = definiArray.getJSONObject(k);//String defini = definiArray.getString(k);
                                    String MEAN = definiObject.getString("definition");
                                    meaning.setText(MEAN);

                                    exam = definiObject.getString("example");
                                    stringBuilder.append(exam);
                                    example.setText(stringBuilder.toString());
                                    example.setText(exam);


                                   // JSONArray synonymsArray = definiObject.getJSONArray("synonyms");
//                                    for (int h = 0; h < definiArray.length(); h++) {
//                                        JSONObject jsonObject4 = definiArray.getJSONObject(h);
//                                        // example.append(",");
//                                    }
                                }
                                }
                          //  }

                        } catch (Exception e) {

                          //  Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                              //  Toast.makeText(MainActivity.this, "EXample Data Not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(MainActivity.this, "Cannot Fetch null Data", Toast.LENGTH_SHORT).show();

                }
            });

            MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
        }
}

