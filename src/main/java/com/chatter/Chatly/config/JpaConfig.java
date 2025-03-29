package com.chatter.Chatly.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing  // JPA Auditing 기능을 활성화 (@CreatedDate 사용 가능)
public class JpaConfig {
}
