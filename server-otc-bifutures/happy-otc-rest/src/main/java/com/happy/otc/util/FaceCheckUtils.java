package com.happy.otc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;

/**
 * @Author: zhuligang
 * @Date: 2018/9/12 9:47
 * @Description:
 */
public class FaceCheckUtils {

    private final static Logger logger = LoggerFactory.getLogger(FaceCheckUtils.class);

    private static String account = "hzmd_rx";
    private static String password = "9H3LupzBh3AP2G57V5YATKRxfC6kEAA6";
    private static String url = "https://service.yjfinance.com/api";

    public static String checkFace(String idnumber, String name, String photo){
        String acode = "900500";//服务代码

        String param = "name=" + name + "&idnumber=" + idnumber + "&photo=" + photo;
        String sign = md5(acode + param + account + md5(password));//生成签名 (MD5大写)

        String post_data = null;
        try {
            post_data = "acode=" + acode + "&param=" + URLEncoder.encode(param, "UTF-8") + "&account="
                    + account + "&sign=" + sign;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        String json = postHtml(url, post_data);
        //返回的 json 即为查询到的信息
        logger.info("面部识别:" + json);
        return json;
    }

    public static String checkFaceWithIDCard(String idnumber, String name, String photo){
        String acode = "900550";//服务代码

        String param = "name=" + name + "&idnumber=" + idnumber + "&image=" + photo;
        String sign = md5(acode + param + account + md5(password));//生成签名 (MD5大写)

        String post_data = null;
        try {
            post_data = "acode=" + acode + "&param=" + URLEncoder.encode(param, "UTF-8") + "&account="
                    + account + "&sign=" + sign;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        String json = postHtml(url, post_data);
        //返回的 json 即为查询到的信息
        logger.info("面部识别:" + json);
        return json;
    }

    private String getSign(String idnumber, String name, String photo){
        StringBuilder key = new StringBuilder();
        key.append("900500")
                .append(name).append("&").append(idnumber)
                .append(account)
                .append(md5(password));
        String sign = md5(key.toString());
        return sign;
    }

    private static String md5(String text) {
        byte[] bts;
        try {
            bts = text.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bts_hash = md.digest(bts);
            StringBuffer buf = new StringBuffer();
            for (byte b : bts_hash) {
                buf.append(String.format("%02X", b & 0xff));
            }
            return buf.toString();
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String postHtml(String url, String postData) {
        try {
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(postData);
            out.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "UTF-8"));
            StringBuffer response = new StringBuffer();

            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
