package site.persipa.common.version;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * 提供应用构建版本的 HTTP endpoint。
 *
 * @author persipa
 */
@RestController
public class AppVersionEndpointController {

    private final ObjectProvider<BuildProperties> buildPropertiesProvider;

    public AppVersionEndpointController(ObjectProvider<BuildProperties> buildPropertiesProvider) {
        this.buildPropertiesProvider = buildPropertiesProvider;
    }

    /**
     * 返回当前应用构建版本。
     *
     * @return 应用构建版本
     */
    @GetMapping("${persipa.cloud.app-version.endpoint.path:/_version}")
    public AppVersionEndpointResponse getVersion() {
        BuildProperties buildProperties = buildPropertiesProvider.getIfAvailable();
        if (buildProperties == null || !StringUtils.hasText(buildProperties.getVersion())) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Build version is unavailable");
        }
        return new AppVersionEndpointResponse(buildProperties.getVersion());
    }
}
