package clrawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BooleanVectorizedSearch {

    public Map<Integer, Double> execute(String text) {
        String fullQuery = text;
        String[] queries = fullQuery.split(" ");
        Map<Integer, Double> indexes = new HashMap<>();
        for (int i = 0; i < queries.length; i++) {
            if (i == 0) {
                Map<Integer, Double> indexesForWord = getIndexesForWord(queries[0]);
                indexes.putAll(indexesForWord);
                continue;
            }
            switch (queries[i].toLowerCase()) {
                case "и":
                    indexes = and(indexes, queries[i+1]);
                    break;
                case "или":
                    indexes = or(indexes, queries[i+1]);
                    break;
                case "не":
                    indexes = not(indexes, queries[i+1]);
            }
        }
        return indexes;
    }

    private Map<Integer, Double> getIndexesForWord(String word) {
        Map<Integer, Double> result = new HashMap<>();

        try {
            Stream<Path> filesToProcess = Files.list(Paths.get(Utils.TOKENS_COUNTERS_DIR));
            filesToProcess.forEach(path -> {
                try (BufferedReader br = new BufferedReader(new FileReader(path.toString()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] values = line.split(" ") ;
                        String wordCurrentLine = values[0];
                        if (wordCurrentLine.equals(word)) {
                            Integer key = Integer.valueOf(path.getFileName().toString().replace(".txt", ""));
                            result.put(key, Double.valueOf(values[2]));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Map<Integer, Double> and(Map<Integer, Double> query1, String query2) {
        Map<Integer, Double> indexesForQuery2 = getIndexesForWord(query2);
        return query1.entrySet().stream()
                .distinct()
                .filter(entry -> indexesForQuery2.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() + indexesForQuery2.get(e.getKey())));
    }

    private Map<Integer, Double> or(Map<Integer, Double> query1, String query2) {
        Map<Integer, Double> indexesForQuery2 = getIndexesForWord(query2);
        indexesForQuery2.putAll(query1);
        return new HashMap<>(indexesForQuery2);
    }

    private Map<Integer, Double> not(Map<Integer, Double> query1, String query2) {
        Map<Integer, Double> indexesForQuery2 = getIndexesForWord(query2);
        return query1.entrySet().stream()
                .distinct()
                .filter(x -> !indexesForQuery2.containsKey(x))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}