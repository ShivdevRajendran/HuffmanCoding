package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class HuffmanCoding {
    private String fileName;
    private ArrayList<CharFreq> sortedCharFreqList;
    private TreeNode huffmanRoot;
    private String[] encodings;

    /**
     * Constructor used by the driver, sets filename
     * DO NOT EDIT
     * @param f The file we want to encode
     */
    public HuffmanCoding(String f) { 
        fileName = f; 
    }

    /**
     * Reads from filename character by character, and sets sortedCharFreqList
     * to a new ArrayList of CharFreq objects with frequency > 0, sorted by frequency
     */
    public void makeSortedList() {
        StdIn.setFile(fileName);

	/* Your code goes here */

    sortedCharFreqList = new ArrayList<CharFreq>();
    double[] occurences = new double[128];

    int stringLength = 0;
    while (StdIn.hasNextChar() == true) {
        char character = StdIn.readChar();
        //StdOut.println(character);
        occurences[character] = occurences[character] + 1;
        //StdOut.println( occurences[character] );
        stringLength++;
    }
    //StdOut.println(stringLength);
    for (int i = 0; i < 128; i++) {
        //char c = (char)(i+97);
        char c = (char)(i);
        //StdOut.println(c);
        if (occurences[c] > 0) {
        //StdOut.println(occurences[c]/stringLength);
        double prob = (occurences[c])/(stringLength);
        //StdOut.println(occurences[c] + " " + prob);
        CharFreq cFreq = new CharFreq(c, prob);
        sortedCharFreqList.add(cFreq);
        }
    }
    if (sortedCharFreqList.size() == 1) {
    char distinctCharacter = sortedCharFreqList.get(0).getCharacter();
    
    CharFreq distinctCharFreq =  new CharFreq(   (char)((distinctCharacter + 1) % 128)    ,   0  );
    sortedCharFreqList.add(distinctCharFreq);
    }

    Collections.sort(sortedCharFreqList);
    }

    /**
     * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
     * in huffmanRoot
     */
    public void makeTree() {
	/* Your code goes here */

        Queue<TreeNode> source = new Queue<>();
        Queue<TreeNode> target = new Queue<>();
        Queue<TreeNode> dequeued = new Queue<>();

        for (int i = 0; i < sortedCharFreqList.size(); i++) {
            TreeNode node = new TreeNode(sortedCharFreqList.get(i), null, null); //figure out nexts and stuff
            source.enqueue(node);
            //source.enqueue(sortedCharFreqList.get(i));
            //StdOut.println(sortedCharFreqList.get(i).getCharacter());
            //Node n = new Node(sortedCharFreqList.get(i) , null); 
        }

        while (source.isEmpty() == false || target.size() != 1) {
            while (dequeued.size() < 2) {
                if (target.isEmpty() == true) dequeued.enqueue(source.dequeue());
                else   
                    if (source.isEmpty() == false) {
                        if (source.peek().getData().getProbOcc() <= target.peek().getData().getProbOcc() )    dequeued.enqueue(source.dequeue());
                        //might need to check if they equal
                        else dequeued.enqueue(target.dequeue());
                    }
                    else dequeued.enqueue(target.dequeue());
            }

            TreeNode smallNode;
            if (dequeued.isEmpty() == true) {
                smallNode = null;
            }
            else { 
                smallNode = dequeued.dequeue();
            }
            TreeNode secondSmallNode;
            if (dequeued.isEmpty() == true) {
                secondSmallNode = null;
            }
            else { 
                secondSmallNode = dequeued.dequeue();
            }
            
            double probOcc1;
            if (smallNode == null) probOcc1 = 0;
            else  probOcc1 = smallNode.getData().getProbOcc();

            double probOcc2;
            if (secondSmallNode == null) probOcc2 = 0;
            else  probOcc2 = secondSmallNode.getData().getProbOcc();

            TreeNode t = new TreeNode( new CharFreq(null, probOcc1+probOcc2), smallNode, secondSmallNode);
            target.enqueue(t);
        }
        huffmanRoot = target.dequeue();

    }

    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array.
     */
    public void makeEncodings() {

	/* Your code goes here */
        encodings = new String[128];
        //String bitString = "";
        TreeNode ptr = huffmanRoot;
        /*for (int i = 0; i < encodings.length; i++) {
            char c = (char)(i);
            String s = String.valueOf(c);
            recursiveEncode(encodings, ptr, bitString);
            //encodings[i] = s;
            //StdOut.println(encodings[i]);
        }
        //recursiveEncode(encodings, ptr, bitString);
        */
        //StdOut.println(encodings);
        recursiveEncode(encodings, ptr, "");
        //recursiveEncode(ptr.getData().getCharacter(), ptr, "");//bitString);

    }

    public void recursiveEncode(String[] encodings, TreeNode ptr, String bitString) {
        if (ptr.getData().getCharacter() != null) { // || encodings[ptr.getData().getCharacter()] != null) {
            encodings[ptr.getData().getCharacter()] = bitString;
            //StdOut.println(encodings[ptr.getData().getCharacter()]);
            return;
        }
        recursiveEncode(encodings, ptr.getLeft(), bitString + "0");
        recursiveEncode(encodings, ptr.getRight(), bitString + "1");
        //return;
    


    }

    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public void encode(String encodedFile) {
        StdIn.setFile(fileName);
        /* Your code goes here */

        String bitString = "";
        while (StdIn.hasNextChar() == true){
            bitString = bitString + encodings[StdIn.readChar()];
        }
        //StdOut.print(bitString);
        writeBitString(encodedFile, bitString);

    }
    
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * DO NOT EDIT
     * 
     * @param filename The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                return;
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Using a given encoded file name, this method makes use of the readBitString method 
     * to convert the file into a bit string, then decodes the bit string using the 
     * tree, and writes it to a decoded file. 
     * 
     * @param encodedFile The file which has already been encoded by encode()
     * @param decodedFile The name of the new file we want to decode into
     */
    public void decode(String encodedFile, String decodedFile) {
        StdOut.setFile(decodedFile);

	/* Your code goes here */
        //StdIn.setFile(encodedFile);
        //while (StdIn.hasNextChar() == true) {

        //}

        String s = readBitString(encodedFile);
        TreeNode ptr = huffmanRoot;
        TreeNode base = huffmanRoot;

        for (int i = 0; i < s.length(); i++) {//each bit in encoded String
            //current = current + s.charAt(i);
            
            if (ptr.getData().getCharacter() != null) {
                StdOut.print(ptr.getData().getCharacter());
                        //System.out.print(ptr.getData().getCharacter());
                ptr = base;
                //current = "";
            
            }
            //if (s.charAt(i) == 0) {
            //System.out.println( s.substring(i, i+1) );
            if (  s.substring(i, i+1).equals("0")   ) { //== "0") {
                ptr = ptr.getLeft();
                //System.out.print(ptr.getData().getCharacter());
            }
            if (  s.substring(i, i+1).equals("1")  ){ //== "1") {
                ptr = ptr.getRight();
                //System.out.print(ptr.getData().getCharacter());
            }

//for last char
            if (i + 1 == s.length()) {
                if (ptr.getData().getCharacter() != null) {
                    StdOut.print(ptr.getData().getCharacter());
                            //System.out.print(ptr.getData().getCharacter());
                    ptr = base;
                    //current = "";
                
                }
            }
        }
        
        //System.out.println(s);

    }

    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * DO NOT EDIT
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /*
     * Getters used by the driver. 
     * DO NOT EDIT or REMOVE
     */

    public String getFileName() { 
        return fileName; 
    }

    public ArrayList<CharFreq> getSortedCharFreqList() { 
        return sortedCharFreqList; 
    }

    public TreeNode getHuffmanRoot() { 
        return huffmanRoot; 
    }

    public String[] getEncodings() { 
        return encodings; 
    }
}
