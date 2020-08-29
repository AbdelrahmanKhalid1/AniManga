package com.example.animanga;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.animanga.data.FirebaseField;
import com.example.animanga.data.Item;
public class ItemActivity extends AppCompatActivity {

    Item item;
    EditText editName;
    EditText editLink;
    EditText editDesc;
    ImageView imageView;
    TextView textCat;
    TextView textStatus;
    Spinner spinnerCat;
    Spinner spinnerStatus;
    Button btnSave;
    Menu menu;
    boolean isInEdit = false;

    private static final String TAG = "ItemActivity";
    // fy 7agat mmkn a5liha comboBox w l a7sn nha bt'a comboBox
    // mmkn a3ml zorar edit w dh lma ados 3lih hy3ml enable le kol l editTexts w y3ml edit br7to aw y3ml cancel
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        item = getIntent().getParcelableExtra("item");
        editName = findViewById(R.id.edit_name);
        editLink = findViewById(R.id.edit_link);
        editDesc = findViewById(R.id.edit_desc);

        imageView = findViewById(R.id.add_poster);

        textCat = findViewById(R.id.text_cat);
        textStatus = findViewById(R.id.text_status);

        spinnerCat = findViewById(R.id.category_spinner);
        spinnerStatus = findViewById(R.id.status_spinner);
        btnSave = findViewById(R.id.btn_save);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onBackPressed() {
        if(isInEdit) {
            isInEdit = false;
            onOptionsItemSelected(menu.findItem(R.id.edit_cancel));
            return;
        }
        super.onBackPressed();

    }

    @Override
    protected void onStart() {
        super.onStart();

        final ArrayAdapter<CharSequence> catAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_cat, R.layout.spinner_item);
        catAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerCat.setAdapter(catAdapter);
        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = parent.getItemAtPosition(position).toString();
                textCat.setText(str);
                Log.e(TAG, "onItemSelected: " + str );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerCat.setSelection(isTrue(item.getCategory()));

        final ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this, R.array.spinner_status, R.layout.spinner_item);
        statusAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerStatus.setAdapter(statusAdapter);
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = parent.getItemAtPosition(position).toString();
                textStatus.setText(str);
                Log.e(TAG, "onItemSelected: "+ str );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerStatus.setSelection(isTrue(item.getStatus()));editName.setText(item.getName());

        editLink.setText(item.getLink());
        editDesc.setText(item.getDesc());
        textCat.setText(FirebaseField.isAnime(item.getCategory()));
        textStatus.setText(item.isDoneToText(item.getStatus()));

        Glide.with(this)
                .load(item.getPic())
                .placeholder(R.drawable.ic_android_black_24dp)
                .into(imageView);
        imageView.setBackgroundColor(Color.TRANSPARENT);
    }

    private int isTrue(boolean x){
        if(x){
            return 0;
        }
        return 1;
    }

    public void onSaveClick(View view){
        item.setName(editName.getText().toString());
        item.setLink(editLink.getText().toString());
        item.setDesc(editDesc.getText().toString());
        item.setCategory(textCat.getText().toString());
        item.setStatusByString(textStatus.getText().toString());
        FirebaseField.updateItem(FirebaseField.isAnime(item.getCategory()),item);
        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
        onOptionsItemSelected(menu.findItem(R.id.edit_cancel));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        MenuItem menuItem;
        switch (item.getItemId()) {
            case R.id.edit_choice:
                menuItem = menu.findItem(R.id.edit_cancel);
                setEditEnable();
                break;
            case R.id.edit_cancel:
                menuItem = menu.findItem(R.id.edit_choice);
                setEditDisable();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        menuItem.setVisible(true);
        item.setVisible(false);
        return true;
    }

    private void setEditEnable() {
        isInEdit = true;
        editName.setEnabled(true);
        editDesc.setEnabled(true);
        editLink.setEnabled(true);
        textCat.setVisibility(View.INVISIBLE);
        textStatus.setVisibility(View.INVISIBLE);
        spinnerCat.setVisibility(View.VISIBLE);
        spinnerStatus.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.VISIBLE);
    }

    private void setEditDisable() {
        isInEdit = false;
        editName.setEnabled(false);
        editDesc.setEnabled(false);
        editLink.setEnabled(false);
        textCat.setVisibility(View.VISIBLE);
        textStatus.setVisibility(View.VISIBLE);
        spinnerCat.setVisibility(View.INVISIBLE);
        spinnerStatus.setVisibility(View.INVISIBLE);
        btnSave.setVisibility(View.INVISIBLE);
    }

}
