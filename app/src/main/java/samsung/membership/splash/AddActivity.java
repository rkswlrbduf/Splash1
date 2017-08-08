package samsung.membership.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by KyuYeol on 2017-07-28.
 */

public class AddActivity extends AppCompatActivity {

    private EditText editTitle;
    private Button confirmButton;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.NoAction);
        setContentView(R.layout.activity_add);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        editTitle = (EditText)findViewById(R.id.title);
        confirmButton = (Button)findViewById(R.id.confirm);

        confirmButton.setVisibility(View.INVISIBLE);

        editTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(editTitle.getText().length() > 0) confirmButton.setVisibility(View.VISIBLE);
                else confirmButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editTitle.getText().length() > 0) confirmButton.setVisibility(View.VISIBLE);
                else confirmButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(editTitle.getText().length() > 0) confirmButton.setVisibility(View.VISIBLE);
                else confirmButton.setVisibility(View.INVISIBLE);
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTitle.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(), "Name을 입력해주세요.", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(AddActivity.this, AddActivity2.class);
                    intent.putExtra("title", editTitle.getText().toString());
                    startActivity(intent);
                    AddActivity.this.finish();
                }
            }
        });

    }
}
