package clrawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class BooleanSearcher {

    public void execute() {
        System.out.println("Write your query:");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in))) {
            String fullQuery = reader.readLine();
            String[] queries = fullQuery.split(" ");
            List<Integer> indexes = new ArrayList<>();
            for (int i = 0; i < queries.length; i++) {
                if (i == 0) {
                    indexes.addAll(getIndexesForWord(queries[0]));
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
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Integer> getIndexesForWord(String word) {
        List<Integer> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(Utils.INDEXED_WORDS_FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.replace(" ", "").split("\\[") ;
                String wordCurrentLine = values[0];
                if (wordCurrentLine.contains(word)) {
                    List<Integer> indexes = Arrays.stream(values[1].replace("]", "")
                            .split(","))
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                    result.addAll(indexes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<Integer> and(List<Integer> query1, String query2) {
        List<Integer> indexesForQuery2 = getIndexesForWord(query2);
        return query1.stream()
                .distinct()
                .filter(indexesForQuery2::contains)
                .collect(Collectors.toList());
    }

    private List<Integer> or(List<Integer> query1, String query2) {
        List<Integer> indexesForQuery2 = getIndexesForWord(query2);
        indexesForQuery2.addAll(query1);
        return new ArrayList<>(new HashSet<>(indexesForQuery2));
    }

    private List<Integer> not(List<Integer> query1, String query2) {
        List<Integer> indexesForQuery2 = getIndexesForWord(query2);
        return query1.stream()
                .distinct()
                .filter(x -> !indexesForQuery2.contains(x))
                .collect(Collectors.toList());
    }
}