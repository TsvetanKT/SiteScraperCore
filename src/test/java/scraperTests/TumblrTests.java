package scraperTests;

import java.net.URL;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import Application.SiteScraper;

public class TumblrTests {

    @Test
    public void catImagesPositive() {
        SiteScraper scraper = new SiteScraper();
        List<String> urls =
                scraper.getIteration("Tumblr Blog", "https://cutest-cats.tumblr.com", 1);

        System.out.println("Images number " + urls.size());
        for (String url : urls) {
            System.out.println(url);
            Assert.assertTrue(isValidURL(url));
        }

        Assert.assertFalse(urls.isEmpty());

    }

    private static boolean isValidURL(String urlString) {
        try {
            URL url = new URL(urlString);
            url.toURI();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
