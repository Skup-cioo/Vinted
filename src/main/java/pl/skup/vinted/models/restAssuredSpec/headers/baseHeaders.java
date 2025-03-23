package pl.skup.vinted.models.restAssuredSpec.headers;

import pl.skup.vinted.springPackage.services.LogInService;

import java.util.Map;
import java.util.Objects;

public class baseHeaders {

    private static String getCookieOrReturnBadCookie() {
        if (Objects.nonNull(LogInService.token)) {
            return LogInService.token;
        } else {
            return "NO Cookie - Bad Request";
        }
    }

    public static Map<String, String> BASE_HEADERS = Map.of(
            "cookie", getCookieOrReturnBadCookie()
    );
}
