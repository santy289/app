package com.rootnetapp.rootnetintranet.models.responses.user;

import java.util.List;

import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.squareup.moshi.Json;

/**
 * Created by Propietario on 14/03/2018.
 */


public class UserResponse {

    @Json(name = "status")
    private String status;
    @Json(name = "code")
    private int code;
    @Json(name = "profiles")
    private List<User> profiles = null;
    @Json(name = "pager")
    private Pager pager;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<User> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<User> profiles) {
        this.profiles = profiles;
    }

    public Pager getPager() {
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }
    /*
import com.squareup.moshi.Json;

    public class Roles {

        @Json(name = "0")
        private String _0;
        @Json(name = "1")
        private String _1;
        @Json(name = "2")
        private String _2;
        @Json(name = "3")
        private String _3;
        @Json(name = "4")
        private String _4;
        @Json(name = "5")
        private String _5;
        @Json(name = "6")
        private String _6;
        @Json(name = "7")
        private String _7;
        @Json(name = "8")
        private String _8;
        @Json(name = "9")
        private String _9;
        @Json(name = "10")
        private String _10;
        @Json(name = "11")
        private String _11;
        @Json(name = "12")
        private String _12;
        @Json(name = "13")
        private String _13;
        @Json(name = "14")
        private String _14;
        @Json(name = "15")
        private String _15;
        @Json(name = "16")
        private String _16;
        @Json(name = "17")
        private String _17;
        @Json(name = "18")
        private String _18;
        @Json(name = "19")
        private String _19;
        @Json(name = "20")
        private String _20;
        @Json(name = "38")
        private String _38;
        @Json(name = "39")
        private String _39;

        public String get0() {
            return _0;
        }

        public void set0(String _0) {
            this._0 = _0;
        }

        public String get1() {
            return _1;
        }

        public void set1(String _1) {
            this._1 = _1;
        }

        public String get2() {
            return _2;
        }

        public void set2(String _2) {
            this._2 = _2;
        }

        public String get3() {
            return _3;
        }

        public void set3(String _3) {
            this._3 = _3;
        }

        public String get4() {
            return _4;
        }

        public void set4(String _4) {
            this._4 = _4;
        }

        public String get5() {
            return _5;
        }

        public void set5(String _5) {
            this._5 = _5;
        }

        public String get6() {
            return _6;
        }

        public void set6(String _6) {
            this._6 = _6;
        }

        public String get7() {
            return _7;
        }

        public void set7(String _7) {
            this._7 = _7;
        }

        public String get8() {
            return _8;
        }

        public void set8(String _8) {
            this._8 = _8;
        }

        public String get9() {
            return _9;
        }

        public void set9(String _9) {
            this._9 = _9;
        }

        public String get10() {
            return _10;
        }

        public void set10(String _10) {
            this._10 = _10;
        }

        public String get11() {
            return _11;
        }

        public void set11(String _11) {
            this._11 = _11;
        }

        public String get12() {
            return _12;
        }

        public void set12(String _12) {
            this._12 = _12;
        }

        public String get13() {
            return _13;
        }

        public void set13(String _13) {
            this._13 = _13;
        }

        public String get14() {
            return _14;
        }

        public void set14(String _14) {
            this._14 = _14;
        }

        public String get15() {
            return _15;
        }

        public void set15(String _15) {
            this._15 = _15;
        }

        public String get16() {
            return _16;
        }

        public void set16(String _16) {
            this._16 = _16;
        }

        public String get17() {
            return _17;
        }

        public void set17(String _17) {
            this._17 = _17;
        }

        public String get18() {
            return _18;
        }

        public void set18(String _18) {
            this._18 = _18;
        }

        public String get19() {
            return _19;
        }

        public void set19(String _19) {
            this._19 = _19;
        }

        public String get20() {
            return _20;
        }

        public void set20(String _20) {
            this._20 = _20;
        }

        public String get38() {
            return _38;
        }

        public void set38(String _38) {
            this._38 = _38;
        }

        public String get39() {
            return _39;
        }

        public void set39(String _39) {
            this._39 = _39;
        }

    }*/
}
