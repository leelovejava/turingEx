package com.yami.trading.bean.quant;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.trading.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI量化问卷
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_quant_questionnaire")
public class QuantQuestionnaire extends BaseEntity {

    private String userId;

    private String userCode;

    private String userMail;

    @TableField("question_1_answer")
    private String question1Answer;

    @TableField("question_2_answer")
    private String question2Answer;

    @TableField("question_3_answer")
    private String question3Answer;

    @TableField("question_4_answer")
    private String question4Answer;

    @TableField("question_5_answer")
    private String question5Answer;

    /**
     * 状态：N=待审核, PASS=通过, NOPASS=拒绝
     */
    private String status;

    /**
     * 审核备注
     */
    private String auditRemark;

    @TableField(exist = false)
    private String question1Question;

    @TableField(exist = false)
    private String question2Question;

    @TableField(exist = false)
    private String question3Question;

    @TableField(exist = false)
    private String question4Question;

    @TableField(exist = false)
    private String question5Question;
}
