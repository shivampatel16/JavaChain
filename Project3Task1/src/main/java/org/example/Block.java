/**
 * Author: Shivam Patel
 * Andrew ID: shpatel
 * Email: shpatel@cmu.edu
 * Last Modified: November 1, 2022
 * File: Block.java
 * Part Of: Project3Task1
 *
 * This Java file acts as a Block for the BlockChain that would be
 * created by the BlockChain class. It has a Block constructor and
 * some of its specific functions include function to calculate hashes,
 * compute proof of work, and convert the Block object to a JSON string.
 */

// Defines the package for the Java file
package org.example;

// Imports necessary for Gson, BigInteger, MessageDisgest and NoSuchAlgorithmException
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Block {

    // Stores the index of the Block in the Blockchain
    // The first block (the so-called Genesis block) has an index of 0.
    private int index;

    // Stores the time when the Block was created
    private java.sql.Timestamp timestamp;

    // Stores the transaction on the Block
    private java.lang.String data;

    // Stores the SHA256 hash of a block's parent. This is also called a hash pointer
    private java.lang.String previousHash;

    // Stores a BigInteger value determined by a proof of work routine
    private java.math.BigInteger nonce;

    // Stores the integer value that specifies the minimum number of left most hex digits needed
    // by a proper hash. The hash is represented in hexadecimal.
    private int difficulty;


    // Constructor to initialise the values of the instance variables of the Block class
    Block (int index, java.sql.Timestamp timestamp, java.lang.String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
        this.nonce = BigInteger.valueOf(0);
    }

    /***
     * Computes the SHA256 hash value of the following:
     * index + timestamp.toString() + data + previousHash + nonce + difficulty
     * @return A String of the hexadecimal representation of the hash
     */
    // Source: Shivam Patel Project 1 Task 1 - CMU Heinz 95-702
    public java.lang.String calculateHash() {

        // String whose hash is to be found
        String hash_input = index + timestamp.toString() + data + previousHash + nonce + difficulty;

        // Source: CMU 95702 Fall 2022 Lab1-InstallationAndRaft Code
        byte[] digest = new byte[0];
        try {
            // Access MessageDigest class for SHA256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Compute the digest
            md.update(hash_input.getBytes());

            // Store digest as a byte array for further use
            digest = md.digest();
        }
        // Handles No SHA-256 Algorithm exceptions
        catch (NoSuchAlgorithmException e) {
            // Print error message in console
            System.out.println("No SHA-256 available" + e);
        }
        // Return the SHA256 hash in String form
        return bytesToHex(digest);
    }

    /***
     * Function to find a good hash. It increments the nonce until it produces a good hash.
     * This method calls calculateHash() to compute a hash of the concatenation of the index,
     * timestamp, data, previousHash, nonce, and difficulty. If the hash has the appropriate
     * number of leading hex zeroes, it is done and returns that proper hash. If the hash
     * does not have the appropriate number of leading hex zeroes, it increments the nonce by
     * 1 and tries again. It continues this process, burning electricity and CPU cycles, until
     * it gets lucky and finds a good hash.
     * @return A String with a hash that has the appropriate number of leading hex zeroes.
     *         The difficulty value is already in the block. This is the minimum number of hex 0's
     *         a proper hash must have.
     */
    public java.lang.String proofOfWork() {

        // Source to repeat a String multiple times:
        // https://stackoverflow.com/questions/1235179/simple-way-to-repeat-a-string
        String leading_zeros = new String(new char[difficulty]).replace("\0", "0");

        // Stores the calculated hash
        String hash;

        // While a good hash is found
        while (true)
        {
            // Calculate new hash
            hash = calculateHash();

            // If hash starts with the required number of leading zeros
            if (hash.startsWith(leading_zeros))
            {
                // End the while loop
                break;
            }
            // If hash did not start with the required number of leading zeros, increment nonce
            nonce = nonce.add(BigInteger.valueOf(1));
        }
        // Return the good hash
        return hash;
    }

    /***
     * This function overrides Java's toString method to convert the object of the Block
     * class to a JSON string
     * @return A JSON representation of all of this block's data is returned
     */
    public java.lang.String toString() {

        // Source to format date in Gson:
        // https://stackoverflow.com/questions/6873020/gson-date-format

        // Create a Gson object
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").create();

        // Serialize to JSON
        return gson.toJson(this);
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

    /***
     * Function to get transaction details of Block
     * @return Transaction details of Block
     */
    public java.lang.String getData() {
        return data;
    }

    /***
     * Function to get difficulty of Block
     * @return Difficulty of Block
     */
    public int getDifficulty() {
        return difficulty;
    }

    /***
     * Function to get index of Block
     * @return Index of Block
     */
    public int getIndex() {
        return index;
    }

    /***
     * Function to get nonce of Block
     * @return Nonce of Block
     */
    public java.math.BigInteger getNonce() {
        return nonce;
    }

    /***
     * Function to get hash of parent of Block
     * @return Hash of parent of Block
     */
    public java.lang.String getPreviousHash() {
        return previousHash;
    }

    /***
     * Function to get time of creation of Block
     * @return Time of creation of Block
     */
    public java.sql.Timestamp getTimestamp() {
        return timestamp;
    }

    /***
     * Function to set transaction details of Block
     * @param data Transaction details of Block
     */
    public void setData(java.lang.String data) {
        this.data = data;
    }

    /***
     * Function to set difficulty of Block
     * @param difficulty Difficulty of Block
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    /***
     * Function to set nonce of Block
     * @param nonce Nonce of Block
     */
    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    /***
     * Function to set index of Block
     * @param index Index of Block
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /***
     * Function to set hash of parent of Block
     * @param previousHash Hash of parent of Block
     */
    public void setPreviousHash(java.lang.String previousHash) {
        this.previousHash = previousHash;
    }

    /***
     * Function to set time of creation of Block
     * @param timestamp Time of creation of Block
     */
    public void setTimestamp(java.sql.Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}