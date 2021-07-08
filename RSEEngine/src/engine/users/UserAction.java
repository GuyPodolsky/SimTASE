package engine.users;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
enum to differ between the three different action types.
The enum will ease the showing of each user action
 */
enum actionType{
    BUY("Buy stocks"),
    SELL("Sell stocks"),
    ADD("Add to aacount balance");

    private String action;

    actionType(String ac){
        this.action = ac;
    }

    public String getAction() {
        return action;
    }
}

public class UserAction {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss:SSS");
    private String action;
    private String date;
    private float transactionAmount;
    private float preBalance;
    private float postBalance;

    public UserAction(String actionType, LocalDateTime actionDate, float amount, float pre, float post){
        this.action = actionType;
        this.date = formatter.format(actionDate);
        this.transactionAmount = amount;
        this.preBalance = pre;
        this.postBalance = post;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = formatter.format(date);
    }

    public float getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(float transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public float getPreBalance() {
        return preBalance;
    }

    public void setPreBalance(float preBalance) {
        this.preBalance = preBalance;
    }

    public float getPostBalance() {
        return postBalance;
    }

    public void setPostBalance(float postBalance) {
        this.postBalance = postBalance;
    }

    public String toJson(){
        String jsonResult = "{";
        jsonResult += ("\"type\":\""+getAction()+"\",");
        jsonResult += ("\"date\":\""+getDate()+"\",");
        jsonResult += ("\"amount\":"+getTransactionAmount()+",");
        jsonResult += ("\"preBalance\":"+getPreBalance()+",");
        jsonResult += ("\"postBalance\":"+getPostBalance()+"}");
        return jsonResult;
    }

    @Override
    public String toString() {
        return "UserAction{" +
                "action=" + action +
                ", date='" + date + '\'' +
                ", transactionAmount=" + transactionAmount +
                ", preBalance=" + preBalance +
                ", postBalance=" + postBalance +
                '}';
    }

}
