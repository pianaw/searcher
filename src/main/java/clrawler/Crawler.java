package clrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

public class Crawler {

    private final Set<String> sitesRelative = new HashSet<>();
    private String tempPageText = null;
    private final Map<Integer, String> numToLink = new HashMap<>();

    public void crawl() {
        sitesRelative.add("/");
        downloadPage(Main.SITE, this::handleDocForLink);
        int i = 1;
        for (String relativePath : sitesRelative) {
            if (i == 101) {
                return;
            }
            String absolutePath = Main.SITE + relativePath;
            downloadPage(absolutePath, this::handleDocForText);
            saveInFile(tempPageText, i + ".txt");
            numToLink.put(i, absolutePath);
            i++;
        }
        saveInIndex();
    }

    private void downloadPage(String absolutePath, Consumer<Document> handleDoc) {
        try {
            Document doc = Jsoup.connect(absolutePath).get();
            handleDoc.accept(doc);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDocForLink(Document doc) {
        Elements links = doc.select("a[href]");
        links.forEach(link -> handleFoundLink(link.attr("href")));
    }

    private void handleFoundLink(String relativePath) {
        if (!relativePath.startsWith("/")
                || sitesRelative.contains(relativePath)
                || relativePath.contains(".")) return;
        sitesRelative.add(relativePath);
    }

    private void handleDocForText(Document doc) {
        if (Objects.isNull(doc)) {
            return;
        }

        tempPageText = doc.html();
    }

    private void saveInFile(String text, String filename) {
        try (PrintStream out = new PrintStream("sites/" + filename)) {
            out.print(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveInIndex() {
        File fout = new File("index.txt");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fout);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))) {
            for (Map.Entry<Integer, String> pair : numToLink.entrySet()) {
                bw.write(pair.getKey() + " - " + pair.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
