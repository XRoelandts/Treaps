import java.util.*;
public class Huffman {
    public static void main(String[] args) {
        List<String> texts = Arrays.asList("marcus fenix is a gear");

        EncodingSystem es = new EncodingSystem(texts);

        System.out.println("highestCode():");
        System.out.println(es.highestCode());
        System.out.println();

        System.out.println("shuffleCodes():");
        System.out.println(es.shuffleCodes());
        System.out.println();

        System.out.println("stats():");
        es.stats();
    }




        //inner node
        private static class Node implements Comparable<Node> {
            char   ch;
            int    freq;
            Node   left, right;

            Node(char ch, int freq) {
                this.ch   = ch;
                this.freq = freq;
            }
            Node(int freq, Node left, Node right) {
                this.ch    = '\0';
                this.freq  = freq;
                this.left  = left;
                this.right = right;
            }
            boolean isLeaf() { return left == null && right == null; }

            @Override
            public int compareTo(Node o) { return this.freq - o.freq; }
        }

        //fields
        private String            text;
        private Map<Character,Integer> freqMap  = new LinkedHashMap<>();
        private Map<Character,String>  codeMap  = new LinkedHashMap<>();
        private Node              root;
        private String            encodedText;

        //constructor
        Huffman(String text) {
            this.text = text;
            frequencyCount(text);
            buildHuffman();
            genCode();
            encodedText = encode(text);
        }

        //frequency count
        public void frequencyCount(String text) {
            freqMap.clear();
            for (char c : text.toCharArray()) {
                freqMap.merge(c, 1, Integer::sum);
            }
        }

        //build Huffman tree
        public void buildHuffman() {
            PriorityQueue<Node> heap = new PriorityQueue<>();

            for (Map.Entry<Character,Integer> e : freqMap.entrySet()) {
                heap.add(new Node(e.getKey(), e.getValue()));
            }

            // edge case: single unique character
            if (heap.size() == 1) {
                Node only = heap.poll();
                root = new Node(only.freq, only, null);
                return;
            }

            while (heap.size() > 1) {
                Node left  = heap.poll();   // lowest freq → left (0)
                Node right = heap.poll();   // next lowest  → right (1)
                heap.add(new Node(left.freq + right.freq, left, right));
            }
            root = heap.poll();
        }

        //generate codes
        public void genCode() {
            codeMap.clear();
            generateCodesHelper(root, "");
        }

        private void generateCodesHelper(Node node, String code) {
            if (node == null) return;
            if (node.isLeaf()) {
                // assign code (handle single-char edge case)
                codeMap.put(node.ch, code.isEmpty() ? "0" : code);
                return;
            }
            generateCodesHelper(node.left,  code + "0");
            generateCodesHelper(node.right, code + "1");
        }

        //encode
        public String encode(String text) {
            StringBuilder sb = new StringBuilder();
            for (char c : text.toCharArray()) {
                sb.append(codeMap.get(c));
            }
            return sb.toString();
        }

        //printStats
        public void printStats() {
            System.out.println("Frequencies:");
            for (Map.Entry<Character,Integer> e :
                    new TreeMap<>(freqMap).entrySet()) {
                char c = e.getKey();
                String label = (c == ' ') ? "(space)" : String.valueOf(c);
                System.out.println("  " + label + " : " + e.getValue());
            }
            System.out.println("Huffman Codes:");
            for (Map.Entry<Character,String> e :
                    new TreeMap<>(codeMap).entrySet()) {
                char c = e.getKey();
                String label = (c == ' ') ? "(space)" : String.valueOf(c);
                System.out.println("  " + label + ": " + e.getValue());
            }
            System.out.println("Encoded:\n  " + encodedText);
        }

        //getters
        public String getEncodedText() { return encodedText; }
    }


//  EncodingSystem

    class EncodingSystem {

        private List<String> encodedTexts = new ArrayList<>();
        private List<String> texts;
        private List<Huffman> huffmanInstances = new ArrayList<>();

        EncodingSystem(List<String> texts) {
            this.texts = texts;
            for (String t : texts) {
                Huffman h = new Huffman(t);
                huffmanInstances.add(h);
                encodedTexts.add(h.getEncodedText());
            }
        }

        public String highestCode() {
            return encodedTexts.stream()
                    .max(Comparator.comparingInt(String::length))
                    .orElse(null);
        }

        public List<String> shuffleCodes() {
            List<String> shuffled = new ArrayList<>(encodedTexts);
            Collections.shuffle(shuffled);
            return shuffled;
        }

        public void printCodes() {
            for (int i = 0; i < texts.size(); i++) {
                System.out.println("\"" + texts.get(i) + "\" => " + encodedTexts.get(i));
            }
        }

        public void stats() {
            for (int i = 0; i < texts.size(); i++) {
                System.out.println("Stats for \"" + texts.get(i) + "\"");
                huffmanInstances.get(i).printStats();
                System.out.println();
            }
        }

    }
