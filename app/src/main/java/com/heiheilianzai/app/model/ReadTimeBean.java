package com.heiheilianzai.app.model;

import java.util.List;

public class ReadTimeBean {

    /**
     * user_read_daily : []
     * user_history_award : 0
     * list : {"desc":"1.决定是否就打架大家看法是你们，梵蒂冈\r\n2.决定是否就打架大家看法是你们，梵蒂冈\r\n3.决定是否就打架大家看法是你们，梵蒂冈\r\n4.决定是否就打架大家看法是你们，梵蒂冈\r\n5.决定是否就打架大家看法是你们，梵蒂冈\r\n6.决定是否就打架大家看法是你们，梵蒂冈","award_info":{"task_daily_list":[{"minute":"10","award":"1"},{"minute":"20","award":"2"},{"minute":"30","award":"3"},{"minute":"40","award":"4"},{"minute":"50","award":"5"},{"minute":"60","award":"6"}]}}
     */

    private int user_history_award;
    /**
     * desc : 1.决定是否就打架大家看法是你们，梵蒂冈
     2.决定是否就打架大家看法是你们，梵蒂冈
     3.决定是否就打架大家看法是你们，梵蒂冈
     4.决定是否就打架大家看法是你们，梵蒂冈
     5.决定是否就打架大家看法是你们，梵蒂冈
     6.决定是否就打架大家看法是你们，梵蒂冈
     * award_info : {"task_daily_list":[{"minute":"10","award":"1"},{"minute":"20","award":"2"},{"minute":"30","award":"3"},{"minute":"40","award":"4"},{"minute":"50","award":"5"},{"minute":"60","award":"6"}]}
     */

    private ListBean list;
    private List<?> user_read_daily;

    public int getUser_history_award() {
        return user_history_award;
    }

    public void setUser_history_award(int user_history_award) {
        this.user_history_award = user_history_award;
    }

    public ListBean getList() {
        return list;
    }

    public void setList(ListBean list) {
        this.list = list;
    }

    public List<?> getUser_read_daily() {
        return user_read_daily;
    }

    public void setUser_read_daily(List<?> user_read_daily) {
        this.user_read_daily = user_read_daily;
    }

    public static class ListBean {
        private String desc;
        private AwardInfoBean award_info;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public AwardInfoBean getAward_info() {
            return award_info;
        }

        public void setAward_info(AwardInfoBean award_info) {
            this.award_info = award_info;
        }

        public static class AwardInfoBean {
            /**
             * minute : 10
             * award : 1
             */

            private List<TaskDailyListBean> task_daily_list;

            public List<TaskDailyListBean> getTask_daily_list() {
                return task_daily_list;
            }

            public void setTask_daily_list(List<TaskDailyListBean> task_daily_list) {
                this.task_daily_list = task_daily_list;
            }

            public static class TaskDailyListBean {
                private String minute;
                private String award;
                private int current_task_status;//1为领取  0为未领取

                public int getCurrent_task_status() {
                    return current_task_status;
                }

                public void setCurrent_task_status(int current_task_status) {
                    this.current_task_status = current_task_status;
                }

                public String getMinute() {
                    return minute;
                }

                public void setMinute(String minute) {
                    this.minute = minute;
                }

                public String getAward() {
                    return award;
                }

                public void setAward(String award) {
                    this.award = award;
                }
            }
        }
    }
}
