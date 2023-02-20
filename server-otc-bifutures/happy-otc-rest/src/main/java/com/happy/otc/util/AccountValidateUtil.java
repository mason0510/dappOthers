package com.happy.otc.util;


import com.happy.otc.enums.CountryEnum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by niyang on 2017/12/28.
 */
public class AccountValidateUtil {

    public static final String REGEX_MOBILE = "^(13|14|15|16|17|18|19)\\d{9}$";

    public static final String REGEX_MOBILE_FOREIGN = "^(\\+(([0-9]){1,2})[-.])?((((([0-9]){2,3})[-.]){1,2}([0-9]{4,10}))|([0-9]{10}))$";

    public static final String REGEX_MOBILE_FOREIGN_AU = "^[4][0-9]{8}$";

    public static final String REGEX_EMAIL = "^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w{2,3}){1,3})$";

    public static Boolean validateMobile(CountryEnum countryEnum, String mobile) {
        Pattern pattern = null;
        switch (countryEnum) {
            case CHINA:
                pattern = Pattern.compile(REGEX_MOBILE);
                break;
            case CANADA:
                pattern = Pattern.compile(REGEX_MOBILE_FOREIGN);
                break;
            case AMERICA:
                pattern = Pattern.compile(REGEX_MOBILE_FOREIGN);
                break;
            case AUSTRALIA:
                pattern = Pattern.compile(REGEX_MOBILE_FOREIGN_AU);
                break;
            default:
                break;
        }
        Matcher matcher = pattern.matcher(mobile);
        return matcher.find();
    }

    public static Boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(REGEX_EMAIL);
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }
}
