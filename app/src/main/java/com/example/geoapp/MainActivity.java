package com.example.geoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.geoapp.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnRegister;

    FirebaseAuth auth;// для авторизации
    FirebaseDatabase db;//для подключения к базе данных
    DatabaseReference users;//для работы с табличками внутри базы данных

    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = findViewById(R.id.btnSignIn);
        btnRegister = findViewById(R.id.btnRegister);

        root = findViewById(R.id.root_element);

        auth = FirebaseAuth.getInstance();//запускаем авторизацию в базе данных
        db = FirebaseDatabase.getInstance();//подключаемся к базе данных
        users = db.getReference("Users");//указываем с какой табличкой будем работать

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterWindow();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignInWindow();
            }
        });
    }

    private void showSignInWindow(){

        //отображаем реализацию всплывающего окна
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);//внутри этого же класса
        // будет отображаться это всплывающее окно
        dialog.setTitle("Войти");//устанавливаем заголовок для окна
        dialog.setMessage("Введите все данные для входа");//подпись под заголовком

        // получаем шаблон всплывающего окна
        LayoutInflater inflater = LayoutInflater.from(this);
        View sign_in_window = inflater.inflate(R.layout. sign_in_window,null);
        //и устанавливаем этот шаблон,как шаблон для всплывающего окна(dialog)
        dialog.setView( sign_in_window);

        //получаем все данные из текстоывых полей шаблона всплывающего окна
        final MaterialEditText email =  sign_in_window.findViewById(R.id.emailField);
        final MaterialEditText pass =  sign_in_window.findViewById(R.id.passField);

        //создаем две кнопки - отмена всплы-го окна и регистрация в базе данных
        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.setPositiveButton("Войти", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (TextUtils.isEmpty(email.getText().toString())) {//если пользователь не введет почту
                    //Snackbar позволяет выводить ошибки в всплывающих окнах
                    Snackbar.make(root, "Введите вашу почту", Snackbar.LENGTH_SHORT).show();
                    return;//чтобы выходили из этой фу-и и дальше код не обрабатывался если ошибка
                }
                if (pass.getText().toString().length() < 5)
                    Snackbar.make(root, "Введите ваш пароль, который более 5 символов", Snackbar.LENGTH_SHORT).show();
                return;
            }

        });

        auth.signInWithEmailAndPassword(email.getText().toString(),pass.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(MainActivity.this, MapActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(root,"Ошибка авторизации ." + e.getMessage(),Snackbar.LENGTH_SHORT).show();
            }
        });

        dialog.show();

    }
    private void showRegisterWindow() {
        //отображаем реализацию всплывающего окна
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);//внутри этого же класса
        // будет отображаться это всплывающее окно
        dialog.setTitle("Зарегистрироваться");//устанавливаем заголовок для окна
        dialog.setMessage("Введите все данные для регистрации");//подпись под заголовком

        // получаем шаблон всплывающего окна
        LayoutInflater inflater = LayoutInflater.from(this);
        View register_window = inflater.inflate(R.layout.register_window,null);
        //и устанавливаем этот шаблон,как шаблон для всплывающего окна(dialog)
        dialog.setView(register_window);

        //получаем все данные из текстоывых полей шаблона всплывающего окна
        final MaterialEditText email = register_window.findViewById(R.id.emailField);
        final MaterialEditText pass = register_window.findViewById(R.id.passField);
        final MaterialEditText name = register_window.findViewById(R.id.nameField);
        final MaterialEditText phone = register_window.findViewById(R.id.phoneField);

        //создаем две кнопки - отмена всплы-го окна и регистрация в базе данных
        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (TextUtils.isEmpty(email.getText().toString())){//если пользователь не введет почту
                    //Snackbar позволяет выводить ошибки в всплывающих окнах
                    Snackbar.make(root,"Введите вашу почту",Snackbar.LENGTH_SHORT).show();
                    return;//чтобы выходили из этой фу-и и дальше код не обрабатывался если ошибка
                }
                if (TextUtils.isEmpty(name.getText().toString())){
                    Snackbar.make(root,"Введите ваше имя",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(phone.getText().toString())){
                    Snackbar.make(root,"Введите ваш телефон",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (pass.getText().toString().length() < 5)
                    Snackbar.make(root,"Введите ваш пароль, который более 5 символов",Snackbar.LENGTH_SHORT).show();
                return;
            }
        });
        //Если не было ошибок, то дальше регистрация пользователя
        auth.createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override//обработчик событий вызовет onSuccess в том случае если поль-ль будет добавлен в базу данных
                    public void onSuccess(AuthResult authResult) {
                        User user = new User();
                        user.setEmail(email.getText().toString());
                        user.setEmail(name.getText().toString());
                        user.setEmail(phone.getText().toString());
                        user.setEmail(pass.getText().toString());

                        //передаем этот обЪект с установл-ми значе-ми в базу данных
                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())//по определенному id
                                .setValue(user)//и добавляем в таблицу users

                                //обработчик событий который сработает, когда поль=ль успешно добавится
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Snackbar.make(root,"Пользователь добавлен", Snackbar.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(root,"Ошибка регистрации. " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                });
        dialog.show();
    }
}
