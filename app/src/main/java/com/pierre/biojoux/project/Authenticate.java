package com.pierre.biojoux.project;
/*
http://www.tutorialspoint.com/android/android_login_screen.htm
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Authenticate extends AppCompatActivity {

    private EditText editTextPassword;
    private EditText editTextUsername;
    private final SecurityDB login = new SecurityDB(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
    }

    public void quit (View view){
        finish();
    }

    public void validate(View view) {
        if (login.login(editTextUsername.getText().toString(), editTextPassword.getText().toString())){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Invalid authentication", Toast.LENGTH_SHORT).show();
        }
    }
}
