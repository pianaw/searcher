package clrawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class InvertedIndexer {

    public void execute() {
        Map<String, Set<Integer>> tokens = new HashMap<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader(Utils.GROUPED_TOKENS_BY_LEMMAS_FILE_PATH)
        )) {
            String tokenLemmatizedLine;
            while ((tokenLemmatizedLine = br.readLine()) != null) {
                tokenLemmatizedLine = tokenLemmatizedLine.split(": ")[0];
                File directory = new File(Utils.WORDS_LEMMAS_DIR);
                File[] files = directory.listFiles();
                if (files == null) return;
                int i = 1;
                for (File file : files) {
                    try (BufferedReader _br = new BufferedReader(new FileReader(file))) {
                        String _line;
                        while ((_line = _br.readLine()) != null) {
                            if (tokenLemmatizedLine.contains(_line)) {
                                if (tokens.containsKey(tokenLemmatizedLine)) {
                                    tokens.get(tokenLemmatizedLine).add(i);
                                } else {
                                    Set<Integer> indexes = new HashSet<>();
                                    indexes.add(i);
                                    tokens.put(tokenLemmatizedLine, indexes);
                                }
                            }
                        }
                        i++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            List<String> result = tokens.entrySet().stream()
                    .map(entry -> "" + entry.getKey() + " " + entry.getValue().toString())
                    .collect(Collectors.toList());
            Utils.writeToFileLineByLine(
                    new File(Utils.INDEXED_WORDS_FILE_PATH),
                    result,
                    false
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}