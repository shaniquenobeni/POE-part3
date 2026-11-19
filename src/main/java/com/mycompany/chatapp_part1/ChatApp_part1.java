package com.mycompany.chatapp_part1;

import java.util.Scanner;

public class ChatApp_part1 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();

        System.out.println("--- User Registration ---");
        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        String regStatus = login.registerUser(username, password, firstName, lastName);
        System.out.println(regStatus);

        if (!regStatus.equals("The two above conditions have been met, and the user has been registered successfully.")) {
            System.out.println("Registration failed. Exiting application.");
            return;
        }

        System.out.println("\n--- User Login ---");
        System.out.print("Enter Username: ");
        String loginUsername = scanner.nextLine();
        System.out.print("Enter Password: ");
        String loginPassword = scanner.nextLine();

        boolean isLoggedIn = login.loginUser(loginUsername, loginPassword);
        System.out.println(login.returnLoginStatus(isLoggedIn));

        if (isLoggedIn) {
            showMainMenu(scanner, firstName + " " + lastName);
        }
    }

    public static void showMainMenu(Scanner scanner, String currentUser) {
        Message messageHandler = new Message();
        messageHandler.loadStoredMessagesFromJSON();
        boolean running = true;
        int messageCounter = 1;

        while (running) {
            System.out.println("\n==================================");
            System.out.println("WELCOME TO QUICKCHAT");
            System.out.println("==================================");
            System.out.println("1. Send Messages");
            System.out.println("2. Show Recently Sent / Disregarded Messages");
            System.out.println("3. Quit");
            System.out.println("4. Stored Messages & Reports");
            System.out.print("Choose an option: ");

            if (!scanner.hasNextInt()) {
                scanner.nextLine();
                System.out.println("Invalid input. Enter a number (1-4).");
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter Recipient Cell Number: ");
                    String recipient = scanner.nextLine();
                    if (!messageHandler.checkRecipientCell(recipient)) {
                        System.out.println("Invalid recipient number format!");
                        break;
                    }

                    System.out.print("Enter Message Text (Max 250 chars): ");
                    String text = scanner.nextLine();
                    if (!messageHandler.checkMessageLength(text)) {
                        System.out.println("Message exceeds 250 characters!");
                        break;
                    }

                    System.out.println("Select Action: 1 = Send Message | 2 = Disregard Message | 3 = Store Message");
                    int action = scanner.nextInt();
                    scanner.nextLine();

                    String flag = (action == 1) ? "Sent" : (action == 2) ? "Disregard" : "Stored";
                    String id = messageHandler.generateMessageID();
                    String hash = messageHandler.createMessageHash(messageCounter, id, text);

                    messageHandler.recordMessage(currentUser, recipient, text, flag, id, hash);
                    System.out.println("Message recorded as [" + flag + "] | ID: " + id + " | Hash: " + hash);
                    messageCounter++;
                    break;

                case 2:
                    System.out.println("Total Disregarded Messages: " + Message.disregardCount);
                    for (int i = 0; i < Message.disregardCount; i++) {
                        System.out.println("- " + Message.disregardedMessages[i]);
                    }
                    break;

                case 3:
                    System.out.println("Exiting application. Goodbye!");
                    running = false;
                    break;

                case 4:
                    showPart3SubMenu(scanner, messageHandler);
                    break;

                default:
                    System.out.println("Invalid option. Please choose 1-4.");
            }
        }
    }

    public static void showPart3SubMenu(Scanner scanner, Message messageHandler) {
        System.out.println("\n--- STORED MESSAGES & REPORTS ---");
        System.out.println("a. Display sender and recipient of all messages");
        System.out.println("b. Display the longest message");
        System.out.println("c. Search for a Message ID");
        System.out.println("d. Search all messages for a particular recipient");
        System.out.println("e. Delete a message using Message Hash");
        System.out.println("f. Display full Sent Messages report");
        System.out.print("Choose sub-option (a-f): ");
        String sub = scanner.nextLine().toLowerCase();

        switch (sub) {
            case "a":
                System.out.println(messageHandler.displaySendersAndRecipients());
                break;
            case "b":
                System.out.println("Longest Message: " + messageHandler.getLongestMessage());
                break;
            case "c":
                System.out.print("Enter Message ID to search: ");
                System.out.println(messageHandler.searchByMessageID(scanner.nextLine()));
                break;
            case "d":
                System.out.print("Enter Recipient Cell Number: ");
                System.out.println(messageHandler.searchByRecipient(scanner.nextLine()));
                break;
            case "e":
                System.out.print("Enter Message Hash to delete: ");
                System.out.println(messageHandler.deleteByMessageHash(scanner.nextLine()));
                break;
            case "f":
                System.out.println(messageHandler.displayFullReport());
                break;
            default:
                System.out.println("Invalid sub-option.");
        }
    }
}