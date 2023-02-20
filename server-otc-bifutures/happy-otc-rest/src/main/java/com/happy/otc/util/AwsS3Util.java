package com.happy.otc.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

/**
 * @Author: zhuligang
 * @Date: 2018/9/6 16:37
 * @Description:
 */
@Component
public class AwsS3Util {
    private static final Logger logger = LogManager.getLogger(AwsS3Util.class);

    private static String accessKeyID;
    private static String secretKey;

    @Value("${aws.access.key.id}")
    public void setAccessKeyID(String accessKeyID) {
        AwsS3Util.accessKeyID = accessKeyID;
    }

    @Value("${aws.secret.access.key}")
    public void setSecretKey(String secretKey) {
        AwsS3Util.secretKey = secretKey;
    }

//    private static String accessKeyID = "AKIAJ4DJCC7L4DCKPBFA";
//    private static String secretKey = "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";

    private static  AWSCredentials credentials ;
    private static  AmazonS3 s3Client;

    private static void getInit() {
        //初始化
        if (s3Client == null) {
            //创建Amazon S3对象
            credentials = new BasicAWSCredentials(accessKeyID, secretKey);
            s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(Regions.AP_NORTHEAST_2)
                    .build();
        }
    }

    /**
     * 上传图片
     * @param bucket
     * @param file
     * @param prefix
     * @param key
     * @return
     */
    public static String uploadImage(String bucket, MultipartFile file, String prefix, String key) {

        logger.info("开始上传");
        getInit();

        if (StringUtils.isBlank(key)) {
            key = UUID.randomUUID().toString();
        }
        if (StringUtils.isNotBlank(prefix)) {
            key = prefix + "/" + key;
        }

        byte[] data = null;
        try {
            data = file.getBytes();
        }catch (IOException ex){
            logger.info("上传异常");
            return null;
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(data.length);
        PutObjectResult result = s3Client.putObject(bucket, key, new ByteArrayInputStream(data), metadata);
        if (StringUtils.isNotBlank(result.getETag())) {
            return key;
        }
        logger.info("上传结束");


        return null;
    }

    /**
     * 下载
     * @param bucket_name
     * @param key_name
     * @return
     */
    public static File getObject(String bucket_name, String key_name) {
        getInit();
        logger.info("开始下载:" + key_name);

        File resultFile = null;
        S3ObjectInputStream s3is = null;
        FileOutputStream fos = null;
        try {
            S3Object o = s3Client.getObject(bucket_name, key_name);
            s3is = o.getObjectContent();
            resultFile = new File(key_name.substring(key_name.lastIndexOf("/") + 1));
            fos = new FileOutputStream(resultFile);
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Exception e){
            logger.error(e.getMessage());
        } finally {
            if (s3is != null) {
                try {
                    s3is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.info("下载结束:" + key_name);
        return resultFile;
    }

}
