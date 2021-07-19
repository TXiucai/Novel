package com.heiheilianzai.app.model;

public class AddressBean {

    /**
     * id : 1
     * uid : 465964
     * province : 江南省-511
     * city : 东方市
     * town : 512区
     * address_details : 和平大道特一号112903
     * receiver_name : 张三丰
     * receiver_mobile : 13000000000
     * created_at : 1626318949
     * updated_at : 1626320361
     */

    private UserAddressBean user_address;

    public UserAddressBean getUser_address() {
        return user_address;
    }

    public void setUser_address(UserAddressBean user_address) {
        this.user_address = user_address;
    }

    public static class UserAddressBean {
        private int id;
        private int uid;
        private String province;
        private String city;
        private String town;
        private String address_details;
        private String receiver_name;
        private String receiver_mobile;
        private int created_at;
        private int updated_at;
        private String post_code;

        public String getPost_code() {
            return post_code;
        }

        public void setPost_code(String post_code) {
            this.post_code = post_code;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getTown() {
            return town;
        }

        public void setTown(String town) {
            this.town = town;
        }

        public String getAddress_details() {
            return address_details;
        }

        public void setAddress_details(String address_details) {
            this.address_details = address_details;
        }

        public String getReceiver_name() {
            return receiver_name;
        }

        public void setReceiver_name(String receiver_name) {
            this.receiver_name = receiver_name;
        }

        public String getReceiver_mobile() {
            return receiver_mobile;
        }

        public void setReceiver_mobile(String receiver_mobile) {
            this.receiver_mobile = receiver_mobile;
        }

        public int getCreated_at() {
            return created_at;
        }

        public void setCreated_at(int created_at) {
            this.created_at = created_at;
        }

        public int getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(int updated_at) {
            this.updated_at = updated_at;
        }
    }
}
