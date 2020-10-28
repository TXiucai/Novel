package com.heiheilianzai.app.model.event;

/**
 * 跳转到有声事件  (banner公告  有声阅读历史或之后其他地方)
 * banner公告 跳转设置  content
 * 有声阅读历史  跳转设置 ncid nid acnme current_time
 */
public class SkipToBoYinEvent {
    String content; //url
    PhonicSkipInfo phonicSkipInfo;//跳转有声播放

    public SkipToBoYinEvent(String content) {
        this.content = content;
    }

    public SkipToBoYinEvent(PhonicSkipInfo phonicSkipInfo) {
        this.phonicSkipInfo = phonicSkipInfo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PhonicSkipInfo getPhonicSkipInfo() {
        return phonicSkipInfo;
    }

    public void setPhonicSkipInfo(PhonicSkipInfo phonicSkipInfo) {
        this.phonicSkipInfo = phonicSkipInfo;
    }

    public static class PhonicSkipInfo {
        String ncid;//当前章节id
        String nid;//小说id
        String acnme;//小说标题
        String current_time;//当前章节播放时长

        public PhonicSkipInfo(String ncid, String nid, String acnme, String current_time) {
            this.ncid = ncid;
            this.nid = nid;
            this.acnme = acnme;
            this.current_time = current_time;
        }

        public String getNcid() {
            return ncid;
        }

        public void setNcid(String ncid) {
            this.ncid = ncid;
        }

        public String getNid() {
            return nid;
        }

        public void setNid(String nid) {
            this.nid = nid;
        }

        public String getAcnme() {
            return acnme;
        }

        public void setAcnme(String acnme) {
            this.acnme = acnme;
        }

        public String getCurrent_time() {
            return current_time;
        }

        public void setCurrent_time(String current_time) {
            this.current_time = current_time;
        }
    }
}