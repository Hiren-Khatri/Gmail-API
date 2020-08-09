package app.android.gmailapi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import app.android.gmailapi.models.Message;
import app.android.gmailapi.R;

public class EmailActivity extends AppCompatActivity {

    Message message;
    TextView tvFirstChar,tvFromUser,tvDate,tvBodyEmail,tvSubject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        message = (Message) getIntent().getSerializableExtra("message");

        tvFirstChar = findViewById(R.id.tvFirstChar);
        tvFromUser = findViewById(R.id.tvFromUser);
        tvDate = findViewById(R.id.tvDate);
        tvSubject = findViewById(R.id.tvSubject);
        tvBodyEmail = findViewById(R.id.tvBodyEmail);

        tvFirstChar.setText(message.getFrom().substring(0, 1).toUpperCase(Locale.ENGLISH));
        tvFromUser.setText(message.getFrom());
        tvDate.setText(timestampToDate(message.getTimestamp()));
        tvSubject.setText(message.getSubject());
        tvBodyEmail.setText(message.getSnippet());

    }

    String timestampToDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("dd/MM/yyyy", cal).toString();
    }

}