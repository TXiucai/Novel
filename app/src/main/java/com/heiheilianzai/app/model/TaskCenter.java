package com.heiheilianzai.app.model;

import java.util.Arrays;
import java.util.List;

/**
 * Created by scb on 2018/11/15.
 */

public class TaskCenter {
    private List<TaskCenter2> task_menu;
    private User_info user_info;
    public Sign_info sign_info;
    private Invite_info invite_info;

    public Invite_info getInvite_info() {
        return invite_info;
    }

    public void setInvite_info(Invite_info invite_info) {
        this.invite_info = invite_info;
    }

    public static class Sign_info {
        @Override
        public String toString() {
            return "Sign_info{" +
                    "sign_days=" + sign_days +
                    ", max_award=" + max_award +
                    ", sign_status=" + sign_status +
                    ", unit='" + unit + '\'' +
                    ", sign_rules=" + Arrays.toString(sign_rules) +
                    '}';
        }

        public int getSign_days() {
            return sign_days;
        }

        public void setSign_days(int sign_days) {
            this.sign_days = sign_days;
        }

        public int getMax_award() {
            return max_award;
        }

        public void setMax_award(int max_award) {
            this.max_award = max_award;
        }

        public int getSign_status() {
            return sign_status;
        }

        public void setSign_status(int sign_status) {
            this.sign_status = sign_status;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String[] getSign_rules() {
            return sign_rules;
        }

        public void setSign_rules(String[] sign_rules) {
            this.sign_rules = sign_rules;
        }

        public int sign_days;//连续签到的天数
        public int max_award;//获取的奖励
        public int sign_status;//签到状态0未签到 1 已签到
        public String unit;//
        public String sign_rules[];
    }

    public List<TaskCenter2> getTask_menu() {
        return task_menu;
    }

    public void setTask_menu(List<TaskCenter2> task_menu) {
        this.task_menu = task_menu;
    }

    public User_info getUser_info() {
        return user_info;
    }

    public void setUser_info(User_info user_info) {
        this.user_info = user_info;
    }

    public static class TaskCenter2 {
        private String task_title;
        private List<Taskcenter> task_list;

        public String getTask_title() {
            return task_title;
        }

        public void setTask_title(String task_title) {
            this.task_title = task_title;
        }

        public List<Taskcenter> getTask_list() {
            return task_list;
        }

        public void setTask_list(List<Taskcenter> task_list) {
            this.task_list = task_list;
        }

        public static class Taskcenter {
            private String task_award;
            private String task_desc;
            private String task_id;
            private String task_action;
            private String task_label;
            private int task_state; //完成状态0未完成1已完成

            @Override
            public String toString() {
                return "Taskcenter{" +
                        "task_award='" + task_award + '\'' +
                        ", task_desc='" + task_desc + '\'' +
                        ", task_id='" + task_id + '\'' +
                        ", task_action='" + task_action + '\'' +
                        ", task_label='" + task_label + '\'' +
                        ", task_state=" + task_state +
                        '}';
            }

            public String getTask_action() {
                return task_action;
            }

            public void setTask_action(String task_action) {
                this.task_action = task_action;
            }

            public String getTask_award() {
                return task_award;
            }

            public void setTask_award(String task_award) {
                this.task_award = task_award;
            }

            public String getTask_desc() {
                return task_desc;
            }

            public void setTask_desc(String task_desc) {
                this.task_desc = task_desc;
            }

            public String getTask_id() {
                return task_id;
            }

            public void setTask_id(String task_id) {
                this.task_id = task_id;
            }

            public String getTask_label() {
                return task_label;
            }

            public void setTask_label(String task_label) {
                this.task_label = task_label;
            }

            public int getTask_state() {
                return task_state;
            }

            public void setTask_state(int task_state) {
                this.task_state = task_state;
            }
        }

    }


    public static class User_info {
        private String avatar;// 头像
        private int is_vip;    // 是否是会员 0 不是 1是
        private String nickname;    // 昵称
        private String unit;    // 货币单位
        private int mission_num;
        private int finish_num;
        private int gender;

        public int getMission_num() {
            return mission_num;
        }

        public void setMission_num(int mission_num) {
            this.mission_num = mission_num;
        }

        public int getFinish_num() {
            return finish_num;
        }

        public void setFinish_num(int finish_num) {
            this.finish_num = finish_num;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getIs_vip() {
            return is_vip;
        }

        public void setIs_vip(int is_vip) {
            this.is_vip = is_vip;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }

    public static class Invite_info {
        private String task_title;
        private boolean invite_status;
        private String invite_code;

        public String getTask_title() {
            return task_title;
        }

        public void setTask_title(String task_title) {
            this.task_title = task_title;
        }

        public boolean isInvite_status() {
            return invite_status;
        }

        public void setInvite_status(boolean invite_status) {
            this.invite_status = invite_status;
        }

        public String getInvite_code() {
            return invite_code;
        }

        public void setInvite_code(String invite_code) {
            this.invite_code = invite_code;
        }
    }
}
