package com.dku.project.domain.chat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 총동아리연합회 회칙 전문을 classpath 리소스에서 읽어 제공하는 상수 클래스입니다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FederationRulebook {

    public static final String RULEBOOK = loadRulebook();

    private static String loadRulebook() {
        ClassLoader classLoader = FederationRulebook.class.getClassLoader();

        ClassPathResource resource = new ClassPathResource(
                "federation-rulebook-20250602.txt",
                classLoader
        );

        try (InputStream is = resource.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("회칙 리소스를 읽을 수 없습니다.", e);
        }
    }
}
