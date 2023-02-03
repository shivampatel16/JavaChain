/**
 * Author: Shivam Patel
 * Andrew ID: shpatel
 * Email: shpatel@cmu.edu
 * Last Modified: November 1, 2022
 * File: BlockChain.java
 * Part Of: Project3Task0
 *
 * This Java file creates a BlockChain by using the objects of the
 * Block class as its blocks. It has a BlockChain constructor and
 * its specific functions include function to add a block to the blockchain,
 * compute number of hashes per second, get Block by index, get number
 * of hashes per second, get the latest block, get time of the Block,
 * get the total difficulty of the blockchain,  get the total expected
 * hashes for the blockchain, verify the blockchain, repair the blockchain
 * if it is corrupted and finally convert the blockchain object to a JSON
 * string.
 */

// Defines the package for the Java file
package org.example;

// Imports necessary for Gson, BigInteger, MessageDisgest and NoSuchAlgorithmException,
// TimeStamp, ArrayList, and IO operations
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class BlockChain {

    // Stores the blocks of the BlockChain
    final ArrayList<Block> ds_chain;

    // Stores the SHA256 hash of the most recently added Block
    String chainHash;

    // Stores the approximate number of hashes per second on this computer
    transient int hashesPerSecond;

    // Constructor to initialise the instance variables of the BlockChain class
    // This constructor creates an empty ArrayList for Block storage. This constructor
    // sets the chain hash to the empty string and sets hashes per second to 0.
    BlockChain() {
        ds_chain = new ArrayList<>();
        chainHash = "";
        hashesPerSecond = 0;
    }

    /***
     * Function to add a new Block to the BlockChain
     * @param newBlock Block to be added to the BlockChain
     */
    public void addBlock(Block newBlock) {

        // Update chainHash to be the hash of the new block that is being added
        chainHash = newBlock.proofOfWork();

        // Add new block to the array list
        ds_chain.add(newBlock);
    }

    /***
     * This method computes exactly 2 million hashes and times how long that process
     * takes. So, hashes per second is approximated as (2 million / number of seconds).
     * It is run on start up and sets the instance variable hashesPerSecond. It uses
     * a simple string - "00000000" to hash.
     */
    public void computeHashesPerSecond() {

        // Source to calculate the run time of a program:
        // https://stackoverflow.com/questions/5204051/how-to-calculate-the-running-time-of-my-program
        long startTime = System.nanoTime();

        // Compute hashes for the string "00000000" for 2 million times
        for (int i = 1; i <= 2000000; i++) {
            computeSHA256("00000000");
        }

        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;

        // Source to convert time in nanoseconds to seconds:
        // https://mkyong.com/java/java-how-to-convert-system-nanotime-to-seconds/
        long totalTimeInSeconds = TimeUnit.SECONDS.convert(totalTime, TimeUnit.NANOSECONDS);

        // Stores the number of hashes per second
        hashesPerSecond = (int) (2000000 / totalTimeInSeconds);
    }

    /***
     * Function to return block at position i
     * @param i Position of the Block to be returned
     * @return Block at postion i
     */
    public Block getBlock(int i) {
        return ds_chain.get(i);
    }

    /***
     * Function to get the chain hash
     * @return The chain hash.
     */
    public String getChainHash() {
        return chainHash;
    }

    /***
     * Function to get the size of the chain in blocks
     * @return The size of the chain in blocks
     */
    public int getChainSize() {
        return ds_chain.size();
    }

    /***
     * Function to get hashes per second
     * @return The instance variable approximating the number of hashes per second
     */
    public int getHashesPerSecond() {
        return hashesPerSecond;
    }

    /***
     * Function to get a reference to the most recently added Block
     * @return A reference to the most recently added Block
     */
    public Block getLatestBlock() {
        return ds_chain.get(ds_chain.size() - 1);
    }

    /***
     * Function to get the current system time
     * @return The current system time
     */
    public java.sql.Timestamp getTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    /***
     * Function to compute and return the total difficulty of all blocks
     * on the chain. Each block knows its own difficulty.
     * @return The total difficulty of the chain
     */
    public int getTotalDifficulty() {

        int totalDifficulty = 0;

        for (int i = 0; i < ds_chain.size(); i++) {
            totalDifficulty = totalDifficulty + ds_chain.get(i).getDifficulty();
        }

        return totalDifficulty;
    }

    /***
     * Function to compute and return the expected number of hashes required for the entire chain
     * @return The total expected hashes for the blockchain
     */
    public double getTotalExpectedHashes() {

        // Stores the total expected hashes for the chain
        double totalExpectedHashes = 0;

        // Loop over all the blocks in the blockchain and increment the
        // count of the total expected hashes
        for (int i = 0; i < ds_chain.size(); i++) {
            totalExpectedHashes = totalExpectedHashes + Math.pow(16, ds_chain.get(i).getDifficulty());
        }

        // The total expected hashes for the chain
        return totalExpectedHashes;
    }

    /***
     * Function to check if the blockchain is valid.
     * If the chain only contains one block, the genesis block at position 0, this
     * routine computes the hash of the block and checks that the hash has the requisite
     * number of leftmost 0's (proof of work) as specified in the difficulty field. It
     * also checks that the chain hash is equal to this computed hash. If either check
     * fails, return an error message. Otherwise, return the string "TRUE". If the chain
     * has more blocks than one, begin checking from block one. Continue checking until
     * you have validated the entire chain. The first check will involve a computation of
     * a hash in Block 0 and a comparison with the hash pointer in Block 1. If they match
     * and if the proof of work is correct, go and visit the next block in the chain. At
     * the end, check that the chain hash is also correct.
     * @return "TRUE" if the chain is valid, otherwise return a string with an appropriate error message
     */
    public java.lang.String isChainValid() {

        // Loop over all the blocks in the chain
        for (int i = 0; i < ds_chain.size(); i++) {

            // Prepare string whose hash is to be calculated
            String hash_input = ds_chain.get(i).getIndex() + ds_chain.get(i).getTimestamp().toString()
                    + ds_chain.get(i).getData() + ds_chain.get(i).getPreviousHash()
                    + ds_chain.get(i).getNonce() + ds_chain.get(i).getDifficulty();

            // Compute SHA256 hash of the hash_input string prepared
            String hash = computeSHA256(hash_input);

            // Compute the number of leading zeros required in the hash
            // Source to repeat a String multiple times:
            // https://stackoverflow.com/questions/1235179/simple-way-to-repeat-a-string
            String leading_zeros_required = new String(new char[ds_chain.get(i).getDifficulty()]).replace("\0", "0");

            // If hash does not start with the required number of leading zeros
            if (!hash.startsWith(leading_zeros_required)) {

                // Return error message
                return "Improper hash on node " + i + ". Does not begin with " + leading_zeros_required;
            }

            // If chain size is greater than 1
            if (ds_chain.size() > 1) {

                // If it is not the last block
                if (i != ds_chain.size() - 1) {

                    // If hash of current block does not equal the previous hash of the next block
                    if (!hash.equals(ds_chain.get(i + 1).getPreviousHash())) {

                        // Return error message
                        return "Hash of Block " + i + " does not match with previous hash of Block " + (i + 1);
                    }
                }
                // It is the last block
                else
                {
                    // If hash of the last block does not equal to the chain hash
                    if (!hash.equals(chainHash)) {

                        // Return error message
                        return "Hash of the last Block (Block " + i + ") does not match with Chain Hash!";
                    }
                }
            }
            // If only one block is present in the chain
            else {
                // If hash of the block does not equal to chain hash
                if (!hash.equals(chainHash)) {

                    // Return error message
                    return "Hash of the last Block (Block " + i + ") does not match with Chain Hash!";
                }
            }
        }
        // Return true if the blockchain is successfully verified
        return "TRUE";
    }

    /***
     * This routine repairs the chain. It checks the hashes of each block and ensures
     * that any illegal hashes are recomputed. After this routine is run, the chain will
     * be valid. The routine does not modify any difficulty values. It computes new proof
     * of work based on the difficulty specified in the Block.
     */
    public void repairChain() {

        // Loop over every block in the chain
        for (int i = 0; i < ds_chain.size(); i++) {

            // Prepare String whose hash is to be found
            String hash_input = ds_chain.get(i).getIndex() + ds_chain.get(i).getTimestamp().toString()
                    + ds_chain.get(i).getData() + ds_chain.get(i).getPreviousHash()
                    + ds_chain.get(i).getNonce() + ds_chain.get(i).getDifficulty();

            // Compute SHA 256 hash of the hash_input
            String hash = computeSHA256(hash_input);

            // Calculate leading zeros required to be present in the hash
            // Source to repeat a String multiple times:
            // https://stackoverflow.com/questions/1235179/simple-way-to-repeat-a-string
            String leading_zeros_required = new String(new char[ds_chain.get(i).getDifficulty()]).replace("\0", "0");

            // If the chain has only one Block
            if (ds_chain.size() == 1) {

                // If the previous hash is not ""
                if (!ds_chain.get(i).getPreviousHash().equals("")) {

                    // Set previous hash of the first node to be ""
                    ds_chain.get(i).setPreviousHash("");
                }
            }

            // If the hash does not start with the required number of leading zeros
            if (!hash.startsWith(leading_zeros_required)) {

                // Computing new proof of work
                // Set initial nonce of the Block to be 0
                ds_chain.get(i).setNonce(BigInteger.valueOf(0));

                // Until the good hash is not found
                while (true)
                {
                    // Prepare new hash input String
                    String new_hash_input = ds_chain.get(i).getIndex()
                            + ds_chain.get(i).getTimestamp().toString()
                            + ds_chain.get(i).getData()
                            + ds_chain.get(i).getPreviousHash()
                            + ds_chain.get(i).getNonce()
                            + ds_chain.get(i).getDifficulty();

                    // Compute new hash
                    hash = computeSHA256(new_hash_input);

                    // If the new hash starts with the required number of leading zeros
                    if (hash.startsWith(leading_zeros_required))
                    {
                        // End the while loop
                        break;
                    }

                    // If the new hash does not start with the required number of leading zeros,
                    // increment the nonce by 1.
                    ds_chain.get(i).setNonce(ds_chain.get(i).getNonce().add(BigInteger.valueOf(1)));
                }
            }

            // Prepare hash input after the above updates
            hash_input = ds_chain.get(i).getIndex() + ds_chain.get(i).getTimestamp().toString()
                    + ds_chain.get(i).getData() + ds_chain.get(i).getPreviousHash()
                    + ds_chain.get(i).getNonce() + ds_chain.get(i).getDifficulty();

            // Compute SHA256 hash
            hash = computeSHA256(hash_input);

            // If the chain size is greater than 1
            if (ds_chain.size() > 1) {

                // If the block is not the last block
                if (i != ds_chain.size() - 1) {

                    // If the hash of the current block is not equal to previous hash of the next block
                    if (!hash.equals(ds_chain.get(i + 1).getPreviousHash())) {

                        // Update the previous hash of the next block
                        ds_chain.get(i + 1).setPreviousHash(hash);
                    }
                }
                // If the block is the last block
                else {

                    // If the hash of the last block is not equal to chain hash
                    if (!hash.equals(chainHash)) {

                        // Update chain hash
                        chainHash = hash;
                    }
                }
            }

            // If only one block is present in the chain
            else {

                // If hash of the block is not equal to chain hash
                if (!hash.equals(chainHash)) {

                    // Update chain hash
                    chainHash = hash;
                }
            }
        }
    }

    /***
     * This method uses the toString method defined on each individual block
     * to convert the BlockChain object to a JSON string
     * @return A String representation of the entire chain
     */
    public java.lang.String toString() {
        // Create a Gson object
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

        // Serialize to JSON
        return gson.toJson(this);
    }

    /***
     * computes the SHA256 hash value of the string passed
     * into the function, converts the hash into hexadecimal and
     * return the hash value
     * @param input String input whose hash values are to be computed
     * @return SHA256 hash value of the input string in hexadecimal form
     */
    public String computeSHA256(String input) {

        String hash = "";
        // Source: CMU 95702 Fall 2022 Lab1-InstallationAndRaft Code
        try {
            // Access MessageDigest class for SHA256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Compute the digest
            md.update(input.getBytes());

            // Store digest as a byte array for further use
            byte[] digest = md.digest();

            hash = bytesToHex(digest);
        }
        // Handles No SHA-256 Algorithm exceptions
        catch (NoSuchAlgorithmException e) {
            // Print error message in console
            System.out.println("No SHA-256 available" + e);
        }

        return hash;
    }

    // Code to convert from byte array to hexadecimal String
    // Source: https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /***
     * Function to convert a byte array to hexadecimal String
     * @param bytes Byte array to be converted to hexadecimal String
     * @return Hexadecimal notation (in String form) of the input byte array
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }


    public static void main(String[] args) {

        // Create a new object of the BlockChain
        BlockChain blockChain = new BlockChain();

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

        // Prompt to the user
        System.out.println("0. View basic blockchain status.\n" +
                "1. Add a transaction to the blockchain.\n" +
                "2. Verify the blockchain.\n" +
                "3. View the blockchain.\n" +
                "4. Corrupt the chain.\n" +
                "5. Hide the corruption by repairing the chain.\n" +
                "6. Exit");

        // Create a Scanner object
        Scanner s = new Scanner(System.in);

        // Get first input from the user
        int user_input = s.nextInt();

        // Until the user does not input 6
        while (user_input != 6) {

            // If user requested to view the status of the basic BlockChain
            if (user_input == 0) {

                // Displays the necessary details
                System.out.println("Current size of chain: " + blockChain.getChainSize());
                System.out.println("Difficulty of most recent block: " + blockChain.getLatestBlock().getDifficulty());
                System.out.println("Total difficulty for all blocks: " + blockChain.getTotalDifficulty());
                System.out.println("Approximate hashes per second on this machine: " + blockChain.getHashesPerSecond());
                System.out.println("Expected total hashes required for the whole chain: " + String.format("%.6f", blockChain.getTotalExpectedHashes()));
                System.out.println("Nonce for most recent block: " + blockChain.getLatestBlock().getNonce());
                System.out.println("Chain hash: " + blockChain.getChainHash());
            }

            // If user requested to add a transaction to the BlockChain
            else if (user_input == 1) {

                // Request for difficulty of the new Block
                System.out.println("Enter difficulty > 0");
                int difficulty = s.nextInt();

                // Request for transaction of the new Block
                System.out.println("Enter transaction");
                s.nextLine();
                String transaction = s.nextLine();

                // Create new Block
                Block newBlock = new Block(blockChain.getChainSize(), blockChain.getTime(), transaction, difficulty);

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

                // Display time required to add Block to the user
                System.out.println("Total execution time to add this block was " + totalTimeInMilliSeconds + " milliseconds");
            }

            // If user requested to verify the Blockchain
            else if (user_input == 2) {

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

                // Display time required to verify the chain to the user
                System.out.println("Total execution time to verify the chain was " + totalTimeInMilliSeconds + " milliseconds");
            }

            // If user requested to view the BlockChain
            else if (user_input == 3) {

                System.out.println("View the Blockchain");

                // Display BlockChain in JSON format
                System.out.println(blockChain);
            }

            // If user requested to corrupt the BlockChain
            else if (user_input == 4) {

                System.out.println("Corrupt the Blockchain");

                // Get ID of Block to corrupt
                System.out.println("Enter block ID of block to corrupt");
                int blockID = s.nextInt();

                // Enter new data of Block
                System.out.println("Enter new data for block " + blockID);
                s.nextLine();
                String newData = s.nextLine();

                // Update new data of Block in the chain
                blockChain.getBlock(blockID).setData(newData);

                // Update user about the corruption
                System.out.println("Block " + blockID + " now holds " + blockChain.getBlock(blockID).getData());
            }

            // If user requested to hide the corruption in the BlockChain by repairing the chain
            else if (user_input == 5) {

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

                // Display time required to repair the chain to the user
                System.out.println("Total execution time required to repair the chain was " + totalTimeInMilliSeconds + " milliseconds");
            }

            // Prompt to user for next input
            System.out.println("0. View basic blockchain status.\n" +
                    "1. Add a transaction to the blockchain.\n" +
                    "2. Verify the blockchain.\n" +
                    "3. View the blockchain.\n" +
                    "4. Corrupt the chain.\n" +
                    "5. Hide the corruption by repairing the chain.\n" +
                    "6. Exit");

            // Get next input from user
            user_input = s.nextInt();
        }


        /*
        Timing Data and Analysis

        - As the difficulty of the new blocks increases, the time to add blocks (addBlock())
        significantly increases. I see an exponential increase in the approximate time taken to
        add blocks with increasing difficulty. However, the time to verify the chain (isChainValid())
        remains almost the same, no matter how large is the maximum difficulty in
        the blockchain. Contrary to verifying the chain, repairing the chain (chainRepair())
        takes an increasingly large amount of time with the increasing difficulties.


        1. Time Analysis for addBlock() with increasing difficulties

        - Approximate run times for addBlock() with increasing difficulties. As the difficulties
        of the newly added blocks increases, the time taken to add the blocks to the blockchain
        increases exponentially as well. This is because, finding the proof of work and the good
        hash gets too hard for the system. With just a difficulty of 7, the expected hashes to be
        performed becomes 268,435,456. This is huge! And hence, the time taken to add the blocks
        is huge as well. For a difficulty of 8, the expected hashes count is 4,294,967,296; however,
        my computer is not capable to perform so many hashes and hence it crashes.

        Difficulty 	Approximate Time
        1		    2 milliseconds
        2		    5 milliseconds
        3		    27 milliseconds
        4		    85 milliseconds
        5		    1174 milliseconds
        6		    30947 milliseconds
        7 		    111800 milliseconds
        8	        My system crashes! The program got hung. So, I did not try further.


        2. Time Analysis for isChainValid() with increasing difficulties

        - Approximate run time for isChainValid() with the following max difficulties in the
        blockchain. The time taken to validate a chain remains almost the same with increasing
        difficulties from 1 to 7. This is because, we do not have to compute the proof of work
        here with the increasing difficulties, rather, we just need to use it to find one hash
        and compare that with the difficulty. This is a lot simpler and hence validating chain
        does not take a lot of time even with the increasing difficulties.

        Max Difficulty		Approximate Time
        1			        0 milliseconds
        2			        0 milliseconds
        3			        0 milliseconds
        4			        0 milliseconds
        5			        0 milliseconds
        6			        0 milliseconds
        7			        0 milliseconds
        8 			        My system crashes! The program got hung. So, I did not try further.


        3. Time Analysis for chainRepair() with increasing difficulties

        -	Approximate run time for chainRepair() when blocks with the following maximum
        difficulties were present in the blockchain and the blockchain was corrupted and repaired.
        As the maximum difficulty in the blockchain increases, the time to repair the chain (after
        it gets corrupted) increases significantly as well. As with the addBlock(), I see an
        exponential increase in time here as well. It is in line with addBlock(), where, to repair
        the chain, the program will have to compute the proof of work and find the good hash. It
        requires computing a lot more hashes (as described in the analysis of addBlock()) and thus
        add to the exponential time.

        Maximum Difficulty		Approximate Time
        1				        1 milliseconds
        2				        3 milliseconds
        3				        12 milliseconds
        4				        17 milliseconds
        5				        694 milliseconds
        6				        32097 milliseconds
        7				        200187 milliseconds
        8 				        My system crashes! The program got hung. So, I did not try further.

        -	After analysing the approximate run times, it is clear for the fact mentioned in class,
        “We give easy problems to the good guys and hard problems to the bad guys.” The easy problems
        are solved quickly and the hard problems take a lot of time to get solved.

         */
    }
}