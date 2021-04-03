package com.heiheilianzai.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ShareDetailsBean implements Serializable {


    /**
     * title : 有声爽文H漫，连载不断
     * link : http://www.heihei3.app
     * qr_link : /images/qr_img/5c732ecbfbe30724379b6425f04e938b.png
     * desc : 更多小说，更多精彩，尽在嘿嘿连载
     * share_rule_content : 分享规则说明： 1.未登录的用户无邀请码
     * share_code : 85s3e
     * share_success_num : 2
     * share_notice : 分享人数未达上限
     * share_status : true
     */

    @SerializedName("title")
    private String title;
    @SerializedName("link")
    private String link;
    @SerializedName("qr_link")
    private String qrLink;
    @SerializedName("desc")
    private String desc;
    @SerializedName("share_rule_content")
    private String shareRuleContent;
    @SerializedName("share_code")
    private String shareCode;
    @SerializedName("share_success_num")
    private int shareSuccessNum;
    @SerializedName("share_notice")
    private String shareNotice;
    @SerializedName("share_status")
    private Boolean shareStatus;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getQrLink() {
        return qrLink;
    }

    public void setQrLink(String qrLink) {
        this.qrLink = qrLink;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getShareRuleContent() {
        return shareRuleContent;
    }

    public void setShareRuleContent(String shareRuleContent) {
        this.shareRuleContent = shareRuleContent;
    }

    public String getShareCode() {
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }

    public int getShareSuccessNum() {
        return shareSuccessNum;
    }

    public void setShareSuccessNum(int shareSuccessNum) {
        this.shareSuccessNum = shareSuccessNum;
    }

    public String getShareNotice() {
        return shareNotice;
    }

    public void setShareNotice(String shareNotice) {
        this.shareNotice = shareNotice;
    }

    public Boolean isShareStatus() {
        return shareStatus;
    }

    public void setShareStatus(Boolean shareStatus) {
        this.shareStatus = shareStatus;
    }
}