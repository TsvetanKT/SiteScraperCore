package scraperTests;

import org.testng.annotations.Test;

public class GoogleImagesTests extends TestCommons  {

    @Test
    public void googleCatImagesPositive() {
        imagesPositiveFlow("Google Images", "cute cats", 1);
    }
}
