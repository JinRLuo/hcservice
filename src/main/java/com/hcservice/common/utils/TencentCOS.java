package com.hcservice.common.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Random;

@Component
public class TencentCOS {

    @Value("${tencentcos.bucketName}")
    private String bucketName;

    @Value("${tencentCOS.secretId}")
    private String secretId;

    @Value("${tencentCOS.secretKey}")
    private String secretKey;

    private COSCredentials cred;

    private ClientConfig clientConfig;

    @PostConstruct
    public void init() {
        // 1 初始化用户身份信息(secretId, secretKey，可在腾讯云后台中的API密钥管理中查看！
        cred = new BasicCOSCredentials(secretId, secretKey);

        // 2 设置bucket的区域, COS地域的简称请参照
        // https://cloud.tencent.com/document/product/436/6224，根据自己创建的存储桶选择地区
        clientConfig = new ClientConfig(new Region("ap-guangzhou"));
    }


    /**
     * 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20 M 以下的文件使用该接口 大文件上传请参照 API 文档高级 API 上传
     *
     * @param localFile 要上传的文件
     */
    public String uploadFile(File localFile,String pathPrefix) throws CosClientException, CosServiceException {

        // 生成cos客户端
        COSClient cosclient = new COSClient(cred, clientConfig);
        String fileName = "";
        try {
            fileName = localFile.getName();
            String substring = fileName.substring(fileName.lastIndexOf("."));
            Random random = new Random();
            // 指定要上传到 COS 上的路径
            fileName = pathPrefix + "/" + random.nextInt(10000) + System.currentTimeMillis() + substring;
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, localFile);
            PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭客户端(关闭后台线程)
            cosclient.shutdown();
        }
        return fileName;
    }



    /**
     * 下载文件
     */
    public void downFile() {
        // 生成cos客户端
        COSClient cosclient = new COSClient(cred, clientConfig);
        //要下载的文件路径和名称
        String key = "down/demo1.jpg";
        // 指定文件的存储路径
        File downFile = new File("src/test/resources/mydown.txt");
        // 指定要下载的文件所在的 bucket 和对象键
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        ObjectMetadata downObjectMeta = cosclient.getObject(getObjectRequest, downFile);
    }


    /**
     * 删除文件
     */
    public void deleteFile(String key) throws CosClientException, CosServiceException {
        // 生成cos客户端
        COSClient cosclient = new COSClient(cred, clientConfig);
        // 指定要删除的 bucket 和路径
        cosclient.deleteObject(bucketName, key);
        // 关闭客户端(关闭后台线程)
        cosclient.shutdown();
    }

    /**
     * 删除临时文件
     *
     * @param files 临时文件，可变参数
     */
    public void deleteFile(File... files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
