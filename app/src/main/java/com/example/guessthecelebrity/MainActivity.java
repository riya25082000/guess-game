package com.example.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celebURLs=new ArrayList<String>();
    ArrayList<String> celebNames=new ArrayList<String>();
    int chosenCeleb=0;
    ImageView imageView;

    Button button0;
    Button button1;
    Button button2;
    Button button3;

    int locationOfCorrectAnswer=0;
    String[] answers=new String[4];

    public void celebChosen(View view){

        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){

            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();
        }
        else{

            Toast.makeText(getApplicationContext(), "Inorrect! It was " + celebNames.get(chosenCeleb), Toast.LENGTH_LONG).show();
        }
        createNewQuestion();
    }
    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{


        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url=new URL(urls[0]);
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream=connection.getInputStream();
                Bitmap myBitmap= BitmapFactory.decodeStream(inputStream);
                return  myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }
    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection connection=null;
            String result="";
            try{
             url=new URL(urls[0]);
             connection=(HttpURLConnection) url.openConnection();
                InputStream inputStream=connection.getInputStream();
                InputStreamReader reader=new InputStreamReader(inputStream);
                int data=reader.read();
                while(data != -1)
                {
                    char current = (char) data;
                    result+=current;
                    data = reader.read();
                }
                return  result;
            }
            catch (Exception e){

                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=(ImageView)findViewById(R.id.imageView);
        button0=(Button)findViewById(R.id.button0);
        button1=(Button)findViewById(R.id.button1);
        button2=(Button)findViewById(R.id.button2);
        button3=(Button)findViewById(R.id.button3);
        DownloadTask task= new DownloadTask();
        String result=null;

        try {
            result=task.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult=result.split("<div class=\"sidebarContainer\">");
            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);
            while(m.find()){

               celebURLs.add(m.group(1));
            }
           p=Pattern.compile("alt=\"(.*?)\"/");
            m=p.matcher(splitResult[0]);
            while(m.find())
            {
                celebNames.add(m.group(1));
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        createNewQuestion();
    }
    public void createNewQuestion(){

        Random random=new Random();
        chosenCeleb=random.nextInt(celebURLs.size());
        ImageDownloader imageTask=new ImageDownloader();
        Bitmap celebImage = null;
        try {
            celebImage=imageTask.execute(celebURLs.get(chosenCeleb)).get();
            imageView.setImageBitmap(celebImage);
            locationOfCorrectAnswer=random.nextInt(4);
            int incorrectAnswerLocation;
            for(int i=0;i<4;i++){
                if(i==locationOfCorrectAnswer) {
                    answers[i] = celebNames.get(chosenCeleb);
                }
                else{

                    incorrectAnswerLocation=random.nextInt(celebURLs.size());
                    while(incorrectAnswerLocation==chosenCeleb){
                        incorrectAnswerLocation=random.nextInt(celebURLs.size());
                    }
                    answers[i]=celebNames.get(incorrectAnswerLocation);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
