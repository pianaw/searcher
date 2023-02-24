package clrawler;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Lemmatizer {

    public void lemmatize() {
        List<String> tokens = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(Utils.TOKENS_FILE_PATH))) {
            LuceneMorphology russianLuceneMorphology = new RussianLuceneMorphology();
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    List<String> wordBaseForms = russianLuceneMorphology.getNormalForms(line);
                    tokens.addAll(wordBaseForms);
                } catch (WrongCharaterException | ArrayIndexOutOfBoundsException ignored) {
                    // ignore
                }
            }
            Utils.writeToFileLineByLine(new File(Utils.TOKENS_LEMMATIZED_FILE_PATH), tokens, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void groupLemmasByTokens() {
        Set<String> uniqueLemmas = new HashSet<>();
        // Берем все уникальные лемммы
        try (BufferedReader br = new BufferedReader(new FileReader(Utils.TOKENS_LEMMATIZED_FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                uniqueLemmas.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Set<String>> lemmaToTokensMap = new HashMap<>();
        uniqueLemmas.forEach(token -> lemmaToTokensMap.put(token, new HashSet<>()));
        try (BufferedReader br = new BufferedReader(new FileReader(Utils.TOKENS_FILE_PATH))) {
            LuceneMorphology russianLuceneMorphology = new RussianLuceneMorphology();
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    List<String> wordBaseForms = russianLuceneMorphology.getNormalForms(line);
                    String finalLine = line;
                    wordBaseForms.forEach(baseForm -> {
                        for (Map.Entry<String, Set<String>> entry : lemmaToTokensMap.entrySet()) {
                            String lemma = entry.getKey();
                            Set<String> tokensArray = entry.getValue();
                            if (baseForm.equals(lemma)) {
                                tokensArray.add(finalLine);
                            }
                        }
                    });
                } catch (WrongCharaterException | ArrayIndexOutOfBoundsException ignored) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> finalLinesList = new ArrayList<>();
        lemmaToTokensMap.forEach((lemma, tokens) -> {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(lemma).append(" ");
            tokens.forEach(token -> stringBuilder.append(token).append(" "));
            finalLinesList.add(stringBuilder.toString());
        });

        Utils.writeToFileLineByLine(new File(Utils.GROUPED_TOKENS_BY_LEMMAS_FILE_PATH), finalLinesList, false);
    }

}