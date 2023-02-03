/**
 * Author: Shivam Patel
 * Andrew ID: shpatel
 * Email: shpatel@cmu.edu
 * Last Modified: November 1, 2022
 * File: ResponseMessage.java
 * Part Of: Project3Task1
 *
 * This Java file defines and helps create a JSON message that would be
 * sent from the server to the client. Since there are different types of
 * response messages sent from the server, multiple subclasses of the
 * ResponseMessage class have been created. These subclasses include,
 * NormalResponseMessage to handle normal response messages that send the
 * operation number and the response message. The next subclass is
 * StatusResponseMessage that send details regarding the status of the
 * blockchain to display the relevant details. Finally, the
 * VerificationResponseMessage sends information regarding the verification
 * output of the blockchain.
 */

// Defines the package for the Java file
package org.example;

// Imports necessary for Gson and BigInteger
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.math.BigInteger;

public class ResponseMessage {

    // Stores the selection number that the user selected
    protected int selection;

}

// This class stores information about a normal response message from the server
// which include only the selection number and the response message. The toString
// method converts the NormalResponseMessage object to a JSON String using Gson.
class NormalResponseMessage extends ResponseMessage {

    // Stores the response from the server for normal response messages
    String response;

    // Constructor to initialize the values of the instance variables
    NormalResponseMessage (int selection, String response) {
        super.selection = selection;
        this.response = response;
    }

    /***
     * The method converts the NormalResponseMessage object to a JSON String using Gson.
     * @return JSON String representation of the NormalResponseMessage object
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

class StatusResponseMessage extends ResponseMessage {

    // Stores the size of the chain
    int size;

    // Stores the expected total hashes required
    int totalHashes;

    // Stores the total difficulty of the chain
    int totalDiff;

    // Stores the nonce of the block that was most recently added to the chain
    BigInteger recentNonce;

    // Stores the difficulty of the most recent block
    int diff;

    // Stores the value of the hashes per second
    int hps;

    // Stores the chain hash
    String chainHash;

    // Constructor to initialize the values of the instance variables
    StatusResponseMessage (int selection, int size, String chainHash, int totalHashes, int totalDiff,
                           BigInteger recentNonce, int diff, int hps) {

        super.selection = selection;
        this.size = size;
        this.chainHash = chainHash;
        this.totalHashes = totalHashes;
        this.totalDiff = totalDiff;
        this.recentNonce = recentNonce;
        this.diff = diff;
        this.hps = hps;
    }

    /***
     * The method converts the StatusResponseMessage object to a JSON String using Gson.
     * @return JSON String representation of the StatusResponseMessage object
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

class VerificationResponseMessage extends ResponseMessage {

    // Stores the response (time required for verification) from the server
    String response;

    // Stores the chain verification output from the server
    String verificationOutput;

    // Constructor to initialize the values of the instance variables
    VerificationResponseMessage (int selection, String response, String verificationOutput) {
        super.selection = selection;
        this.response = response;
        this.verificationOutput = verificationOutput;
    }

    /***
     * The method converts the StatusResponseMessage object to a JSON String using Gson.
     * @return JSON String representation of the StatusResponseMessage object
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