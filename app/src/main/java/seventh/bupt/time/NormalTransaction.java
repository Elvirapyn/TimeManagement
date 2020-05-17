package seventh.bupt.time;

public class NormalTransaction {

    public String transactionDate_;
    public String startTime_;
    public String endTime_;
    public String isNotify_;
    public String description_;

    public NormalTransaction(String date, String isNotify, String description, String startTime, String endTime){
        this.transactionDate_=date;
        this.isNotify_ = isNotify;
        this.description_ = description;
        this.startTime_ =startTime;
        this.endTime_ =endTime;
    }
    @Override
    public String toString() {
        String result = "";
        result += "事务描述：" + this.description_ + "     ";
        result += "事务日期" + this.transactionDate_ + "     ";
        result += "是否提醒：" + this.isNotify_ + "     ";
        result += "开始时间：" + this.startTime_ + "     ";
        result += "结束时间：" + this.endTime_;

        return result;
    }
}
