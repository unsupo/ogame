package objects.messages;

import objects.Coordinates;
import org.jsoup.nodes.Element;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by jarndt on 9/26/16.
 */
public class MessageObj {
    public static final String  ESPIONAGE_ACTION_ON     = "Espionage action on ", //THIS MUST BE CHECKED FOR CONTAINS
                                ESPIONAGE_REPORT_FROM   = "Espionage report from ",//THIS MUST BE CHECKED FOR CONTAINS
                                COMBAT_REPORT_VALUE     = "Combat Report",//THIS MUST BE CHECKED FOR CONTAINS

                                ITEM_EXPIRE             = "An item is about to expire"; //THIS CAN BE EXACT

    public static final String  ESPIONAGE_ACTION = "Enemy Espionaged On You",
                                ESPIONAGE_REPORT = "Your Espionage Report",
                                COMBAT_REPORT    = "Combat Report";

    private String msg_status, msg_title, msgType;

    private Coordinates coordinates;
    private Element msg_head, msg_content;

    private LocalDateTime msgDate;
    private long data_msg_id;

    private IMessage subMessage;

    /**
     * pass in a message element one at a time:
     *      Jsoup.parse(v).select("li.msg");
     * @param e
     */
    public MessageObj(Element e){
//        String str = "23.09.2016 17:30:03";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        data_msg_id = Long.parseLong(e.attr("data-msg-id"));
        msgDate = LocalDateTime.parse(e.select("span.msg_date").text(), formatter);
        msg_title = e.select("span.msg_title").text();
        msgType = msg_title;
        if(msg_title.contains(ESPIONAGE_ACTION_ON))
            msgType = ESPIONAGE_ACTION;
        else if(msg_title.contains(ESPIONAGE_REPORT_FROM)) {
            msgType = ESPIONAGE_REPORT;
            subMessage = new EspionageMsg(msgDate,e);
        }else if(msg_title.contains(COMBAT_REPORT_VALUE)) {
            msgType = COMBAT_REPORT;
            subMessage = new CombatMsg(msgDate,e);
        }
    }

    public String getMsg_status() {
        return msg_status;
    }

    public void setMsg_status(String msg_status) {
        this.msg_status = msg_status;
    }

    public String getMsg_title() {
        return msg_title;
    }

    public void setMsg_title(String msg_title) {
        this.msg_title = msg_title;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Element getMsg_head() {
        return msg_head;
    }

    public void setMsg_head(Element msg_head) {
        this.msg_head = msg_head;
    }

    public Element getMsg_content() {
        return msg_content;
    }

    public void setMsg_content(Element msg_content) {
        this.msg_content = msg_content;
    }

    public LocalDateTime getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(LocalDateTime msgDate) {
        this.msgDate = msgDate;
    }

    public long getData_msg_id() {
        return data_msg_id;
    }

    public void setData_msg_id(long data_msg_id) {
        this.data_msg_id = data_msg_id;
    }

    public IMessage getSubMessage() {
        return subMessage;
    }

    public void setSubMessage(IMessage subMessage) {
        this.subMessage = subMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageObj that = (MessageObj) o;

        if (data_msg_id != that.data_msg_id) return false;
        if (msg_status != null ? !msg_status.equals(that.msg_status) : that.msg_status != null) return false;
        if (msg_title != null ? !msg_title.equals(that.msg_title) : that.msg_title != null) return false;
        if (msgType != null ? !msgType.equals(that.msgType) : that.msgType != null) return false;
        if (coordinates != null ? !coordinates.equals(that.coordinates) : that.coordinates != null) return false;
        if (msg_head != null ? !msg_head.equals(that.msg_head) : that.msg_head != null) return false;
        if (msg_content != null ? !msg_content.equals(that.msg_content) : that.msg_content != null) return false;
        if (msgDate != null ? !msgDate.equals(that.msgDate) : that.msgDate != null) return false;
        return subMessage != null ? subMessage.equals(that.subMessage) : that.subMessage == null;

    }

    @Override
    public int hashCode() {
        int result = msg_status != null ? msg_status.hashCode() : 0;
        result = 31 * result + (msg_title != null ? msg_title.hashCode() : 0);
        result = 31 * result + (msgType != null ? msgType.hashCode() : 0);
        result = 31 * result + (coordinates != null ? coordinates.hashCode() : 0);
        result = 31 * result + (msg_head != null ? msg_head.hashCode() : 0);
        result = 31 * result + (msg_content != null ? msg_content.hashCode() : 0);
        result = 31 * result + (msgDate != null ? msgDate.hashCode() : 0);
        result = 31 * result + (int) (data_msg_id ^ (data_msg_id >>> 32));
        result = 31 * result + (subMessage != null ? subMessage.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MessageObj{" +
                "msg_status='" + msg_status + '\'' +
                ", msg_title='" + msg_title + '\'' +
                ", msgType='" + msgType + '\'' +
                ", coordinates=" + coordinates +
                ", msg_head=" + msg_head +
                ", msg_content=" + msg_content +
                ", msgDate=" + msgDate +
                ", data_msg_id=" + data_msg_id +
                ", subMessage=" + subMessage +
                '}';
    }
}
