package dot.dominionofcity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class Register extends AppCompatActivity {
    EditText username, password, password2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (EditText)findViewById(R.id.etUserName);
        password = (EditText)findViewById(R.id.etPassword);
        password2 = (EditText)findViewById(R.id.etPassword2);
    }
    public void OnReg(View view){
        String str_username = username.getText().toString();
        String str_password = password.getText().toString();
        String str_password2 = password2.getText().toString();
        AlertDialog.Builder builder;
        builder= new AlertDialog.Builder(Register.this);
        if(!TextUtils.isEmpty(str_username)&&!TextUtils.isEmpty(str_password)&&!TextUtils.isEmpty(str_password2)) {
            if(str_password.equals(str_password2)) {
                String type = "register";
                BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                backgroundWorker.execute(type, str_username, str_password, str_password2);
            }
            else{
                builder.setTitle("Register Fail");
                builder.setMessage("Two Password not consistent!");
                builder.setNegativeButton("Back",null);
                builder.show();
            }
        }
        else{
            builder.setTitle("Register Fail");
            builder.setMessage("Please fill in all information!");
            builder.setNegativeButton("Back",null);
            builder.show();
        }
    }
}
