import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Tokenizer {

    public static class Token {
        String type;
        String token;
        ArrayList<Character> components;

        public Token(String token, String type, ArrayList<Character> components) {
            this.token = token;
            this.type = type;
            this.components = components;
        }
    }

    public static String classifyToken(String token) {
        if (isParsableToFloat(token)) {
            return "Number";
        } else if (token.matches("[a-zA-Z]+")) {
            return "Word";
        } else if (token.matches("[a-zA-Z0-9]+")) {
            return "Alphanumeric";
        } else if (token.matches("\\p{Punct}")) {
            return "Punctuation";
        } else if (token.equals("\n")) {
            return "End of Line";
        } else {
            return "Unknown";
        }
    }

    public static boolean isParsableToFloat(String str) {
        try {
            if (str.endsWith(".") || str.equals("."))
                return false;
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static ArrayList<Character> getGranular(String word) {
        ArrayList<Character> granularComponents = new ArrayList<>();
        for (char ch : word.toCharArray()) {
            granularComponents.add(ch);
        }
        return granularComponents;
    }

    public static String arrListToStr(ArrayList<Character> charList) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < charList.size(); i++) {
            sb.append("'").append(charList.get(i)).append("'");
            if (i < charList.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);
        File inputFile = null;
        String fileName;

        // Loop until the correct file is provided
        while (true) {
            System.out.print("Enter file Name: ");
            fileName = inputScanner.nextLine();

            // Append the ".txt" extension to the file name
            inputFile = new File(fileName + ".txt");

            // Check if the file exists
            if (inputFile.exists() && !inputFile.isDirectory()) {
                break; // Exit loop if the file exists
            } else {
                System.out.println("File not found: " + fileName + ".txt");
            }
        }

        // Process the file once a valid one is found
        try {
            // Read the entire file content at once
            String fileContent = readFileContent(inputFile);

            // Tokenize the entire file content
            ArrayList<Token> tokens = new ArrayList<>();
            StringBuilder currentToken = new StringBuilder();
            boolean insideNumber = false;

            for (int i = 0; i < fileContent.length(); i++) {
                char c = fileContent.charAt(i);

                if (c == '?' || c == '.' && (i == fileContent.length() - 1 || !Character.isLetterOrDigit(fileContent.charAt(i + 1)))) {
                    // Handle sentence end or delimiter
                    if (currentToken.length() > 0) {
                        String token = currentToken.toString();
                        tokens.add(new Token(token, classifyToken(token), getGranular(token)));
                        currentToken.setLength(0);
                    }
                    // Period as end of sentence or delimiter
                    if (c == '.') {
                        tokens.add(new Token(".", "Punctuation", getGranular(".")));
                    }
                } else if (Character.isLetterOrDigit(c) || (c == '.' && insideNumber)) {
                    currentToken.append(c);
                    if (Character.isDigit(c)) {
                        insideNumber = true;
                    }
                } else if (isPunctuation(c)) {
                    if (currentToken.length() > 0) {
                        String token = currentToken.toString();
                        tokens.add(new Token(token, classifyToken(token), getGranular(token)));
                        currentToken.setLength(0);
                    }
                    tokens.add(new Token(String.valueOf(c), "Punctuation", getGranular(String.valueOf(c))));
                    insideNumber = false;
                } else {
                    if (currentToken.length() > 0) {
                        String token = currentToken.toString();
                        tokens.add(new Token(token, classifyToken(token), getGranular(token)));
                        currentToken.setLength(0);
                    }
                }
            }

            // Add the last token if there is one
            if (currentToken.length() > 0) {
                String token = currentToken.toString();
                tokens.add(new Token(token, classifyToken(token), getGranular(token)));
            }

            tokens.add(new Token("EOL", "End of Line", new ArrayList<>()));

            // Output the tokenized results in the terminal
            System.out.println("\n------------------------------");
            System.out.println("\nPart 1: Tokenize");
            System.out.println("\n------------------------------");
            for (Token token : tokens) {
                System.out.println("Token -> " + token.token + " | Type -> " + token.type);
            }
            System.out.println("\n------------------------------");
            System.out.println("\nPart 2: Granular");
            System.out.println("\n------------------------------");
            for (Token token : tokens) {
                if (token.token.equals("EOL"))
                    break;
                if (token.components.size() > 1) {
                    System.out.println("Token: " + token.token + " -> " + arrListToStr(token.components));
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
        }

        inputScanner.close();
    }

    // Method to read the entire file content as a string
    private static String readFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private static boolean isPunctuation(char c) {
        return String.valueOf(c).matches("\\p{Punct}");
    }
}
