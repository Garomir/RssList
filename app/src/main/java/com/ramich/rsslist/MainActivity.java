package com.ramich.rsslist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ListView lvRss;
    Db db;
    SimpleCursorAdapter sca;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvRss = findViewById(R.id.lvRss);
        registerForContextMenu(lvRss);

        db = new Db(this);

        lvRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor curs = db.getOneData(l);
                curs.moveToFirst();
                String oneLink = curs.getString(curs.getColumnIndexOrThrow(Db.KEY_LINK));
                curs.close();
                Intent intent = new Intent(getApplicationContext(), FeedsActivity.class);
                intent.putExtra("link_name", oneLink);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        db.open();
        fillListView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_item:
                showDialog();
                fillListView();
                break;
            case R.id.settings_item:
                Toast.makeText(this, "Here will be settings!", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.delete_item:
                db.delRec(acmi.id);
                fillListView();
                break;
        }
        return true;
    }

    public void fillListView(){
        cursor = db.getAllData();
        if (cursor.getCount() == 0){
            Toast.makeText(this, "No data to show!", Toast.LENGTH_SHORT).show();
        }
        String[] from = new String[]{Db.KEY_NAME};
        int[] to = new int[]{R.id.tvListItem};
        sca = new SimpleCursorAdapter(this, R.layout.list_item, cursor, from,to,0);
        lvRss.setAdapter(sca);
    }

    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final EditText etName = (EditText) dialogView.findViewById(R.id.etName);
        final EditText etLink = (EditText) dialogView.findViewById(R.id.etLink);

        builder
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Можно передавать строку в активити по нажатию
                        String someName = etName.getText().toString();
                        String someLink = etLink.getText().toString();
                        if (someName.length() == 0 || someLink.length() == 0){
                            Toast.makeText(getApplicationContext(), "Enter the fields!", Toast.LENGTH_SHORT).show();
                        } else {
                            if (db.addRec(someName, someLink)){
                                fillListView();
                                Toast.makeText(getApplicationContext(), "Link is added!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
