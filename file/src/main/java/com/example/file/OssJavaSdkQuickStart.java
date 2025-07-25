package com.example.file;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyuncs.exceptions.ClientException;

import java.io.*;
import java.util.Random;

public class OssJavaSdkQuickStart {
    /** 生成一个唯一的 Bucket 名称 */
    public static String generateUniqueBucketName(String prefix) {
        // 获取当前时间戳
        String timestamp = String.valueOf(System.currentTimeMillis());
        // 生成一个 0 到 9999 之间的随机数
        Random random = new Random();
        int randomNum = random.nextInt(10000); // 生成一个 0 到 9999 之间的随机数
        // 连接以形成一个唯一的 Bucket 名称
        return prefix + "-" + timestamp + "-" + randomNum;
    }

    public static OSS getOssClient(String endpoint) {
        EnvironmentVariableCredentialsProvider credentialsProvider = null;
        try {
            // 创建 OSSClient 实例
            credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        }
        catch (ClientException e) {
            e.printStackTrace();
        }
        // 替换为您的 Bucket 区域
        String region = "cn-chengdu";

        return OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();
    }

    public static OSS getOssClient2(String endpoint) {
        String accessKeyId = "";
        String accessKeySecret = "";
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    public static void createBucket(OSS ossClient, String bucketName) {
        ossClient.createBucket(bucketName);
        System.out.println("1. Bucket " + bucketName + " 创建成功。");
    }

    public static void putObject(OSS ossClient, String bucketName, String objectName) {
        String content = "Hello OSS";
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content.getBytes()));
        System.out.println("2. 文件 " + objectName + " 上传成功。");
    }

    public static void getObject(OSS ossClient, String bucketName, String objectName) {
        OSSObject ossObject = ossClient.getObject(bucketName, objectName);
        InputStream contentStream = ossObject.getObjectContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(contentStream));
        String line;
        System.out.println("3. 下载的文件内容：");
        try {
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            contentStream.close();
        }
        catch (IOException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        }
    }

    public static void listObjects(OSS ossClient, String bucketName) {
        System.out.println("4. 列出 Bucket 中的文件：");
        ObjectListing objectListing = ossClient.listObjects(bucketName);
        for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            System.out.println(" - " + objectSummary.getKey() + " (大小 = " + objectSummary.getSize() + ")");
        }
    }

    public static void deleteObject(OSS ossClient, String bucketName, String objectName) {
        ossClient.deleteObject(bucketName, objectName);
        System.out.println("5. 文件 " + objectName + " 删除成功。");
    }

    public static void deleteBucket(OSS ossClient, String bucketName) {
        ossClient.deleteBucket(bucketName);
        System.out.println("6. Bucket " + bucketName + " 删除成功。");
    }

    public static void main(String[] args) {
        OSS ossClient = getOssClient("https://oss-cn-hangzhou.aliyuncs.com");
        String bucketName = generateUniqueBucketName("demo");
        try {
            // 1. 创建存储空间（Bucket）
            createBucket(ossClient, bucketName);

            String objectName = "exampledir/exampleobject.txt";
            // 2. 上传文件
            putObject(ossClient, bucketName, objectName);
            // 3. 下载文件
            getObject(ossClient, bucketName, objectName);
            // 4. 列出文件
            listObjects(ossClient, bucketName);
            // 5. 删除文件
            deleteObject(ossClient, bucketName, objectName);
            // 6. 删除存储空间（Bucket）
            deleteBucket(ossClient, bucketName);
        }
        catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        }
        finally {
            ossClient.shutdown();
        }
    }
}
