package com.develop.filhan.filhandennis_1202150079_modul5;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView lblNotExist;
    private RecyclerView recyclerView;

    private TodoHelper todoHelper;
    private ArrayList<TodoModel> todos;

    private int shapeColor;
    private int optionShapeColor;
    private SharedPreferences pref;
    private SharedPreferences.Editor prefEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivityForResult(new Intent(getApplicationContext(), AddTodoActivity.class), 201);
            }
        });

        //Inisialisasi Attribute SharedPreferences dengan Nama 'Pref'
        pref = getApplicationContext().getSharedPreferences("pref",0);
        //Inisialisasi SharedPreferences untuk Membuat SharedPreferences dapat di edit
        prefEdit=pref.edit();
        Log.d("SharedPreferences::DATA","CardView BgColor: "+pref.getString("shapeColorTXT","#FFFFFF"));

        //Elemen yang muncul jika data belum ada / 0
        lblNotExist=(TextView)findViewById(R.id.lblNotExistData);
        //Inisialisasi RecyclerView
        recyclerView=(RecyclerView)findViewById(R.id.recyclerviewTodos);


        //TodoHelper digunakan sebagai Class Database untuk Item ToDos
        todoHelper = new TodoHelper(this);
        //Load untuk Daftar Item Todos
        loadTodoData();
        //Method untuk mengatur tingkah laku dari tiap item recyclerview (cardview)
        settingRecyclerBehavior();
    }

    /*
    * Method yang bertugas sebagai pengecek jika Activity Insert berhasil
    *   digunakan juga sebagai refresh dari list item
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==201){
            if(resultCode==RESULT_OK){
                loadTodoData();
                int newRowId = data.getIntExtra("EXTRA_INSERT_RESULT",-1);
                Log.d("SQLITE::DATA","INSERT SUCCESS "+newRowId);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //startActivityForResult(new Intent(getApplicationContext(), SettingsActivity.class), 001);
            changeBgItemColor();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    * Method yang digunakan untuk memilih warna Background Item CardView
    */
    public void changeBgItemColor(){
        //Inisialisasi Default untuk warna
        shapeColor = R.color.shapeDefault;
        //Data ID RadioButton yang diambil dari SharePreferences
        optionShapeColor = pref.getInt("optionShapeColorSelected",R.id.rShapeColorDefault);
        //mCardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        //Membuat Dialog
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Shape Color"); //Mengatur Judul
        //Mengubah Layout XML menjadi Kodingan
        final View dialogView = getLayoutInflater().inflate(R.layout.fragment_shapecolor,null);
        //Mengambil RadioGroup dari Layout XML
        final RadioGroup rg =(RadioGroup)dialogView.findViewById(R.id.rgShapeColor);
        //Melakukan Check Default
        rg.check(optionShapeColor);
        //Inflat R.layout.fragment_shapecolor
        dialog.setView(dialogView);
        //Perintah jika tombol Done pada dialog ditap
        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Mengambil nilai yang di cek/pilih
                switch(rg.getCheckedRadioButtonId()){
                    //Melakuakan Pengambilan nilai
                    // optionShapeColor untuk ID RadioButton
                    // shapeColor untuk Kode Warna
                    case R.id.rShapeColorDefault: optionShapeColor=R.id.rShapeColorDefault; shapeColor=R.color.shapeDefault; break;
                    case R.id.rShapeColorRed: optionShapeColor=R.id.rShapeColorRed; shapeColor=R.color.shapeRed; break;
                    case R.id.rShapeColorBlue: optionShapeColor=R.id.rShapeColorBlue; shapeColor=R.color.shapeBlue; break;
                    case R.id.rShapeColorGreen: optionShapeColor=R.id.rShapeColorGreen; shapeColor=R.color.shapeGreen; break;
                }
                Log.d("SET::COLOR",""+getResources().getString(shapeColor));
                //Set jika kondisiterpenuhi
                setTodoItemBg();
            }
        });
        //Perintah jika tombol Cancel pada dialog ditap
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    /*
    * Method yang digunakan setelah pemilihan warna selesai
    *   digunakan untuk melakukan penyimpanan nilai ke SharedPreferences > pref
    *   shapeColor = id warna di colors.xml
    *   optionShapeColorSelected = id radiobutton
    */
    public void setTodoItemBg(){
        String color = getResources().getString(shapeColor);
        //mCardView.setCardBackgroundColor(Color.parseColor("#f5f5f5"));
        //Simpan ID RadioButton
        prefEdit.putInt("optionShapeColorSelected",optionShapeColor);
        //Simpan Warna
        prefEdit.putInt("shapeColor",shapeColor);
        prefEdit.putString("shapeColorTXT",color);
        //prefEdit.putString("shapeColorTXT","#FFFFFF");
        prefEdit.commit();

        //SharedPreferences pref = getApplicationContext().getSharedPreferences("pref",0);
        Log.d("SharedPreferences::DATA","CardViewSet BgColor: "+pref.getString("shapeColorTXT","#FFFFFF"));
        //Dilakukan untuk me-refresh list
        loadTodoData();
    }


    /*
    * Method untuk mengambil data dari database
    */
    public void loadTodoData(){
        /*
        * Alurnya data di dalam database SQLite dibaca dari awal hingga akhir (1)
        *   sebelum itu, dilakukan pengecekan isi data, jika kosong di hentikan (2)
        *   setelah data berhasil diambil, ditaruh di recycler (3)
        */
        SQLiteDatabase db = todoHelper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM todolist",null);

        // 2
        if(res.getCount()<1){
            lblNotExist.setVisibility(View.VISIBLE);
            Log.d("SQLITE::DATA","Tidak Ada Data "+res.getCount());
            return;
        }

        //Mulai 1
        res.moveToFirst();

        todos = new ArrayList<>();
        //for(int c=0;c<cursor.getCount();c++){
        while(res.isAfterLast() == false){
            int id = res.getInt(res.getColumnIndex("id"));
            String nama = res.getString(res.getColumnIndex("name"));
            String desc = res.getString(res.getColumnIndex("desc"));
            int prior = res.getInt(res.getColumnIndex("prior"));

            TodoModel todo = new TodoModel(nama,desc,prior);
            todo.setId(id);
            todos.add(todo);

            res.moveToNext();
        }
        //Akhir 1

        lblNotExist.setVisibility(View.GONE);

        //Awal 3
        TodoAdapter todoAdapter = new TodoAdapter(todos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(todoAdapter);
        //Akhir 3
        res.close();
    }

    /*
    * Method yang digunakan untuk mengatur tingkah laku item dalam RecyclerView: Swipe
    */
    public void settingRecyclerBehavior(){
        // Jika Item dalam RecyclerView di Swipe ke Kiri atau ke Kanan maka item tersebut akan terhapus baik dari arraylist maupun database
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                //Toast.makeText(MainActivity.this, ""+position+"Swiped!", Toast.LENGTH_SHORT).show();
                deleteIt(position);
            }
        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    /*
    * MEthod yang digunakan untuk menghapus 1 item dalam recycler view
    */
    public void deleteIt(int pos){
        /*
        * Alur Kerjanya dengan melakukan pengecekan Posisi Item, (1)
        *   kemudian posisi tersebut dijadikan sebuah informasi item untuk diambil id item dalam database (2)
        *   dan dilakukan penghapusan item menggunakan class todohelper (3)
        */
        final TodoModel todo = todos.get(pos); // 1 dan 2
        Log.d("SQLITE::DATA","ID "+(todo.getId()));
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Hapus Item");
        alertDialog.setMessage("Hapus Item '"+todo.getName()+"' ?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //3
                if(todoHelper.deleteTodo(todo.getId())){
                    loadTodoData();
                    Toast.makeText(getApplicationContext(), "Berhasil Hapus "+todo.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                loadTodoData();
            }
        });
        alertDialog.show();
    }
}
