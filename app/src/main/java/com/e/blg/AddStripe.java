package com.e.blg;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class AddStripe extends Dashboard { // I extended the class Dashboard to be able to access to MyBTclass object created there.

    private static String field_name = "Saved stripes:";
    private String stripes = "";
    ArrayAdapter stripesArray = null;

    // GUI objects.
    com.google.android.material.textfield.TextInputEditText stripeName;
    Button save, clear;
    ListView stripesList;
    NumberPicker numberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stripe);

        // GUI definitions.
        stripeName = findViewById(R.id.name);
        save = findViewById(R.id.save);
        clear = findViewById(R.id.clear_button);
        stripesList = findViewById(R.id.stripes_list);
        numberPicker = findViewById(R.id.number_picker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(100);

        stripeName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        // Initialize Array adapter that requires a .xml file (new "Layout resource file" in the "layout" directory) with a single TextView.
        stripesArray = new ArrayAdapter(AddStripe.this, R.layout.stripes_tv);

        // Initialize listView.
        stripesList.setAdapter(stripesArray);

        // Read the preferences of the specified field name.
        SharedPreferences preferencias = getSharedPreferences(field_name, Context.MODE_PRIVATE);
        for (String string: preferencias.getString(field_name, "No stripes found").split(";")) stripesArray.add(string);

        final Animation save_springAnimation = AnimationUtils.loadAnimation(this, R.anim.spring_animation);
        common.MyBounceInterpolator interpolator = new common.MyBounceInterpolator(0.2, 20);
        save_springAnimation.setInterpolator(interpolator);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save.startAnimation(save_springAnimation);

                String newStripe = "Name: " + stripeName.getText() + " | Nº: " + numberPicker.getValue();
                SharedPreferences preferencias = getSharedPreferences(field_name, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferencias.edit();
                editor.putString(field_name, preferencias.getString(field_name, "") + newStripe + ";");
                editor.commit();
                stripesArray.add(newStripe);
                Toast.makeText(getApplicationContext(), "Finished...", Toast.LENGTH_SHORT).show();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferencias = getSharedPreferences(field_name, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferencias.edit();
                editor.putString(field_name, "");
                editor.commit();
                stripesArray.clear();
            }
        });
    }

    // Back button callback.
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(AddStripe.this, Dashboard.class);
        startActivity(intent);
        finish();
    }
}
