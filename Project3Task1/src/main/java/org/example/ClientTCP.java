/**
 * Author: Shivam Patel
 * Andrew ID: shpatel
 * Email: shpatel@cmu.edu
 * Last Modified: November 1, 2022
 * File: ClientTCP.java
 * Part Of: Project3Task1
 *
 * This Java file acts as the TCP Client who will make requests for blockchain
 * operations over a TCP socket to the TCP Server. The client will be focused
 * on driving the menu-driven interaction and communicating with the server on
 * the backend. If the client exits, the server will still handle new requests
 * with the existing blockchain intact.
 */

// Defines the package for the Java file
package org.example;

// imports required for TCP/IP, IO operations and Gson
import com.google.gson.Gson;
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ClientTCP {

    public static void main(String[] args) throws IOException {

        // Creating a Scanner object for taking inputs from the user
        Scanner s = new Scanner(System.in);

        // Stores the input from the user
        String user_input;

        // Create a BufferReader object to get input from the user
        BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));

        // Keeping executing until the client is terminated
        while (true) {

            // Initialize user input to null
            user_input = "";

            // Prompt the user for operation
            System.out.println("0. View basic blockchain status.\n" +
                    "1. Add a transaction to the blockchain.\n" +
                    "2. Verify the blockchain.\n" +
                    "3. View the blockchain.\n" +
                    "4. Corrupt the chain.\n" +
                    "5. Hide the corruption by repairing the chain.\n" +
                    "6. Exit");

            // Update user input
            user_input = user_input + typed.readLine();

            // Stores the request message that would be sent to the server
            RequestMessage requestMessage;

            // Stores difficulty of block that the user will input
            int difficulty;

            // Stores blockId of block that the user will input
            int blockID;

            // Stores transaction data of block that the user will input
            String transactionData;

            // Create a Gson object
            Gson gson = new Gson();

            // Switch case for user input
            switch (user_input) {

                // If user requested to view the blockchain status
                case "0" -> {

                    // Create a normal request message
                    requestMessage = new NormalRequestMessage(0);

                    // Request the blockchain operation from server and store the value of response
                    String serverReturned = blockchain_operations(requestMessage.toString());

                    // Parse JSON response from server into StatusResponseMessage
                    StatusResponseMessage responseMessage = gson.fromJson(serverReturned, StatusResponseMessage.class);

                    // Display details to the user
                    System.out.println("Current size of chain: " + responseMessage.size);
                    System.out.println("Difficulty of most recent block: " + responseMessage.diff);
                    System.out.println("Total difficulty for all blocks: " + responseMessage.totalDiff);
                    System.out.println("Approximate hashes per second on this machine: " + responseMessage.hps);
                    System.out.println("Expected total hashes required for the whole chain: " + String.format("%.6f", (double) responseMessage.totalHashes));
                    System.out.println("Nonce for most recent block: " + responseMessage.recentNonce);
                    System.out.println("Chain hash: " + responseMessage.chainHash);
                }

                // If user requested to add a transaction to the blockchain
                case "1" -> {

                    // Prompt the user for difficulty
                    System.out.println("Enter difficulty > 0");
                    difficulty = Integer.parseInt(typed.readLine());

                    // Prompt the user for transaction
                    System.out.println("Enter transaction");
                    transactionData = typed.readLine();

                    // Create an add request message
                    requestMessage = new AddRequestMessage(1, difficulty, transactionData);

                    // Request the blockchain operation from server and store the value of response
                    String serverReturned = blockchain_operations(requestMessage.toString());

                    // Parse JSON response from server into NormalResponseMessage
                    NormalResponseMessage responseMessage = gson.fromJson(serverReturned, NormalResponseMessage.class);

                    // Display response to the user
                    System.out.println(responseMessage.response);
                }

                // If user requested to verify the blockchain
                case "2" -> {

                    // Create a normal request message
                    requestMessage = new NormalRequestMessage(2);

                    // Request the blockchain operation from server and store the value of response
                    String serverReturned = blockchain_operations(requestMessage.toString());

                    // Parse JSON response from server into VerificationResponseMessage
                    VerificationResponseMessage verificationResponseMessage = gson.fromJson(serverReturned, VerificationResponseMessage.class);

                    // If chain verification was successful
                    if (verificationResponseMessage.verificationOutput.equals("TRUE")) {
                        // Display success message
                        System.out.println("Chain verification: " + verificationResponseMessage.verificationOutput);
                    }
                    // If chain verification was not successful
                    else {
                        // Display error message
                        System.out.println("Chain verification: FALSE");
                        System.out.println(verificationResponseMessage.verificationOutput);
                    }

                    // Display response to the user
                    System.out.println(verificationResponseMessage.response);
                }

                // If user requested to view the blockchain
                case "3" -> {

                    System.out.println("View the Blockchain");

                    // Create a normal request message
                    requestMessage = new NormalRequestMessage(3);

                    // Request the blockchain operation from server and store the value of response
                    String serverReturned = blockchain_operations(requestMessage.toString());

                    // Server would return a JSON message here
                    System.out.println(serverReturned);
                }

                // If user requested to corrupt the blockchain
                case "4" -> {

                    System.out.println("Corrupt the Blockchain");

                    // Prompt the user for difficulty
                    System.out.println("Enter block ID of block to corrupt");
                    blockID = Integer.parseInt(typed.readLine());

                    // Prompt the user for transaction
                    System.out.println("Enter new data for block " + blockID);
                    transactionData = typed.readLine();

                    // Create a corrupt request message
                    requestMessage = new CorruptRequestMessage(4, blockID, transactionData);

                    // Request the blockchain operation from server and store the value of response
                    String serverReturned = blockchain_operations(requestMessage.toString());

                    // Parse JSON response from server into NormalResponseMessage
                    NormalResponseMessage responseMessage = gson.fromJson(serverReturned, NormalResponseMessage.class);

                    // Display response to the user
                    System.out.println(responseMessage.response);
                }

                // If user requested to hide the corruption by repairing the chain
                case "5" -> {

                    // Create a normal request message
                    requestMessage = new NormalRequestMessage(5);

                    // Request the blockchain operation from server and store the value of response
                    String serverReturned = blockchain_operations(requestMessage.toString());

                    // Parse JSON response from server into NormalResponseMessage
                    NormalResponseMessage responseMessage = gson.fromJson(serverReturned, NormalResponseMessage.class);

                    // Display response to the user
                    System.out.println(responseMessage.response);
                }

                // If user requested to exit
                case "6" -> {

                    // Halt client execution
                    System.exit(0);
                }
            }
        }
    }

    /***
     * Function to communicate with the server and perform the required
     * operation on the requested integer value by the client
     * @param user_input Input from the user containing client ID, operation and operand
     * @return Updated sum from the server
     */

    /***
     * Function to communicate with the server and perform the required
     * blockchain operation. The request message from the client would be
     * in the form of a JSON string.
     * @param jsonRequestMessage Request message from client
     * @return JSON response from server
     */
    public static String blockchain_operations(String jsonRequestMessage) {

        // Define a TCP style Socket
        Socket clientSocket = null;

        // Stores the JSON response from the server
        String serverReturned = "";

        try {
            // Creating a String object for localhost
            String localhost = "";

            // Updating clientSocket with localhost and the server port
            clientSocket = new Socket(localhost, 6789);

            // Set up "in" to read from the server socket
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Set up "out" to write to the server socket
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

            // Request to the server with the JSON request message
            out.println(jsonRequestMessage);

            // Flush to server socket
            out.flush();

            // Store the JSON response from the server
            serverReturned = in.readLine(); // read a line of data from the stream
        }
        // Handle general I/O exceptions
        catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
        }
        // Always close the socket
        finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }

        // Return the JSON response from server
        return serverReturned;
    }
}
