package com.sky.utils;

import com.aliyun.sdk.service.oss2.OSSAsyncClient;
import com.aliyun.sdk.service.oss2.credentials.CredentialsProvider;
import com.aliyun.sdk.service.oss2.credentials.StaticCredentialsProvider;
import com.aliyun.sdk.service.oss2.models.PutObjectRequest;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import com.sky.properties.AliOssProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * 阿里云 OSS 文件上传工具，使用 OSS Java SDK V2。
 */
@Component
@Slf4j
public class AliOssUtil implements DisposableBean {

    private final AliOssProperties properties;
    private final OSSAsyncClient client;

    public AliOssUtil(AliOssProperties properties) {
        this.properties = properties;

        // 客户端按地域选择默认访问地址，并在整个应用运行期间复用连接。
        CredentialsProvider credentialsProvider = new StaticCredentialsProvider(
                properties.getAccessKeyId(), properties.getAccessKeySecret());
        this.client = OSSAsyncClient.newBuilder()
                .region(properties.getBucketRegion())
                .credentialsProvider(credentialsProvider)
                .build();
    }

    /**
     * 根据原始文件名保留扩展名，并生成唯一对象名后上传。
     * 上传失败时将异常交给调用方处理。
     *
     * @param bytes 文件内容
     * @param originalFilename 前端上传文件的原始名称，用于提取扩展名
     * @return 对象的访问地址；能否直接访问取决于 Bucket 权限
     */
    public String upload(byte[] bytes, String originalFilename) {
        // 只取简单的字母或数字扩展名，避免原始文件名影响 OSS 对象路径。
        String extension = "";
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < originalFilename.length() - 1) {
                String suffix = originalFilename.substring(dotIndex + 1);
                if (suffix.matches("[A-Za-z0-9]{1,10}")) {
                    extension = "." + suffix;
                }
            }
        }
        String objectName = UUID.randomUUID() + extension;

        PutObjectRequest request = PutObjectRequest.newBuilder()
                .bucket(properties.getBucketName())
                .key(objectName)
                .body(BinaryData.fromStream(new ByteArrayInputStream(bytes)))
                .build();

        // 等待异步上传完成后再返回地址，避免失败时返回一个不存在的文件地址。
        client.putObjectAsync(request).join();

        String url = "https://" + properties.getBucketName()
                + ".oss-" + properties.getBucketRegion()
                + ".aliyuncs.com/" + objectName;
        log.info("文件上传到：{}", url);
        return url;
    }

    @Override
    public void destroy() throws Exception {
        // Spring 关闭应用时释放 SDK 客户端持有的连接资源。
        client.close();
    }
}
