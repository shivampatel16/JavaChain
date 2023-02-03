/**
 * Author: Shivam Patel
 * Andrew ID: shpatel
 * Email: shpatel@cmu.edu
 * Last Modified: November 1, 2022
 * File: ServerTCP.java
 * Part Of: Project3Task1
 *
 * This Java file acts as the TCP Server who will get requests for the TCP Client
 * in the form on JSON strings and perform the necessary blockchain operations.
 * The blockchain will exist on the server. It will be constructed there and the
 * client will make requests for operations over a TCP socket. If the client exits,
 * the server will still handle new requests with the existing blockchain intact.
 */

// Defines the package for the Java file
package org.example;

// imports required for TCP/IP, IO operations and Gson
import com.google.gson.Gson;
import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ServerTCP {

    // Stores an array list of blocks in the blockchain
    static BlockChain blockChain = new BlockChain();

    // Stores the JSON response to be sent to the client
    static String json_response = null;

    // Stores the ResponseMessage to be converted to JSON which would be later sent to the client
    static ResponseMessage responseMessage;

    // Stores the response which is a part of the overall response message
    static String response;

    // Create a Gson object
    static Gson gson = new Gson();

    public static void main(String[] args) {

        // Define a TCP style Socket
        Socket clientSocket = null;

        // Define a TCP style ServerSocket
        ServerSocket listenSocket;
        try {

            // Hard coded port for the server (as suggest on Piazza)
            int serverPort = 6789;

            // Create a new server socket
            listenSocket = new ServerSocket(serverPort);

            // Create the first Block, called the genesis Block
            Block genesis = new Block(0, blockChain.getTime(), "Genesis", 2);

            // Set the previous hash of the genesis block to be an empty String
            genesis.setPreviousHash("");

            // Compute the hashes per second on this system
            blockChain.computeHashesPerSecond();

            // Update chain hash by the hash of the genesis Block
            blockChain.chainHash = genesis.proofOfWork();

            // add Genesis block to chain
            blockChain.ds_chain.add(genesis);

            System.out.println("Blockchain server running");
            System.out.println("We have a visitor");

            /*
             * Forever,
             *   read a line from the socket
             *   print it to the console
             *   echo it (i.e. write it) back to the client
             */
            while (true) {
                /*
                 * Block waiting for a new connection request from a client.
                 * When the request is received, "accept" it, and the rest
                 * the tcp protocol handshake will then take place, making
                 * the socket ready for reading and writing.
                 */
                clientSocket = listenSocket.accept();
                // If we get here, then we are now connected to a client.

                // Set up "in" to read from the client socket
                Scanner in;
                in = new Scanner(clientSocket.getInputStream());

                // Set up "out" to write to the client socket
                PrintWriter out;
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

                // Get the input from the client in JSON format
                String user_input = in.nextLine();

                // Convert JSON client request into a RequestMessage format
                RequestMessage requestMessage = gson.fromJson(user_input, RequestMessage.class);

                // If user requested to view the status of the basic BlockChain
                if (requestMessage.operation == 0) {

                    // Form the JSON response by calling viewBlockChainStatus()
                    json_response = viewBlockChainStatus(requestMessage.operation);

                    // Display response to user
                    System.out.println("Response : " + json_response);
                }

                // If user requested to add a transaction to the BlockChain
                else if (requestMessage.operation == 1) {
                    System.out.println("Adding a block");

                    // Form a AddRequestMessage from the client request
                    AddRequestMessage addRequestMessage = gson.fromJson(user_input, AddRequestMessage.class);

                    // Form the JSON response by calling addTransaction()
                    json_response = addTransaction(addRequestMessage);

                    // Display response to user
                    System.out.println("..." + json_response);
                }

                // If user requested to verify the Blockchain
                else if (requestMessage.operation == 2) {
                    System.out.println("Verifying entire chain");

                    // Form a NormalRequestMessage from the client request
                    NormalRequestMessage normalRequestMessage = gson.fromJson(user_input, NormalRequestMessage.class);

                    // Form the JSON response by calling verifyBlockChain()
                    json_response = verifyBlockChain(normalRequestMessage);
                }

                // If user requested to view the BlockChain
                else if (requestMessage.operation == 3) {
                    System.out.println("View the Blockchain");

                    // Form the JSON response by calling viewBlockChain()
                    json_response = viewBlockChain();

                    // Display response to user
                    System.out.println("Setting response to " + json_response);
                }

                // If user requested to corrupt the BlockChain
                else if (requestMessage.operation == 4) {
                    System.out.println("Corrupt the Blockchain");

                    // Form a CorruptRequestMessage from the client request
                    CorruptRequestMessage corruptRequestMessage = gson.fromJson(user_input, CorruptRequestMessage.class);

                    // Form the JSON response by calling corruptBlockChain()
                    json_response = corruptBlockChain(corruptRequestMessage);

                    // Display response to user
                    System.out.println("Setting response to " + response);
                }

                // If user requested to hide the corruption in the BlockChain by repairing the chain
                else if (requestMessage.operation == 5) {
                    System.out.println("Repairing the entire chain");

                    // Form a NormalRequestMessage from the client request
                    NormalRequestMessage normalRequestMessage = gson.fromJson(user_input, NormalRequestMessage.class);

                    // Form the JSON response by calling repairBlockChain()
                    json_response = repairBlockChain(normalRequestMessage);

                    // Display response to user
                    System.out.println("Setting response to " + response);
                }

                // Reply the JSON response to the client
                out.println(json_response);

                // Flush to client socket
                out.flush();
            }
        }
        // Handle IO exceptions
        catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
            // If quitting (typically by you sending quit signal) clean up sockets
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
    }

    /***
     * Function to view the current blockchain status
     * @param requestMessage_operation requested operation number from the client
     * @return JSON response
     */
    public static String viewBlockChainStatus(int requestMessage_operation) {

        // Create a StatusResponseMessage object
        ResponseMessage responseMessage = new StatusResponseMessage(
                requestMessage_operation,
                blockChain.getChainSize(),
                blockChain.getChainHash(),
                (int) blockChain.getTotalExpectedHashes(),
                blockChain.getTotalDifficulty(),
                blockChain.getLatestBlock().getNonce(),
                blockChain.getLatestBlock().getDifficulty(),
                blockChain.getHashesPerSecond());

        // Convert the object to JSON
        json_response = gson.toJson(responseMessage);

        // JSON response to client
        return json_response;
    }

    /***
     * Function to add block to the blockchain
     * @param addRequestMessage AddRequestMessage from client
     * @return JSON response
     */
    public static String addTransaction(AddRequestMessage addRequestMessage) {

        // Create new Block
        Block newBlock = new Block(blockChain.getChainSize(), blockChain.getTime(),
                addRequestMessage.transactionData, addRequestMessage.difficulty);

        // Set previous hash of the new Block to be the chain hash
        newBlock.setPreviousHash(blockChain.getChainHash());

        // Source to calculate the run time of a program:
        // https://stackoverflow.com/questions/5204051/how-to-calculate-the-running-time-of-my-program
        // Start time counter
        long startTime = System.nanoTime();

        // Add Block to BlockChain
        blockChain.addBlock(newBlock);

        // End time counter
        long endTime = System.nanoTime();

        // Calculate total time
        long totalTime = endTime - startTime;

        // Source to convert time in nanoseconds to milliseconds:
        // https://stackoverflow.com/questions/4300653/conversion-of-nanoseconds-to-milliseconds-and-nanoseconds-999999-in-java
        long totalTimeInMilliSeconds = TimeUnit.MILLISECONDS.convert(totalTime, TimeUnit.NANOSECONDS);

        // Create response string
        response = "Total execution time to add this block was " + totalTimeInMilliSeconds + " milliseconds";

        System.out.println("Setting response to " + response);

        // Create a NormalResponseMessage
        responseMessage = new NormalResponseMessage(addRequestMessage.operation, response);

        // Convert the object to JSON
        json_response = gson.toJson(responseMessage);

        // JSON response to client
        return json_response;
    }

    /***
     * Function to verify blockchain
     * @param normalRequestMessage NormalRequestMessage from client
     * @return JSON response
     */
    public static String verifyBlockChain(NormalRequestMessage normalRequestMessage) {
        // Source to calculate the run time of a program:
        // https://stackoverflow.com/questions/5204051/how-to-calculate-the-running-time-of-my-program
        // Start time counter
        long startTime = System.nanoTime();

        // Compute chain verification result
        String chainVerificationResult = blockChain.isChainValid();

        // End time counter
        long endTime = System.nanoTime();

        // Compute total time required
        long totalTime = endTime - startTime;

        // Source to convert time in nanoseconds to milliseconds:
        // https://stackoverflow.com/questions/4300653/conversion-of-nanoseconds-to-milliseconds-and-nanoseconds-999999-in-java
        long totalTimeInMilliSeconds = TimeUnit.MILLISECONDS.convert(totalTime, TimeUnit.NANOSECONDS);

        // If the result of chain verification is true
        if (chainVerificationResult.equals("TRUE")) {

            // Display success message to user
            System.out.println("Chain verification: " + chainVerificationResult);
        }

        // If the result of chain verification is false
        else {

            // Display error message to user
            System.out.println("Chain verification: FALSE");
            System.out.println(chainVerificationResult);
        }

        // Define response message
        response = "Total execution time to verify the chain was " + totalTimeInMilliSeconds + " milliseconds";

        // Display time required to verify to user
        System.out.println("Total execution time required to verify the chain was " + totalTimeInMilliSeconds);

        // Display response to user
        System.out.println("Setting response to " + response);

        // Create a VerificationResponseMessage
        responseMessage = new VerificationResponseMessage(normalRequestMessage.operation, response, chainVerificationResult);

        // Convert the object to JSON
        json_response = gson.toJson(responseMessage);

        // JSON response to client
        return json_response;
    }

    /***
     * Function to view blockchain
     * @return JSON response
     */
    public static String viewBlockChain() {

        // Convert blockCahin object to JSON string format
        json_response = blockChain.toString(); // would be a json message

        // JSON response to client
        return json_response;
    }

    /***
     * Function to corrupt blockchain
     * @param corruptRequestMessage CorruptRequestMessage from client
     * @return JSON response
     */
    public static String corruptBlockChain(CorruptRequestMessage corruptRequestMessage) {

        // Stores block ID of the Block to corrupt
        int blockID = corruptRequestMessage.blockID;

        // Stores corrupted data to be stored in the Block
        String newData = corruptRequestMessage.transactionData;

        // Corrupt block
        blockChain.getBlock(blockID).setData(newData);

        // Define response message
        response = "Block " + blockID + " now holds " + blockChain.getBlock(blockID).getData();

        // Display response to user
        System.out.println(response);

        // Create a NormalResponseMessage
        responseMessage = new NormalResponseMessage(corruptRequestMessage.operation, response);

        // Convert the object to JSON
        json_response = gson.toJson(responseMessage);

        // JSON response to client
        return json_response;
    }

    /***
     * Function to repaid blockchain
     * @param normalRequestMessage NormalRequestMessage from client
     * @return JSON response
     */
    public static String repairBlockChain(NormalRequestMessage normalRequestMessage) {
        // Source to calculate the run time of a program:
        // https://stackoverflow.com/questions/5204051/how-to-calculate-the-running-time-of-my-program
        // Start time counter
        long startTime = System.nanoTime();

        // Repair block chain
        blockChain.repairChain();

        // End time counter
        long endTime = System.nanoTime();

        // Compute total time required to repair the chain
        long totalTime = endTime - startTime;

        // Source to convert time in nanoseconds to milliseconds:
        // https://stackoverflow.com/questions/4300653/conversion-of-nanoseconds-to-milliseconds-and-nanoseconds-999999-in-java
        long totalTimeInMilliSeconds = TimeUnit.MILLISECONDS.convert(totalTime, TimeUnit.NANOSECONDS);

        // Define response message
        response = "Total execution time required to repair the chain was " + totalTimeInMilliSeconds + " milliseconds";

        // Create a NormalResponseMessage
        responseMessage = new NormalResponseMessage(normalRequestMessage.operation, response);

        // Convert the object to JSON
        json_response = gson.toJson(responseMessage);

        // JSON response to client
        return json_response;
    }
}
