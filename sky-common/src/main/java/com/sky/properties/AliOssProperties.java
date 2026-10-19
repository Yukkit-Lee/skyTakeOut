package com.sky.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.alioss")
@Getter
@Setter
public class AliOssProperties {

    // Bucket 所在地域，例如 cn-guangzhou；SDK V2 会据此选择默认访问地址。
    private String bucketRegion;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

}
