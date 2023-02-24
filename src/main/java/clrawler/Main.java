package clrawler;

public class Main {

    public static final String SITE = "https://tproger.ru";

    public static void main(String[] args) {
//        Crawler crawler = new Crawler();
//        crawler.crawl();
        Tokenizer tokenizer = new Tokenizer();
        tokenizer.retrieveTokens();
        Lemmatizer lemmatizer = new Lemmatizer();
        lemmatizer.lemmatize();
        lemmatizer.groupLemmasByTokens();
    }

}
