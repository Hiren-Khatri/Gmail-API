package app.khatrisoftwares.gmailapi;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private Context mContext;
//    private Utils mUtils;
    private List<Message> messageList;
//    private List<Message> messageListFiltered;

//    private ViewGroup parent;

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        LinearLayoutCompat lytItemParent;
        ConstraintLayout lytFromPreviewParent;
        AppCompatTextView txtFromPreview, txtFrom, txtDate, txtSubject, txtSnippet;

        public MessageViewHolder(View itemView) {
            super(itemView);

            lytItemParent = itemView.findViewById(R.id.lytItemParent);
            lytFromPreviewParent = itemView.findViewById(R.id.lytFromPreviewParent);
            txtFromPreview = itemView.findViewById(R.id.txtFromPreview);
            txtFrom = itemView.findViewById(R.id.txtFrom);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtSubject = itemView.findViewById(R.id.txtSubject);
            txtSnippet = itemView.findViewById(R.id.txtSnippet);
        }
    }

    public MessagesAdapter(Context context, List<Message> messageList) {
        this.mContext = context;
//        this.mUtils = new Utils(context);
        this.messageList = messageList;
//        this.messageListFiltered = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
           View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);

        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
         final Message message = this.messageList.get(position);

        holder.txtFromPreview.setText(message.getFrom().substring(0, 1).toUpperCase(Locale.ENGLISH));
        holder.txtFrom.setText(message.getFrom());
        holder.txtDate.setText(timestampToDate(message.getTimestamp()));
        holder.txtSubject.setText(message.getSubject());
        holder.txtSnippet.setText(message.getSnippet());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,EmailActivity.class);
                intent.putExtra("message",message);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.messageList.size();
    }

    String timestampToDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("dd/MM/yyyy", cal).toString();
    }
    }
