package com.letraaletra.api.domain.game.service;

import java.security.SecureRandom;

public class GenerateRoomCode {
    private final SecureRandom random = new SecureRandom();

    public String execute() {
        int length = 6;
        StringBuilder code = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            String base = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
            int index = random.nextInt(base.length());
            code.append(base.charAt(index));
        }

        return code.toString();
    }
}
