package clrawler;

public class Main {

    public static final String SITE = "https://tproger.ru";

    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        crawler.crawl();
    }

}
