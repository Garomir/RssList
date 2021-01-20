package com.ramich.rsslist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FeedsActivity extends AppCompatActivity {

    ListView lvFeeds;
    ArrayList<String> titles;
    ArrayList<String> links;
    ArrayList<String> descriptions;

    List<Topic> allTopics;
    Topic someTopic;
    String linkName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);

        titles = new ArrayList<String>();
        links = new ArrayList<String>();
        descriptions = new ArrayList<String>();
        allTopics = new ArrayList<>();
        someTopic = new Topic();

        lvFeeds = findViewById(R.id.lvFeeds);

        Bundle arguments = getIntent().getExtras();
        if(arguments!=null){
            linkName = arguments.get("link_name").toString();
            Toast.makeText(getApplicationContext(), ""+linkName, Toast.LENGTH_SHORT).show();
        }

        new ProcessInBackground().execute(linkName);
        lvFeeds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, ""+allTopics.get(i).getLink());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }

    public InputStream getInputStream (URL url){
        try {
            return url.openConnection().getInputStream();
        }catch (IOException e){
            return null;
        }
    }

    public class ProcessInBackground extends AsyncTask<String, Void, Exception> {

        ProgressDialog progressDialog = new ProgressDialog(FeedsActivity.this);
        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Loading RSS feed...please wait!");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(url), "UTF-8");
                boolean insideItem = false;
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT){
                    if (eventType == XmlPullParser.START_TAG){
                        if (xpp.getName().equalsIgnoreCase("item")){
                            insideItem = true;
                        }
                        else if (xpp.getName().equalsIgnoreCase("title")){
                            if (insideItem){
                                titles.add(xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("description")){
                            if (insideItem){
                                descriptions.add(xpp.nextText());
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("link")){
                            if (insideItem){
                                links.add(xpp.nextText());
                            }
                        }
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                        insideItem = false;
                    }
                    eventType = xpp.next();
                }
            }catch (MalformedURLException e){
                exception = e;
            }catch (XmlPullParserException e){
                exception = e;
            }catch (IOException e){
                exception = e;
            }

            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);

            for (int i=0; i<titles.size(); i++){
                allTopics.add(new Topic(titles.get(i), descriptions.get(i), links.get(i)));
            }
            lvFeeds.setAdapter(new CustomAdapter(getApplicationContext(), allTopics));

            progressDialog.dismiss();
        }
    }
}
