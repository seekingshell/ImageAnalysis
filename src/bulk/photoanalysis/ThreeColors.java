package bulk.photoanalysis;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ThreeColors {
    public static void main(String args[]) throws IOException {
        List<String> imageUrls = Files.readAllLines(Paths.get("urls.txt"));
        for(String imageUrl : imageUrls) {
            processImage(imageUrl);
        }

    }

    private static void processImage(String urlString) throws IOException {
        BufferedImage image = downloadImage(urlString);
        HashMap<Integer, Integer> pixelCountMap = new HashMap();

        // Getting pixel color by position x and y for whole image
        // store color value and prevalence in map
        for(int x = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getHeight(); y++) {
                Integer pixelRGB = image.getRGB(x,y);
                pixelCountMap.put(pixelRGB, pixelCountMap.getOrDefault(pixelRGB, 0) + 1);
            }
        }

        Entry<Integer, Integer>[] topThree = findTopThreePrevalentColors(pixelCountMap);
        StringBuilder output = new StringBuilder();
        output.append(Integer.toHexString(topThree[0].getKey()) + ": " + topThree[0].getValue() + ", ");
        output.append(Integer.toHexString(topThree[1].getKey()) + ": " + topThree[1].getValue() + ", ");
        output.append(Integer.toHexString(topThree[2].getKey()) + ": " + topThree[2].getValue() + "\n");

        System.out.print(output);
    }

    private static BufferedImage downloadImage(String urlString) throws IOException {
        URL url = new URL(urlString);
        return ImageIO.read(url);
    }

    private static Entry<Integer, Integer>[] findTopThreePrevalentColors(HashMap<Integer, Integer> pixelCountMap) {
        Entry<Integer, Integer> topThree[] = new Entry[3];
        for(Entry<Integer, Integer> pair : pixelCountMap.entrySet()) {
            if(topThree[0] == null || pair.getValue() > topThree[0].getValue()) {
                if(topThree[0] != null) {
                    // move down 1->2 and 2->3
                    topThree[2] = topThree[1];
                    topThree[1] = topThree[0];
                }

                // insert new pair at 1st place
                topThree[0] = pair;
            }
            else if(topThree[1] == null || pair.getValue() > topThree[1].getValue()) {
                if(topThree[1] != null) {
                    // move 2->3
                    topThree[2] = topThree[1];
                }

                // insert new pair at 2nd place
                topThree[1] = pair;
            }
            else if(topThree[2] == null || pair.getValue() > topThree[2].getValue()) {
                // insert new pair at 3rd place
                topThree[2] = pair;
            }
        }

        return topThree;
    }
}

