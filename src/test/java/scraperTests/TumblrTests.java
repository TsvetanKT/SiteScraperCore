package scraperTests;

import org.testng.annotations.Test;

public class TumblrTests extends TestCommons {

    @Test
    public void catImagesPositive() {
        imagesPositiveFlow("Tumblr Blog", "https://cutest-cats.tumblr.com", 1);
    }
}
