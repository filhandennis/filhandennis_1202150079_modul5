package com.develop.filhan.filhandennis_1202150079_modul5;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddTodoActivity extends AppCompatActivity {

    private EditText mNama, mDesc, mPrior;
    private Button mAdd;

    private TodoHelper todoHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        setTitle("Tambah Todo");

        todoHelper = new TodoHelper(this);

        mAdd=(Button)findViewById(R.id.btnTodoAdd);
        mNama=(EditText)findViewById(R.id.txtTodoName);
        mDesc=(EditText)findViewById(R.id.txtTodoDesc);
        mPrior=(EditText)findViewById(R.id.txtTodoPrior);

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTodoData();
            }
        });
    }


    /*
    * Method yang digunakan untuk menambahkan data pada database SQLite
    */
    public void addTodoData(){
        //Ambil Text
        String nama = mNama.getText().toString();
        String desc = mDesc.getText().toString();
        int prior = Integer.parseInt(mPrior.getText().toString());

        //Query SQL
        String sqlAdd = "INSERT INTO todolist(name, desc, prior) VALUES(?,?,?)";
        SQLiteDatabase db = todoHelper.getWritableDatabase();
        //PreparedStatment: Statment masih berbentuk abstrak (?) sebelum akhirnya di compile
        SQLiteStatement stmt = db.compileStatement(sqlAdd);
        //Nama
        stmt.bindString(1,nama);
        //Decription
        stmt.bindString(2,desc);
        //Priority
        stmt.bindLong(3,prior);

        //Eksekusi Statment
        long rowId = stmt.executeInsert();
        Log.d("SQLITE::DATA","INSERT SUCCESS "+rowId);

        //Pengecekan Ekseskusi, jika berhasil maka mengembalikan id insert, jika tidak -1
        if(rowId!=-1){
            Toast.makeText(this, "Tambah ToDo Berhasil ("+rowId+")", Toast.LENGTH_SHORT).show();
            Intent ini = getIntent();
            //Memberikan nilai tambahan ke intent yang sudah menunggu (MainActivity)
            ini.putExtra("EXTRA_INSERT_RESULT",rowId);
            //Memberikan nilai hasil saat Activity berakhir
            setResult(Activity.RESULT_OK,ini);
            finish();
        }
    }
}
