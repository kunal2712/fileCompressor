import java.io.*;
import java.util.PriorityQueue;
import java.util.HashMap;

class HuffmanNode implements Comparable<HuffmanNode> {
    int data;
    char c;
    HuffmanNode left, right;

    public int compareTo(HuffmanNode node) {
        return data - node.data;
    }
}

public class HuffmanCompression {

    private static HashMap<Character, String> huffmanCodes = new HashMap<>();

    public static void buildHuffmanTree(HashMap<Character, Integer> frequencyMap) {
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>();

        for (char c : frequencyMap.keySet()) {
            HuffmanNode node = new HuffmanNode();
            node.c = c;
            node.data = frequencyMap.get(c);
            node.left = null;
            node.right = null;
            priorityQueue.add(node);
        }

        while (priorityQueue.size() > 1) {
            HuffmanNode x = priorityQueue.poll();
            HuffmanNode y = priorityQueue.poll();
            HuffmanNode sum = new HuffmanNode();
            sum.data = x.data + y.data;
            sum.c = '-';
            sum.left = x;
            sum.right = y;
            priorityQueue.add(sum);
        }

        buildHuffmanCodes(priorityQueue.peek(), "");
    }

    public static void buildHuffmanCodes(HuffmanNode root, String code) {
        if (root == null) {
            return;
        }
        if (root.c != '-') {
            huffmanCodes.put(root.c, code);
        }
        buildHuffmanCodes(root.left, code + "0");
        buildHuffmanCodes(root.right, code + "1");
    }

    public static void compress(String inputFilePath, String outputFilePath) {
        try (FileInputStream fis = new FileInputStream(inputFilePath);
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFilePath))) {

            HashMap<Character, Integer> frequencyMap = new HashMap<>();
            int ch;
            while ((ch = fis.read()) != -1) {
                char character = (char) ch;
                frequencyMap.put(character, frequencyMap.getOrDefault(character, 0) + 1);
            }

            buildHuffmanTree(frequencyMap);

            oos.writeObject(huffmanCodes);
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void decompress(String inputFilePath, String outputFilePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFilePath));
                FileOutputStream fos = new FileOutputStream(outputFilePath)) {

            HashMap<Character, String> huffmanCodes = (HashMap<Character, String>) ois.readObject();
            StringBuilder encodedBits = new StringBuilder();
            int bit;
            while ((bit = ois.read()) != -1) {
                encodedBits.append((char) bit);
            }

            HashMap<String, Character> reverseMapping = new HashMap<>();
            for (char c : huffmanCodes.keySet()) {
                reverseMapping.put(huffmanCodes.get(c), c);
            }

            StringBuilder decodedText = new StringBuilder();
            String currentCode = "";
            for (char bitChar : encodedBits.toString().toCharArray()) {
                currentCode += bitChar;
                if (reverseMapping.containsKey(currentCode)) {
                    decodedText.append(reverseMapping.get(currentCode));
                    currentCode = "";
                }
            }

            fos.write(decodedText.toString().getBytes());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String inputFile = "text.txt";
        String compressedFile = "compressed.bin";
        String decompressedFile = "decompressed.txt";

        // Compression
        compress(inputFile, compressedFile);
        System.out.println("File compressed successfully.");

        // Decompression
        decompress(compressedFile, decompressedFile);
        System.out.println("File decompressed successfully.");
    }
}
