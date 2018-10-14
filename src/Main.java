
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) throws IOException {
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8182");
        List<Article> articles = new ArrayList<>();

        Document document = Jsoup.connect("https://www.gazeta.ru").get();
        System.out.println(document.location() + " " + document.title());

        Elements articleWeb = document.getElementsByAttributeValue("itemprop", "headline");

        for (Element element : articleWeb) {
            if (element.className().equals("")) {
                articles.add(new Article(element.text(), element.baseUri() + element.parent().attr("href")));
            } else {
                articles.add(new Article(element.text(), element.baseUri() + element.child(0).attr("href")));
            }
        }
        writerToFileAllURL(articles);

        AtomicInteger counterFile = new AtomicInteger(1);
        for(Article counterArticle : articles){
            createFileArticle(counterArticle.getUrl(), "Articles\\" + counterFile + ".txt");
            counterFile.getAndIncrement();
        }
        System.out.println((char) 27 + "[32mВыполнено! " + (char)27 + "[0m");
    }

    private static void createFileArticle(String url, String path) throws IOException {
        Document document = Jsoup.connect(url).followRedirects(true).get();
        System.out.println(document.location() + " " + document.title());
        Elements elements = document.getElementsByAttributeValue("itemprop", "articleBody");
        StringBuffer text = new StringBuffer();
        text.append(elements.text());
        for(int i = 150; i <= text.length(); i+=150) text.insert(i, '\n');
        FileWriter writer = new FileWriter(path);
        writer.write(String.valueOf(text));
        writer.close();
    }

    private static void writerToFileAllURL(List<Article> articles) throws IOException {
        FileWriter writer = new FileWriter("allArticle.txt");
        int counter = 1;
        for(Article counterArticle : articles) {
            String name = counterArticle.getName();
            String url = counterArticle.getUrl();
            writer.write(counter + ". name=" + name + " " + " url=" + url + System.getProperty("line.separator"));
            counter++;
        }
        writer.close();
    }
}

class Article {
    private String name;
    private String url;

    @Override
    public String toString() {
        return"name='" + name +
                ", url='" + url + '\'' + '\n';
    }

    Article(String name, String url) {
        this.name = name;
        this.url = url;
    }

    String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}