package com.carrace.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CarNameGenerator {
    
    private static final Random random = new Random();
    
    private static final List<String> ADJECTIVES = Arrays.asList(
        "Lightning", "Thunder", "Blazing", "Turbo", "Nitro",
        "Sonic", "Rocket", "Phoenix", "Storm", "Shadow",
        "Golden", "Silver", "Crimson", "Azure", "Neon",
        "Phantom", "Steel", "Chrome", "Viper", "Cobra",
        "Wild", "Savage", "Fierce", "Raging", "Mad"
    );
    
    private static final List<String> NOUNS = Arrays.asList(
        "Fury", "Flash", "Streak", "Bolt", "Arrow",
        "Bullet", "Comet", "Meteor", "Star", "Blaze",
        "Storm", "Hurricane", "Tornado", "Cyclone", "Tempest",
        "Beast", "Demon", "Dragon", "Tiger", "Wolf",
        "Hawk", "Falcon", "Eagle", "Raven", "Phoenix"
    );
    
    public static String generate() {
        String adjective = ADJECTIVES.get(random.nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(random.nextInt(NOUNS.size()));
        return adjective + " " + noun;
    }
    
    public static String generateUnique(List<String> existingNames) {
        String name;
        int attempts = 0;
        do {
            name = generate();
            attempts++;
            // Fallback after 10 attempts - add a number
            if (attempts > 10) {
                name = name + " " + random.nextInt(100);
                break;
            }
        } while (existingNames.contains(name));
        return name;
    }
}
