import java.io.IOException;
import java.util.List;
import java.util.Map;

import crawl.CrawlData;

public class App {

	public static void main(String[] args) {
		CrawlData crawler = new CrawlData();
		try {
			List<Map<String, Object>> result = crawler.clawerDataScene("https://pointcard.rakuten.co.jp/partner/");
			System.out.print(result);
		} catch (IOException e) {
			System.out.print(e.getMessage());
		}

	}

}
