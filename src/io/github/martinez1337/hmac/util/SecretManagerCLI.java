package io.github.martinez1337.hmac.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class SecretManagerCLI {

    public static void main(String[] args) {
        if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
            System.out.println("Usage:");
            System.out.println("  generate <config-path>          - Generate and save a new random key");
            System.out.println("  set <config-path> <new-secret>  - Set a specific secret");
            return;
        }

        String command = args[0];
        try {
            switch (command) {
                case "generate" -> generateAndSave(args[1]);
                case "set" -> updateConfig(args[1], args[2]);
                default -> System.out.println("Unknown command: " + command);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void generateAndSave(String configPath) throws Exception {
        // Генерируем 32 случайных байта (256 бит) для HmacSHA256
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        String newSecret = Base64.getEncoder().encodeToString(randomBytes);

        updateConfig(configPath, newSecret);
        System.out.println("New secret generated and saved successfully.");
    }

    private static void updateConfig(String configPath, String secret) throws Exception {
        JsonObject config;
        try (FileReader reader = new FileReader(configPath, StandardCharsets.UTF_8)) {
            config = JsonParser.parseReader(reader).getAsJsonObject();
        }

        config.addProperty("secret", secret);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(configPath, StandardCharsets.UTF_8)) {
            gson.toJson(config, writer);
        }
    }
}