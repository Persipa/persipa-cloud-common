package site.persipa.common.version;

/**
 * 应用版本 HTTP endpoint 响应。
 *
 * @param version 应用构建版本
 * @author persipa
 */
public record AppVersionEndpointResponse(String version) {
}
