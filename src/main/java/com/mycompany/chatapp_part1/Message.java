package com.mycompany.chatapp_part1;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;

public class Message {

    // Parallel Arrays (Capacity of 100 messages)
    public static String[] sentMessages = new String[100];
    public static String[] disregardedMessages = new String[100];
    public static String[] storedMessages = new String[100];
    public static String[] messageHashes = new String[100];
    public static String[] messageIDs = new String[100];
    public static String[] recipients = new String[100];
    public static String[] senders = new String[100];
    public static String[] flags = new String[100]; // "Sent", "Stored", "Disregard"

    public static int sentCount = 0;
    public static int disregardCount = 0;
    public static int storedCount = 0;
    public static int totalCount = 0;

    private static final String JSON_FILE = "stored_messages.json";

    // --- PART 2 VALIDATION & GENERATION METHODS ---
    public boolean checkRecipientCell(String cellNumber) {
        if (cellNumber == null) return false;
        return cellNumber.matches("^\\+\\d{10,12}$") || cellNumber.matches("^\\d{10}$");
    }

    public boolean checkMessageLength(String text) {
        if (text == null) return false;
        return text.length() <= 250;
    }

    public String generateMessageID() {
        String chars = "0123456789";
        StringBuilder id = new StringBuilder();
        Random rnd = new Random();
        while (id.length() < 10) {
            id.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return id.toString();
    }

    public String createMessageHash(int num, String id, String text) {
        if (text == null || text.trim().isEmpty()) {
            return num + ":" + id.substring(0, 2) + ":NONE";
        }
        String[] words = text.trim().split("\\s+");
        String firstWord = words[0];
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;
        return (num + ":" + id.substring(0, 2) + ":" + firstWord + "-" + lastWord).toUpperCase();
    }

    // --- PART 3: POPULATE ARRAYS (NO HARDCODING) ---
    public void recordMessage(String sender, String recipient, String text, String flag, String id, String hash) {
        if (totalCount >= 100) return;

        senders[totalCount] = sender;
        recipients[totalCount] = recipient;
        messageIDs[totalCount] = id;
        messageHashes[totalCount] = hash;
        flags[totalCount] = flag;

        if (flag.equalsIgnoreCase("Sent")) {
            sentMessages[sentCount++] = text;
        } else if (flag.equalsIgnoreCase("Disregard")) {
            disregardedMessages[disregardCount++] = text;
        } else if (flag.equalsIgnoreCase("Stored")) {
            storedMessages[storedCount++] = text;
            saveToJSON();
        }
        totalCount++;
    }

    // --- PART 3: JSON STORAGE & READING ---
    public void saveToJSON() {
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < totalCount; i++) {
                if ("Stored".equalsIgnoreCase(flags[i])) {
                    JSONObject obj = new JSONObject();
                    obj.put("sender", senders[i]);
                    obj.put("recipient", recipients[i]);
                    obj.put("messageID", messageIDs[i]);
                    obj.put("messageHash", messageHashes[i]);
                    obj.put("message", getMessageTextByIndex(i));
                    jsonArray.put(obj);
                }
            }
            try (FileWriter file = new FileWriter(JSON_FILE)) {
                file.write(jsonArray.toString(4));
            }
        } catch (Exception e) {
            System.out.println("Error saving JSON: " + e.getMessage());
        }
    }

    public void loadStoredMessagesFromJSON() {
        try {
            File file = new File(JSON_FILE);
            if (!file.exists()) return;

            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE)));
            JSONArray jsonArray = new JSONArray(content);
            storedCount = 0;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                storedMessages[storedCount++] = obj.getString("message");
            }
        } catch (Exception e) {
            System.out.println("Error reading JSON: " + e.getMessage());
        }
    }

    // Helper to retrieve message text aligned with parallel index
    private String getMessageTextByIndex(int index) {
        int sIdx = 0, dIdx = 0, stIdx = 0;
        for (int i = 0; i <= index; i++) {
            if ("Sent".equalsIgnoreCase(flags[i])) {
                if (i == index) return sentMessages[sIdx];
                sIdx++;
            } else if ("Disregard".equalsIgnoreCase(flags[i])) {
                if (i == index) return disregardedMessages[dIdx];
                dIdx++;
            } else if ("Stored".equalsIgnoreCase(flags[i])) {
                if (i == index) return storedMessages[stIdx];
                stIdx++;
            }
        }
        return "";
    }

    // --- PART 3 MENU OPTION 4 FEATURES (a - f) ---

    // 2a. Display sender and recipient of all stored/sent messages
    public String displaySendersAndRecipients() {
        StringBuilder sb = new StringBuilder("--- Senders & Recipients ---\n");
        for (int i = 0; i < totalCount; i++) {
            if ("Sent".equalsIgnoreCase(flags[i]) || "Stored".equalsIgnoreCase(flags[i])) {
                sb.append("Sender: ").append(senders[i])
                  .append(" | Recipient: ").append(recipients[i]).append("\n");
            }
        }
        return sb.toString();
    }

    // 2b. Display the longest message
    public String getLongestMessage() {
        String longest = "";
        for (int i = 0; i < totalCount; i++) {
            if ("Sent".equalsIgnoreCase(flags[i]) || "Stored".equalsIgnoreCase(flags[i])) {
                String current = getMessageTextByIndex(i);
                if (current.length() > longest.length()) {
                    longest = current;
                }
            }
        }
        return longest;
    }

    // 2c. Search for a Message ID and display corresponding recipient and message
    public String searchByMessageID(String searchID) {
        for (int i = 0; i < totalCount; i++) {
            if (messageIDs[i] != null && messageIDs[i].equals(searchID)) {
                return "Recipient: " + recipients[i] + " | Message: " + getMessageTextByIndex(i);
            }
        }
        return "Message ID not found.";
    }

    // 2d. Search for all messages sent or stored for a particular recipient
    public String searchByRecipient(String searchRecipient) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < totalCount; i++) {
            if (recipients[i] != null && recipients[i].equals(searchRecipient)
                    && ("Sent".equalsIgnoreCase(flags[i]) || "Stored".equalsIgnoreCase(flags[i]))) {
                if (sb.length() > 0) sb.append("\n");
                sb.append(getMessageTextByIndex(i));
            }
        }
        return sb.length() > 0 ? sb.toString() : "No messages found for recipient.";
    }

    // 2e. Delete a message using the message hash
    public String deleteByMessageHash(String hashToDelete) {
        for (int i = 0; i < totalCount; i++) {
            if (messageHashes[i] != null && messageHashes[i].equalsIgnoreCase(hashToDelete)) {
                String deletedText = getMessageTextByIndex(i);
                // Shift elements left in parallel arrays
                for (int j = i; j < totalCount - 1; j++) {
                    senders[j] = senders[j + 1];
                    recipients[j] = recipients[j + 1];
                    messageIDs[j] = messageIDs[j + 1];
                    messageHashes[j] = messageHashes[j + 1];
                    flags[j] = flags[j + 1];
                }
                totalCount--;
                saveToJSON();
                return "Message \"" + deletedText + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }

    // 2f. Display a full report of all sent messages
    public String displayFullReport() {
        StringBuilder sb = new StringBuilder("=== SENT MESSAGES REPORT ===\n");
        for (int i = 0; i < totalCount; i++) {
            if ("Sent".equalsIgnoreCase(flags[i])) {
                sb.append("Message Hash: ").append(messageHashes[i]).append("\n")
                  .append("Recipient: ").append(recipients[i]).append("\n")
                  .append("Message: ").append(getMessageTextByIndex(i)).append("\n")
                  .append("----------------------------\n");
            }
        }
        return sb.toString();
    }
}