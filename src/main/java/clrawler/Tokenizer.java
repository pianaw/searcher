package clrawler;

import org.jsoup.Jsoup;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Tokenizer {

    public static String removeAllHtml(String htmlContent) {
        return Jsoup.parse(htmlContent).text();
    }

    public void retrieveTokens() {
        File directory = new File("sites");
        File[] files = directory.listFiles();
        if (files == null) return;
        Set<String> tokens = new HashSet<>();
        for (File file : files) {
            retrieveTokens(tokens, file);
        }
        Utils.writeToFileLineByLine(new File(Utils.TOKENS_FILE_PATH), new ArrayList<>(tokens), true);
    }

    private void retrieveTokens(Set<String> tokens, File source) {
        StringBuilder sb = new StringBuilder();
        Set<String> retrievedTokens = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(source))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String textOnly = removeAllHtml(sb.toString());
            retrievedTokens.addAll(Arrays.asList(textOnly.toLowerCase().split(" ")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        tokens.addAll(retrievedTokens.stream()
                .map(token -> token.replace(" ", ""))
                .map(token -> token.replace(".", ""))
                .map(token -> token.replace(",", ""))
                .map(token -> token.replace(":", ""))
                .map(token -> token.replace("!", ""))
                .map(token -> token.replace("?", ""))
                .map(token -> token.replace(";", ""))
                .map(token -> token.replace("\"", ""))
                .map(token -> token.replace(")", ""))
                .map(token -> token.replace("(", ""))
                .map(token -> token.replace("»", ""))
                .map(token -> token.replace("«", ""))
                .filter(token -> !token.equals(""))
                .filter(token -> token.length() != 1)
                .filter(token -> !token.toLowerCase().matches(Utils.UNIONS_REGEX_PATTERN))
                .filter(token -> !token.toLowerCase().matches(Utils.PREPOSITIONS_REGEX_PATTERN))
                .collect(Collectors.toSet()));
    }

}
