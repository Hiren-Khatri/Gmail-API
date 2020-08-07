package app.khatrisoftwares.gmailapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.raizlabs.android.dbflow.sql.language.Delete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.khatrisoftwares.gmailapi.MainActivity.PREF_ACCOUNT_NAME;
import static app.khatrisoftwares.gmailapi.MainActivity.SCOPES;

public class InboxActivity extends AppCompatActivity {

    RecyclerView listMessages;
    SharedPreferences sharedPref;

    List<Message> messageList;
    MessagesAdapter messagesAdapter;

    GoogleAccountCredential mCredential;
    Gmail mService;
    SwipeRefreshLayout refreshMessages;
    String pageToken = null;
    boolean isFetching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        mService = null;

        sharedPref = InboxActivity.this.getSharedPreferences(getString(R.string.preferences_file_name), Context.MODE_PRIVATE);

        String accountName = sharedPref.getString(PREF_ACCOUNT_NAME, null);

        messageList = new ArrayList<>();

        if (accountName != null) {
            mCredential.setSelectedAccountName(accountName);

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, mCredential)
                    .setApplicationName("MailBox App")
                    .build();

            new GetEMailsTask(true).execute();

        } else {
            startActivity(new Intent(InboxActivity.this, MainActivity.class));
            ActivityCompat.finishAffinity(InboxActivity.this);
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(InboxActivity.this);
        listMessages = findViewById(R.id.listMessages);
        messagesAdapter = new MessagesAdapter(InboxActivity.this, messageList);


        refreshMessages = findViewById(R.id.refreshMessages);
        refreshMessages.setColorSchemeResources(R.color.colorPrimary);
        refreshMessages.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isFetching && isDeviceOnline()) {
                    new GetEMailsTask(false).execute();
                } else
                    Toast.makeText(InboxActivity.this, "Device is offline!", Toast.LENGTH_SHORT).show();
            }
        });

        listMessages.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (!isFetching && isDeviceOnline()) {
                    new GetEMailsTask(false).execute();
                } else
                    Toast.makeText(InboxActivity.this, "Device is offline!", Toast.LENGTH_SHORT).show();
            }
        });

        listMessages.setLayoutManager(mLayoutManager);
        listMessages.setAdapter(messagesAdapter);

    }

    @SuppressLint("StaticFieldLeak")
    private class GetEMailsTask extends AsyncTask<Void, Void, List<Message>> {

        private int itemCount = 0;
        private boolean clear;
        private Exception mLastError = null;

        GetEMailsTask(boolean clear) {
            this.clear = clear;
        }

        @Override
        protected List<Message> doInBackground(Void... voids) {
            isFetching = true;
            List<Message> messageListReceived = null;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        InboxActivity.this.refreshMessages.setRefreshing(true);

                    }
                });

            if (clear) {
                InboxActivity.this.pageToken = null;
            }
            try {

                String user = "me";
                String query = "in:inbox";
                ListMessagesResponse messageResponse = mService.users().messages().list(user).setQ(query).setMaxResults(20L).setPageToken(InboxActivity.this.pageToken).execute();
                InboxActivity.this.pageToken = messageResponse.getNextPageToken();

                messageListReceived = new ArrayList<>();
                List<com.google.api.services.gmail.model.Message> receivedMessages = messageResponse.getMessages();
                for (com.google.api.services.gmail.model.Message message : receivedMessages) {
                    com.google.api.services.gmail.model.Message actualMessage = mService.users().messages().get(user, message.getId()).execute();

                    Map<String, String> headers = new HashMap<>();
                    for (MessagePartHeader messagePartHeader : actualMessage.getPayload().getHeaders())
                        headers.put(
                                messagePartHeader.getName(), messagePartHeader.getValue()
                        );

                    Message newMessage = new Message(
                            actualMessage.getLabelIds(),
                            actualMessage.getSnippet(),
                            actualMessage.getPayload().getMimeType(),
                            headers,
                            actualMessage.getPayload().getParts(),
                            actualMessage.getInternalDate(),
                            actualMessage.getPayload()
                    );

                    messageListReceived.add(newMessage);

                    itemCount++;
                }
            } catch (Exception e) {
//                mLastError = e;
                cancel(true);
                Toast.makeText(InboxActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            return messageListReceived;
        }

        @Override
        protected void onPostExecute(List<Message> output) {
            isFetching = false;

            if (output != null && output.size() != 0) {
                if (clear) {
                    InboxActivity.this.messageList.clear();
                    InboxActivity.this.messageList.addAll(output);
                    InboxActivity.this.messagesAdapter.notifyDataSetChanged();
                } else {
                    int listSize = InboxActivity.this.messageList.size();
                    InboxActivity.this.messageList.addAll(output);
                    InboxActivity.this.messagesAdapter.notifyItemRangeInserted(listSize, itemCount);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        InboxActivity.this.refreshMessages.setRefreshing(false);
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        InboxActivity.this.refreshMessages.setRefreshing(false);
                    }
                });
//                InboxActivity.this.showSnackbar(lytParent, getString(R.string.fetch_failed));
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    public boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        return (networkInfo != null && networkInfo.isConnected());
    }
}