/**
 * Author: Shivam Patel
 * Andrew ID: shpatel
 * Email: shpatel@cmu.edu
 * Last Modified: November 1, 2022
 * File: RequestMessage.java
 * Part Of: Project3Task1
 *
 * This Java file defines and helps create a JSON message that would be
 * sent to the server from the client. Since there are different types of
 * request messages sent from the client, multiple subclasses of the
 * RequestMessage class have been created. These subclasses include,
 * NormalRequestMessage to handle normal request messages that do not send
 * anything apart from the selection number. The next subclass is
 * AddRequestMessage that send details regarding adding a new block to
 * the chain. Finally, the CorruptRequestMessage sends information regarding
 * corruption in the blockchain.
 */

// Defines the package for the Java file
package org.example;

// Imports necessary for Gson
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RequestMessage {

    // Stores the operation number in the client request
    protected int operation;
}

// This class stores information about a normal request message from the client
// which only send the operation ID to the server. The toString method convert
// the NormalRequestMessage object to a JSON String using Gson.
class NormalRequestMessage extends RequestMessage {

    // Constructor to initialize the values of the instance variables
    NormalRequestMessage (int operation) {
        super.operation = operation;
    }

    /***
     * The method converts the NormalRequestMessage object to a JSON String using Gson.
     * @return JSON String representation of the NormalRequestMessage object
     */
    public java.lang.String toString() {

        // Create a Gson object
        // Source to format date in Gson:
        // https://stackoverflow.com/questions/6873020/gson-date-format
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

        // Serialize to JSON
        return gson.toJson(this);
    }
}

// This class stores information about the add request message from the client
// which send the operation number, difficulty of new block and the transaction
// to be stored on the new block. The toString method converts the
// AddRequestMessage object to a JSON String using Gson.
class AddRequestMessage extends RequestMessage {

    // Stores the difficulty of the new Block
    int difficulty;

    // Stores the transaction to be stored on the new block
    String transactionData;

    // Constructor to initialize the values of the instance variables
    AddRequestMessage (int operation, int difficulty, String transactionData) {
        super.operation = operation;
        this.difficulty = difficulty;
        this.transactionData = transactionData;
    }

    /***
     * The method converts the AddRequestMessage object to a JSON String using Gson.
     * @return JSON String representation of the AddRequestMessage object
     */
    public java.lang.String toString() {

        // Create a Gson object
        // Source to format date in Gson:
        // https://stackoverflow.com/questions/6873020/gson-date-format
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

        // Serialize to JSON
        return gson.toJson(this);
    }
}

// This class stores information about the corrupt request message from the client
// which send the operation number, block ID of the block that is to be corrupted,
// and the transaction to be stored on the corrupted block. The toString method converts
// the CorruptRequestMessage object to a JSON String using Gson.
class CorruptRequestMessage extends RequestMessage {

    // Stores the blockID of the Block which is to be corrupted
    int blockID;

    // Stores the new (corrupted) transaction data to be stored on the blockID
    String transactionData;

    // Constructor to initialize the values of the instance variables
    CorruptRequestMessage (int operation, int blockID, String transactionData) {
        super.operation = operation;
        this.blockID = blockID;
        this.transactionData = transactionData;
    }

    /***
     * The method converts the CorruptRequestMessage object to a JSON String using Gson.
     * @return JSON String representation of the CorruptRequestMessage object
     */
    public java.lang.String toString() {

        // Create a Gson object
        // Source to format date in Gson:
        // https://stackoverflow.com/questions/6873020/gson-date-format
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

        // Serialize to JSON
        return gson.toJson(this);
    }
}
