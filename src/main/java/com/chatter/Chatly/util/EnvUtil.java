package com.chatter.Chatly.util;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
// import org.springframework.core.env.Profiles;

@Component
public class EnvUtil {
    private final Environment env;

    public EnvUtil(Environment env) {
        this.env = env;
    }

    public boolean isDev() {
        return env.matchesProfiles("dev", "test");
        // return env.acceptsProfiles(Profiles.of("dev", "test"));
    }
}