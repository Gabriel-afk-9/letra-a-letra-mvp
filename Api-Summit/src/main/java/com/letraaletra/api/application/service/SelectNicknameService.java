package com.letraaletra.api.application.service;

import com.letraaletra.api.domain.repository.user.UserRepository;

import java.util.Random;
import java.util.UUID;

public class SelectNicknameService {
    private final UserRepository userRepository;
    private final Random random;

    private static final String[] firstPart = {
            "Wolf", "Falcon", "Tiger", "Eagle", "Panther",
            "Dragon", "Viper", "Shadow", "Storm", "Blaze",
            "Frost", "Thunder", "Ghost", "Phantom", "Raven",
            "Hawk", "Lion", "Scorpion", "Kraken", "Phoenix",
            "Titan", "Hunter", "Sniper", "Warrior", "Knight",
            "Samurai", "Ninja", "Gladiator", "Raider", "Assassin",
            "Guardian", "Sentinel", "Nomad", "Warden", "Paladin",
            "Berserker"
    };

    private static final String[] secondPart = {
            "Attacker", "Slayer", "Breaker", "Striker", "Shooter",
            "Sniper", "Destroyer", "Crusher", "Invoker", "Caster",
            "Rider", "Walker", "Runner", "Jumper", "Seeker",
            "Tracker", "Hunter", "Defender", "Protector", "Conqueror",
            "Ruler", "Master", "Lord", "King", "Reaper",
            "Executioner", "Avenger", "Dominator", "Overlord", "Vanquisher"
    };

    public SelectNicknameService(UserRepository userRepository, Random random) {
        this.userRepository = userRepository;
        this.random = random;
    }

    public String execute() {
        while (true) {

            String first = firstPart[random.nextInt(firstPart.length)];
            String second = secondPart[random.nextInt(secondPart.length)];

            if (first.equalsIgnoreCase(second)) continue;

            int remainingLength = 15 - (first.length() + second.length());

            if (remainingLength < 2) continue;

            String suffix = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, remainingLength);

            String nickname = first + second + suffix;

            if (!userRepository.existsByNickname(nickname)) {
                return nickname;
            }
        }
    }
}
