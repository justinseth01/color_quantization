import java.util.*;

public class BucketingMapGenerator implements ColorMapGenerator_Inter{
    
    public BucketingMapGenerator(){

    }

    public Pixel[] generateColorPalette(Pixel[][] pixelArr, int numColors){
        if(numColors<1) throw new IllegalArgumentException();
        Bag uniqueColors = uniqueColors(pixelArr);
        Pixel[] palette = new Pixel[numColors];

        if(uniqueColors.size()<numColors){
            palette = new Pixel[uniqueColors.size()];
            int i = 0;
            for(Pixel p : uniqueColors){
                palette[i] = p;
                i++;
            }
            return palette;
        }

        double bucketRange = 16777216.0 / numColors;
        for(int i = 0; i<numColors; i++){
            int color = (int) (bucketRange * i + (bucketRange / 2.0));
            palette[i] = new Pixel((color>>16) & 0xFF,(color>>8) & 0xFF, color & 0xFF);
        }
        return palette;
    }

    public Map<Pixel, Pixel> generateColorMap(Pixel[][] pixelArr, Pixel[] initialColorPalette){
        Map<Pixel, Pixel> colorMap = new HashMap<>();

        // For each pixel in pixelArr, find the closest color in the initialColorPalette
        for (Pixel[] row : pixelArr) {
            for (Pixel pixel : row) {
                Pixel closestColor = minDistPixel(pixel, initialColorPalette);
                colorMap.put(pixel, closestColor);
            }
        }

        return colorMap;
    }

    private Pixel minDistPixel(Pixel p, Pixel[] colorPalette){
        if(colorPalette==null) return null;

        Pixel output = null;
        double minDist = Double.MAX_VALUE;
        for(int i=0; i<colorPalette.length; i++){
            double currDist = calculateColorDistance(p, colorPalette[i]);
            if(currDist < minDist){
                minDist = currDist;
                output = colorPalette[i];
            }
        }

        return output;
    }

    private int calculateColorDistance(Pixel pixel1, Pixel pixel2) {
        int c1 = (pixel1.getRed()<<16) | (pixel1.getGreen()<<8)  | pixel1.getBlue();
        int c2 = (pixel2.getRed()<<16) | (pixel2.getGreen()<<8)  | pixel2.getBlue();
        return Math.abs(c2-c1);
    }

    private Bag uniqueColors(Pixel[][] pixelArr){
        Bag bag = new Bag();
        boolean[] selected = new boolean[16777216];

        for (int i=0; i<pixelArr.length; i++) {
            for (int j=0; j<pixelArr[0].length; j++) {
                int rgb = pixel2rgb(pixelArr[i][j]);
                if(! selected[rgb]){
                    bag.add(pixelArr[i][j]);
                    selected[rgb] = true;
                }
            }
        }
        
        return bag;
    }

    private int pixel2rgb(Pixel input){
        return (input.getRed() << 16) | (input.getGreen() << 8) | input.getBlue();
    }
}
