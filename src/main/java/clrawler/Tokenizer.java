package clrawler;

import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Tokenizer {

    public static String removeAllHtml(String htmlContent) {
        return Jsoup.parse(htmlContent).text();
    }

    public void retrieveTokens(boolean writeIntoSingleFile) {
        File directory = new File("sites");
        File[] files = directory.listFiles();
        if (files == null) return;
        Integer i = writeIntoSingleFile ? null : 1;
        Set<String> tokens = new HashSet<>();
        for (File file : files) {
            retrieveTokens(tokens, file, writeIntoSingleFile, i);
            if (!writeIntoSingleFile) {
                File tokenFile = new File(Utils.generateFileName(Utils.WORDS_TOKEN_DIR, i));
                i++;
            }
        }
        Utils.writeToFileLineByLine(new File(Utils.TOKENS_FILE_PATH), new ArrayList<>(tokens), true);
    }

    private void retrieveTokens(Set<String> tokens, File source, boolean needToWriteInSingleFile, Integer index) {
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

        if (needToWriteInSingleFile) {
            Utils.writeToFileLineByLine(
                    new File(Utils.TOKENS_FILE_PATH),
                    new LinkedList<>(tokens),
                    false
            );
        } else {
            Utils.writeToFileLineByLine(
                    new File(Utils.generateFileName(Utils.WORDS_TOKEN_DIR, index)),
                    new LinkedList<>(tokens),
                    false
            );
        }
//        removeDuplicates(source, true);
    }

    private void removeDuplicates(File source, boolean shouldDelete) {
        Set<String> noDuplicatesSet = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(source))) {
            String line;
            while ((line = br.readLine()) != null) {
                noDuplicatesSet.add(line.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.writeToFileLineByLine(new File(Utils.TOKENS_FILE_PATH), new LinkedList<>(noDuplicatesSet), shouldDelete);
        Utils.writeToFileLineByLine(
                source,
                new ArrayList<>(noDuplicatesSet).stream().sorted().collect(Collectors.toList()),
                shouldDelete
        );
    }
}
