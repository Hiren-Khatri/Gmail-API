package app.khatrisoftwares.gmailapi;

import com.google.api.services.gmail.model.MessagePart;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Message implements Serializable {

    private int id;
    private int message_id;
    private String labelsJson;
    private String snippet;
    private String mimetype;
    private String headersJson;
    private String parentPartJson;
    private String partsJson;
    private String from;
    private String subject;
    private long timestamp;

    private List<String> labels;
    private Map<String, String> headers;

    public Message(List<String> labels, String snippet, String mimetype, Map<String, String> headers, List<MessagePart> parts, long timestamp, MessagePart parentPart) throws JSONException {
        this.labels = labels;
        this.snippet = snippet;
        this.mimetype = mimetype;
        this.headers = headers;
        this.timestamp = timestamp;

        this.from = this.headers.get("From");
        this.subject = this.headers.get("Subject");

        if (this.labels != null) {
            JSONArray labelsArr = new JSONArray();
            for (String label : this.labels)
                labelsArr.put(label);
            this.labelsJson = labelsArr.toString();
        }

        if (this.headers != null) {
            JSONObject headersObj = new JSONObject();
            for (Map.Entry<String, String> header : this.headers.entrySet())
                headersObj.put(header.getKey(), header.getValue());
            this.headersJson = headersObj.toString();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public String getLabelsJson() {
        return labelsJson;
    }

    public void setLabelsJson(String labelsJson) {
        this.labelsJson = labelsJson;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getHeadersJson() {
        return headersJson;
    }

    public void setHeadersJson(String headersJson) {
        this.headersJson = headersJson;
    }

    public String getParentPartJson() {
        return parentPartJson;
    }

    public void setParentPartJson(String parentPartJson) {
        this.parentPartJson = parentPartJson;
    }

    public String getPartsJson() {
        return partsJson;
    }

    public void setPartsJson(String partsJson) {
        this.partsJson = partsJson;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


}
