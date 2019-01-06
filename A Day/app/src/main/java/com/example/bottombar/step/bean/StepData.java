package com.example.bottombar.step.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by dylan on 2016/1/30.
 */

@Table("step")
public class StepData {

    // 指定自增，每个对象需要有一个主键
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    @Column("today")
    private String today;
    @Column("step")
    private String step;

    @Column("step0")
    private String step0;
    @Column("step1")
    private String step1;
    @Column("step2")
    private String step2;
    @Column("step3")
    private String step3;
    @Column("step4")
    private String step4;
    @Column("step5")
    private String step5;
    @Column("step6")
    private String step6;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }


    public String getStep0() {
        return step0;
    }

    public String getStep1() {
        return step1;
    }

    public String getStep2() {
        return step2;
    }

    public String getStep3() {
        return step3;
    }

    public String getStep4() {
        return step4;
    }

    public String getStep5() {
        return step5;
    }

    public String getStep6() {
        return step6;
    }

    public String getTimeStep(int i)
    {
        switch (i)
        {
            case 0:
                return step0;
            case 1:
                return step1;
            case 2:
                return step2;
            case 3:
                return step3;
            case 4:
                return step4;
            case 5:
                return step5;
            case 6:
                return step6;
        }
        return null;
    }

    public void setTimeStep(int i,String step)
    {
        switch (i)
        {
            case 0:
                step0=step;
                break;
            case 1:
                step1=step;
                break;
            case 2:
                step2=step;
                break;
            case 3:
                step3=step;
                break;
            case 4:
                step4=step;
                break;
            case 5:
                step5=step;
                break;
            case 6:
                step6=step;
                break;

        }
    }

    @Override
    public String toString() {
        return "StepData{" +
                "id=" + id +
                ", today='" + today + '\'' +
                ", step='" + step + '\'' +
                ", step0='" + step0 + '\'' +
                ", step1='" + step1 + '\'' +
                ", step2='" + step2 + '\'' +
                ", step3='" + step3 + '\'' +
                ", step4='" + step4 + '\'' +
                ", step5='" + step5 + '\'' +
                ", step6='" + step6 + '\'' +
                '}';
    }
}
