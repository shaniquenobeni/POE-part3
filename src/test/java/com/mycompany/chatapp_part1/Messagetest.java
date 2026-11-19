package com.mycompany.chatapp_part1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Messagetest {

    private Message msg;

    @BeforeEach
    public void setUp() {
        // Reset static arrays before each test
        Message.sentCount = 0;
        Message.disregardCount = 0;
        Message.storedCount = 0;
        Message.totalCount = 0;
        msg = new Message();

        // Populate Test Data Messages 1 - 5
        msg.recordMessage("Developer", "+27834557896", "Did you get the cake?", "Sent", "ID001", "1:ID:DID-CAKE?");
        msg.recordMessage("Developer", "+27838884567", "Where are you? You are late! I have asked you to be on time.", "Stored", "ID002", "2:ID:WHERE-TIME.");
        msg.recordMessage("Developer", "+27834484567", "Yohoooo, I am at your gate.", "Disregard", "ID003", "3:ID:YOHOOOO-GATE.");
        msg.recordMessage("Developer", "0838884567", "It is dinner time !", "Sent", "ID004", "4:ID:IT-!");
        msg.recordMessage("Developer", "+27838884567", "Ok, I am leaving without you.", "Stored", "ID005", "5:ID:OK-YOU.");
    }

    @Test
    public void testSentMessagesArrayPopulated() {
        assertEquals("Did you get the cake?", Message.sentMessages[0]);
        assertEquals("It is dinner time !", Message.sentMessages[1]);
    }

    @Test
    public void testDisplayLongestMessage() {
        assertEquals("Where are you? You are late! I have asked you to be on time.", msg.getLongestMessage());
    }

    @Test
    public void testSearchForMessageID() {
        String result = msg.searchByMessageID("ID004");
        assertEquals("Recipient: 0838884567 | Message: It is dinner time !", result);
    }

    @Test
    public void testSearchAllMessagesForRecipient() {
        String result = msg.searchByRecipient("+27838884567");
        String expected = "Where are you? You are late! I have asked you to be on time.\nOk, I am leaving without you.";
        assertEquals(expected, result);
    }

    @Test
    public void testDeleteMessageUsingHash() {
        String result = msg.deleteByMessageHash("2:ID:WHERE-TIME.");
        assertEquals("Message \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.", result);
    }

    @Test
    public void testDisplayReport() {
        String report = msg.displayFullReport();
        assertTrue(report.contains("1:ID:DID-CAKE?"));
        assertTrue(report.contains("+27834557896"));
        assertTrue(report.contains("Did you get the cake?"));
    }
}