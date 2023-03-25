package clrawler;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    public static final String UNIONS_REGEX_PATTERN = "(а|абы|аж|ан|благо|буде|будто|вроде|да|дабы|даже|едва|ежели|если|же|затем|зато|и|ибо|или|итак|кабы|как|когда|коли|коль|ли|либо|лишь|нежели|но|пока|покамест|покуда|поскольку|притом|причем|пускай|пусть|раз|разве|ровно|сиречь|словно|так|также|тоже|только|точно|хоть|хотя|чем|чисто|что|чтоб|чтобы|чуть|якобы)";
    public static final String PREPOSITIONS_REGEX_PATTERN = "(без|близ|в|вместо|вне|для|до|за|из|из-за|из-под|к|ко|кроме|между|меж|на|над|о|об|обо|от|ото|перед|передо|пред|предо|по|под|подо|при|про|ради|с|со|сквозь|у|через|чрез)";
    public static final String TOKENS_FILE_PATH = "words/tokens.txt";
    public static final String TOKENS_LEMMATIZED_FILE_PATH = "words/tokens_lemmatized.txt";
    public static final String GROUPED_TOKENS_BY_LEMMAS_FILE_PATH = "words/tokens_grouped_by_lemmas.txt";
    public static final String INDEXED_WORDS_FILE_PATH = "words/words_indexed.txt";
    public static final String WORDS_LEMMAS_DIR = "words-lemma";
    public static final String WORDS_TOKEN_DIR = "words-tokens";
    public static final String IDFS_TOKENS_FILE_PATH = "words/idfs_tokens.txt";
    public static final String IDFS_LEMMAS_FILE_PATH = "words/idfs_lemmas.txt";
    public static final String TOKENS_COUNTERS_DIR = "token-counters";
    public static final String LEMMAS_COUNTERS_DIR = "lemma-counters";

    public static String generateFileName(String dirName, Integer index) {
        return dirName.concat("/").concat(String.valueOf(index)).concat(".txt");
    }

    public static void writeToFileLineByLine(File outputFile, List<String> lines, boolean shouldDelete) {
        if (!outputFile.exists()) {
            outputFile.getParentFile().mkdirs();
        } else {
            if (shouldDelete) {
                outputFile.delete();
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            assert fos != null;
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))) {
                for (String line : lines) {
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Map<Integer, String> findLinksForDocumentNumbers(List<Integer> documentNumbers) {
        Map<Integer, String> result = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("index.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.replace(" ", "").split("-") ;
                Integer documentNumberCurrentLine = Integer.parseInt(values[0]);
                if (documentNumbers.contains(documentNumberCurrentLine)) {
                    result.put(documentNumberCurrentLine, values[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}