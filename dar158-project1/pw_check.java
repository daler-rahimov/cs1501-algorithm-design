
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * 9/22/16
 *
 * @author Daler
 */
public class pw_check {

    static HashSet<String> dictionaryTable = new HashSet<>();
    static DLB dictionaryTable2 = new DLB(); // dictionary table is 500 common word in English
    static HashMap<String, String> losgestMachedPrefix = new HashMap<>();// 10 closest prefixes mached in all_password.txt value+char char-is length of prefix 
    static int count = 0;

    static String fileNameAllPass = "/all_passwords.txt";
    static String fileNameDictionary = "/dictionary.txt";

    static char[] word = new char[5];
    static char[] allAllowedChar = "abcdefghijklmnopqrstuvwxyz1234567890!@$^_*".toCharArray();
    static char[] allAllowedCharCopy = "abc12@!".toCharArray();// for debugging 
    static String currentAbsolutePath = System.getProperty("user.dir");

    public static void main(String[] args) throws IOException {
        if (args.length == 0 || !args[0].equals("-find") && !args[0].equals("-check")) {
            System.out.println("Illegal argument : java pw_check [ -find | -check ] ");
            System.exit(0);
        }
        switch (args[0]) {
            case "-find":
                findAllPasswords();
                break;
            case "-check":
                if (args.length < 2) {
                    System.out.println("Illegal argument : java pw_check  -check WORD ");
                    System.exit(0);
                }
                checkPassword(args[1]);
                break;
        }
    }

    public static void findAllPasswords() throws IOException {
        long startTime = System.currentTimeMillis();
        loadDictionary();
        genPasswords();

        long endTime = System.currentTimeMillis();
        System.out.println("count: " + count + " time elapsed: " + (endTime - startTime));
    }

    public static void checkPassword(String searchText) throws IOException {
        Scanner scan = new Scanner(System.in);
        File file = new File(currentAbsolutePath + fileNameAllPass);
        if (!file.exists() && file.isDirectory()) {
            System.out.println("all_passwords.txt not found, Run fist with -find arg");
            System.exit(0);
        }
        DecimalFormat decFormat = new DecimalFormat("00");
        int lineNumber = 0;
        String line, timeToCrack, genPass;
        boolean isPassFound = false;
        do {
            Scanner scanner = new Scanner(file);
            //now read the file line by line...
            while (scanner.hasNextLine()) {
                lineNumber++;
                line = scanner.nextLine();
                timeToCrack = line.substring(6, line.length());
                genPass = line.substring(0, 5);
                if (genPass.equals(searchText)) {
                    System.out.println(searchText + " is valid password, time to crack: "
                            + timeToCrack + "ms");
                    isPassFound = true;
                    break;
                }
                checkPrefix(genPass, timeToCrack, searchText);
            }
            if (!isPassFound) {
                System.out.println(searchText + " is invalid password, posible valid passwords: ");
                Set set = losgestMachedPrefix.entrySet();
                Iterator iterator = set.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    Map.Entry mentry = (Map.Entry) iterator.next();
                    System.out.print(i + ")" + mentry.getKey() + ", time to crack: " + mentry.getValue() + "ms");
                    System.out.println();
                    i++;
                }
            }

            System.out.print("Enter password to check or \"q\" to exit>");
            searchText = scan.next();
        } while (!searchText.equals("q"));
    }

    private static void checkPrefix(String word, String timeToCrack, String searchStr) {
        int prefixLength;
        for (prefixLength = 0; prefixLength < searchStr.length() && word.charAt(prefixLength) == searchStr.charAt(prefixLength); prefixLength++) {
        }
        if (losgestMachedPrefix.isEmpty() || losgestMachedPrefix.size() < 10) {
            losgestMachedPrefix.put(word + prefixLength, timeToCrack);//0 is length of prefix mached;
            return;
        }

        //if hash table has key that is smaller than i put it to table
        Set set = losgestMachedPrefix.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry) iterator.next();
            char assPrefixLengthChar = ((String) mentry.getKey()).charAt(5);
            int assPrefixLength = Character.getNumericValue(assPrefixLengthChar);
            if (assPrefixLength < prefixLength) {
                if (losgestMachedPrefix.size() > 10) {
                    losgestMachedPrefix.remove((String) mentry.getKey());
                }
                losgestMachedPrefix.put(word + prefixLength, timeToCrack);
                return;
            }
        }
    }

    private static void genPasswords() throws FileNotFoundException, UnsupportedEncodingException {
        long startTime = System.currentTimeMillis();
        PrintWriter fileWriter;
        fileWriter = new PrintWriter(currentAbsolutePath + fileNameAllPass, "UTF-8");

        for (int i = 0; i < allAllowedChar.length; i++) {
            word[0] = allAllowedChar[i];
            for (int j = 0; j < allAllowedChar.length; j++) {
                word[1] = allAllowedChar[j];
                for (int f = 0; f < allAllowedChar.length; f++) {
                    word[2] = allAllowedChar[f];
                    for (int g = 0; g < allAllowedChar.length; g++) {
                        word[3] = allAllowedChar[g];          //// put it back to 3
                        for (int t = 0; t < allAllowedChar.length; t++) {
                            word[4] = allAllowedChar[t];
                            if (isValidPassword(word)) {
                                count++;
                                fileWriter.println(new String(word) + "," + (System.currentTimeMillis() - startTime));
                            }
                        }
                    }
                }
            }
        }// first loop 

        fileWriter.close();
    }

    public static boolean isValidPassword(char[] arr) {
        if (numberOfSymbols(arr, "abcdefghijklmnopqrstuvwxyz") > 3) {
            return false;
        } else if (numberOfSymbols(arr, "1234567890") > 2) {
            return false;
        } else if (numberOfSymbols(arr, "!@$^_*") > 2) {
            return false;
        } else if (dictionaryTable.contains(new String(arr))) {
            return false;
        } else if (isAnyWordInDictionaryIsSubstringOf(new String(arr))) {
            return false;
        } else if (isRaplacedWithSpesialChars(new String(arr))) {
            return false;
        } else {
            return true;
        }
    }

    /**
     *
     * @param arr: char array where to look for char given in str
     * @param str: look for chars in this string
     * @return number of symbols in arr
     */
    public static int numberOfSymbols(char[] arr, String str) {
        int symbolCount = 0;
        for (int i = 0; i < arr.length; i++) {
            if (str.indexOf(arr[i]) != -1) {
                symbolCount++;
            }
        }
        return symbolCount;
    }

public static void loadDictionary() throws FileNotFoundException, IOException {
        BufferedReader in;
        in = new BufferedReader(new FileReader(currentAbsolutePath + fileNameDictionary));
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.length() <= 5) {
                dictionaryTable.add(line);
            }
        }
        in.close();
//        System.out.println(dictionaryTable.toString());
    }

    public static boolean isAnyWordInDictionaryIsSubstringOf(String str) {

        Iterator<String> iterator = dictionaryTable.iterator();
        
        while (iterator.hasNext()) {
            if (str.contains(iterator.next())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isRaplacedWithSpesialChars(String string) {
        Iterator<String> iterator = dictionaryTable.iterator();
        String replacedStr;
        while (iterator.hasNext()) {
            String dictWord = iterator.next();
            if (string.contains(dictWord)) {
                return true;
            }
            for (int i = 0; i < dictWord.length(); i++) {
                switch (dictWord.charAt(i)) {
                    case 't':
                        replacedStr = dictWord.replace('t', '7');
                        if (string.contains(replacedStr)) {
                            return true;
                        }
                        break;
                    case 'a':
                        replacedStr = dictWord.replace('a', '4');
                        if (string.contains(replacedStr)) {
                            return true;
                        }
                        break;
                    case 'o':
                        replacedStr = dictWord.replace('o', '0');
                        if (string.contains(replacedStr)) {
                            return true;
                        }
                        break;
                    case 'e':
                        replacedStr = dictWord.replace('e', '3');
                        if (string.contains(replacedStr)) {
                            return true;
                        }
                        break;
                    case 'i':
                        replacedStr = dictWord.replace('i', '1');
                        if (string.contains(replacedStr)) {
                            return true;
                        }
                        break;
                    case 'l':
                        replacedStr = dictWord.replace('l', '1');
                        if (string.contains(replacedStr)) {
                            return true;
                        }
                        break;
                    case 's':
                        replacedStr = dictWord.replace('s', '$');
                        if (string.contains(replacedStr)) {
                            return true;
                        }
                        break;
                }
            }
        }
        return false;
    }

    /**
     * Only for debugging purposes
     *
     * @param dlb
     */
    public static void printAllDLB(DLB dlb) {
        Queue<String> q = new Queue<String>();
        dlb.collect(dlb.rootNode, "", q);
        int size = q.size();
        for (int i = 0; i < size; i++) {
            System.out.println(i + ") " + q.dequeue());
        }
    }

    //********************inner classes ************************//
    public static class DLB {

        final static char END_OF_WORD_CHAR = '&';
        Node rootNode = null; //
        int wordCount; // how many words does dlb contian 

        //Constractors
        public DLB() {
        }

        //METHODS
        /**
         * return false if symbol table does not contain given word
         *
         * @param s
         * @return
         */
        boolean contains(String s) {
            s = s + END_OF_WORD_CHAR;
            if (rootNode == null || rootNode.list == null) {
                return false;
            }
            Node iterNode = rootNode;
            for (int i = 0; i < s.length(); i++) {
                if (iterNode == null || !iterNode.getList().contains(s.charAt(i))) {//if in any level char does not exist return false
                    return false;
                }
                iterNode = iterNode.list.getNodeWithChar(s.charAt(i)).childNode;// get the node where this letter is pinting to
            }
            return true;
        }

        /**
         * Insert a word to the symbol table
         *
         * @param s
         * @return
         */
        public boolean insert(String s) {
            if (contains(s)) {
                return false;
            }
            s = s + END_OF_WORD_CHAR;
            wordCount++;
            int i; // last inserted index of string 
            LinkedList newList;
            if (this.rootNode == null) {// first word entered to the DLB
                Node trieNode;
                newList = new LinkedList(new Node(s.charAt(0)));
                trieNode = new Node(newList);
                this.rootNode = trieNode;
                trieNode = this.rootNode.getList().getNodeWithChar(s.charAt(0));
                for (i = 1; i < s.length(); i++) {
                    newList = new LinkedList(new Node(s.charAt(i)));
                    trieNode.childNode = new Node(newList);
                    trieNode = trieNode.childNode.getList().getNodeWithChar(s.charAt(i));
                }
            } else {
                Node nextTrieNode = rootNode;
                Node prevTrieNode = rootNode;
                Node siblingNode;
                for (i = 0; i < s.length(); i++) {
                    if (nextTrieNode != null && nextTrieNode.list != null && nextTrieNode.list.contains(s.charAt(i))) {
                        prevTrieNode = nextTrieNode;
                        nextTrieNode = nextTrieNode.list.getNodeWithChar(s.charAt(i)).childNode;
                    } else if (nextTrieNode == null) {
                        newList = new LinkedList(new Node(s.charAt(i)));
                        siblingNode = prevTrieNode.getList().getNodeWithChar(s.charAt(i - 1));
                        siblingNode.childNode = new Node(newList);
                        prevTrieNode = prevTrieNode.list.getNodeWithChar(s.charAt(i - 1)).childNode;
                        nextTrieNode = prevTrieNode.getList().getNodeWithChar(s.charAt(i)).childNode;
                    } else if (nextTrieNode != null) {
                        nextTrieNode.getList().insert(s.charAt(i));
                        prevTrieNode = nextTrieNode;
                        nextTrieNode = nextTrieNode.getList().getNodeWithChar(s.charAt(i)).childNode;
                    }
                }// for loop
            }//else 
            return true;
        }

        /**
         * look for 10 predecessors(with closest prefix) words and puts it in to
         * queue.
         *
         * @param sSearch string to find predecessor for.
         * @param q Queue, where to collect the predecessors.
         */
        public void genPredecessor(String sSearch, Queue<String> q) {
            if (this.rootNode == null || this.rootNode.getList() == null || this.rootNode.getList().head == null) {
                return;
            }
            String pre = "";
            Node iterNode = this.rootNode;
            for (int i = 0; iterNode != null && iterNode.getList() != null && iterNode.getList().head != null; i++) {
                if (i < sSearch.length() && iterNode.getList().contains(sSearch.charAt(i))) {
                    pre += sSearch.charAt(i);
                    iterNode = iterNode.getList().getNodeWithChar(sSearch.charAt(i)).childNode;
                } else {
                    break;
                }
            }
//        System.out.println(pre);
            collect10(iterNode, pre, q);
        }

        /**
         * collect 10 word from symbol table. Specific fro this assignment
         *
         * @param x
         * @param pre
         * @param q
         */
        public void collect10(Node x, String pre, Queue<String> q) {
            if (x == null || x.getList() == null || x.getList().head == null || q.size() > 9) {
//            System.out.println(pre);
                if (pre.length() > 5) {
                    q.enqueue(pre.substring(0, pre.length() - 1));// delleting the end of string char and adding to queue
                }
                return;
            }
            Node iterNode = x.list.head;
            for (; iterNode != null;) {
//            System.out.println(pre + iterNode.value);
                collect10(x.getList().getNodeWithChar(iterNode.value).childNode, pre + iterNode.value, q);
                iterNode = iterNode.siblingNode;
            }
        }

        /**
         * collect all word from symbol table in to Queue
         *
         * @param q
         */
        public void collectAll(Queue<String> q) {
            collect(this.rootNode, "", q);
        }

        /**
         * collect all word from symbol table
         *
         * @param x
         * @param pre
         * @param q
         */
        public void collect(Node x, String pre, Queue<String> q) {
            if (x == null || x.getList() == null || x.getList().head == null) {
//            System.out.println(pre);
                q.enqueue(pre.substring(0, pre.length() - 1));// delleting the end of string char and adding to queue
                return;
            }
            Node iterNode = x.list.head;
            for (; iterNode != null;) {
//            System.out.println(pre + iterNode.value);
                collect(x.getList().getNodeWithChar(iterNode.value).childNode, pre + iterNode.value, q);
                iterNode = iterNode.siblingNode;
            }
        }

        /**
         * Only for debugging purposes
         *
         * @param rootNode
         * @param prevChars
         */
        public void printAllWord(Node rootNode, String prevChars) {
            if (rootNode == null || rootNode.list == null || rootNode.list.head == null) {
                return;
            }
            Node iterNode = rootNode.list.head;
            for (int i = 0; iterNode != null; i++) {

                System.out.print(prevChars + iterNode.value);
                i++;
                printAllWord(rootNode.getList().getNodeWithChar(iterNode.value).childNode, prevChars + iterNode.value);
                iterNode = iterNode.siblingNode;
            }
        }

    }

    /**
     *
     * @author Daler
     */
    public static class LinkedList {

        public LinkedList list;
        public Node head;
        public int size;

        public LinkedList() {
        }

        public LinkedList(Node node) {
            if (head == null) {
                head = node;
            } else {
                node.siblingNode = this.head;
                this.head = node;
            }
            this.size++;
        }

        public LinkedList(char c) {
            Node newNode = new Node(c);
            this.head = newNode;
            this.size++;
        }

        public Node getNodeWithChar(char c) {
            Node iterNode = head;
            while (iterNode != null) {
                if (iterNode.value == c) {
                    return iterNode;
                }
                iterNode = iterNode.siblingNode;
            }
            return null;
        }

        public char getCharAt(int index) {
            Node iterNode = head;
            for (int i = 0; iterNode != null && i <= index; i++) {
                iterNode = iterNode.siblingNode;
            }
            if (iterNode == null) {
                return ' ';
            } else {
                return iterNode.value;
            }
        }

        public void addNode(Node node) {
            if (head == null) {
                head = node;
            } else {
                node.siblingNode = this.head;
                this.head = node;
            }
            this.size++;
        }

        public boolean contains(char c) {
            if (head == null) {
                return false;
            }
            Node node = this.head;
            while (node != null) {
                if (node.getData() == c) {
                    return true;
                } else {
                    node = node.siblingNode;
                }
            }
            return false;
        }

        public void insert(char c) {
            Node newNode = new Node(c);
            if (this.size == 0) {
                this.head = newNode;
            } else {
                newNode.siblingNode = this.head;
                this.head = newNode;
            }
            this.size++;
        }

        public void printAll() {
            Node iterNode = head;
            int i = 0;
            while (iterNode != null) {
                System.out.println("Node #" + i + "value is: " + iterNode.value);
                i++;
                iterNode = iterNode.siblingNode;
            }
        }

    }

    /**
     *
     * @author Daler
     */
    public static class Node {

        public LinkedList list = null;
        public Node siblingNode = null; //reference to next node of the Linked list in the same level of DLB
        public Node childNode = null; //reference to the next node of DLB
        public char value; //char value, "&" in end of the word

        public Node(char c) //constructor
        {
            this.value = c;
        }

        public Node() {

        }

        public Node(LinkedList list) {
            this.list = list;
        }

        //setters 
        public void setData(char c) {
            this.value = c;
        }

        //getters 
        public char getData() {
            return this.value;
        }

        public LinkedList getList() {
            return this.list;
        }

        void setList(LinkedList newList) {
            this.list = newList;
        }

    }

    public static class Queue<E> {

        private java.util.LinkedList<E> list = new java.util.LinkedList<E>();

        public void enqueue(E item) {
            list.addLast(item);
        }

        public E dequeue() {
            return list.poll();
        }

        public boolean hasItems() {
            return !list.isEmpty();
        }

        public int size() {
            return list.size();
        }
    }
}
