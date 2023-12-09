import java.util.HashMap;
import java.util.Map;

public class ClusteringMapGenerator implements ColorMapGenerator_Inter{
    private DistanceMetric_Inter distance;
    private Bag selected;

    public ClusteringMapGenerator(String distance_metric ){
        if(distance_metric.equals("euclidean")){
            distance = new SquaredEuclideanMetric();

        }else{
            distance = new CircularHueMetric();
        }
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

        Pixel[] colors = new Pixel[numColors];
        selected = new Bag();
        selected.add(pixelArr[0][0]);
        // selected = new Bag();
        // selected.add(pixelArr[0][0]);
        colors[0] = pixelArr[0][0]; //check for sending null

        for(int i=1; i<numColors; i++){
            colors[i] = farthestPixel(pixelArr);
        }
        for(Pixel p : colors) {
            System.out.print(p.getRed() + " " + p.getGreen() + " " + p.getBlue());
            System.out.println(" hue: " + p.getHue());
        }
        return colors;
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

    private Pixel farthestPixel(Pixel pixelArr[][]){
        double maxDistance = -1;
        Pixel maxPixel = null;

        for(int i=0; i<pixelArr.length; i++){
            for(int j=0; j<pixelArr[0].length; j++){
                if(selected.contains(pixelArr[i][j])) continue;
                double temp = minDistance(pixelArr[i][j]);  
                
                //if it's bigger than maxDistance, update minDistance and set minPixel = current pixel
                if(temp == maxDistance){
                    int rgbTemp = pixel2rgb(pixelArr[i][j]);
                    int rgbMax = pixel2rgb(maxPixel);

                    //only change if 24 bit color value is higher (for ties)
                    if(rgbTemp>rgbMax){
                        maxPixel = pixelArr[i][j];
                        maxDistance = temp;
                    }
                }
                else if(temp > maxDistance){
                    maxPixel = pixelArr[i][j];
                    maxDistance = temp;
                }
            }
        }
        selected.add(maxPixel);
        return maxPixel;
    }

    private int pixel2rgb(Pixel input){
        return (input.getRed() << 16) | (input.getGreen() << 8) | input.getBlue();
    }

    // compute the total distance to each of the already selected pixels
    private double minDistance(Pixel input){
        double minDistance = Double.MAX_VALUE;
        for (Pixel p : selected){
            if(p.equals(input)) continue;
            double temp = distance.colorDistance(p,input);

            if (temp < minDistance) {
                minDistance = temp;
            }
        }
    
        return minDistance;
    }





    // ==================================================================
    // ==================================================================
    // ==================================================================

    public Map<Pixel, Pixel> generateColorMap(Pixel[][] pixelArr, Pixel[] initialColorPalette){
        boolean changed = true;

        // for each pixel in our picture, group it into initialColorPalette.length
        // different groups depending on which color it's closest to
        // then find the average rgb of each and update inital color palette accordingly
        // keep doing it till nothing changed
        // loop through one last time to map everything
        
        do{
            changed = false;
            Bag[] updates = new Bag[initialColorPalette.length];

            for(int i=0; i<pixelArr.length; i++){
                for(int j=0; j<pixelArr[0].length; j++){
                    // minDistPixel returns which bucket we put it in
                    int index = minDistIndex(initialColorPalette, pixelArr[i][j]);
                    if(updates[index]==null) updates[index] = new Bag();
                    updates[index].add(pixelArr[i][j]);
                }
            }
    
            for(int i=0; i<updates.length; i++){
                Bag bag = updates[i];
                if(bag==null) continue;

                int r = 0, g = 0, b = 0;

                for(Pixel p : bag){
                    r += p.getRed();
                    g += p.getGreen();
                    b += p.getBlue();
                }
                r /= bag.size();
                g /= bag.size();
                b /= bag.size();
                // rgb /= bag.size();
                int red = initialColorPalette[i].getRed();
                int green = initialColorPalette[i].getGreen();
                int blue = initialColorPalette[i].getBlue();
                // int prev = (red<<16) | (green<<8) | (blue);
                if(r==red && g==green && b==blue) {}
                else{ changed = true;}
                initialColorPalette[i] = new Pixel(r,g,b);
            }
            
        }while(changed);

        Map<Pixel, Pixel> colorMap = new HashMap<Pixel, Pixel>();
        for(int i=0; i<pixelArr.length; i++){
            for(int j=0; j<pixelArr[0].length; j++){
                // minDistPixel returns which bucket we put it in
                Pixel p = minDistPixel(initialColorPalette, pixelArr[i][j]);
                colorMap.put(pixelArr[i][j], p);
            }
        }

        // determine inital cluster locations given 
        return colorMap;
    }

    private int minDistIndex(Pixel[] colorPalette, Pixel p){
        int index = -1;
        double min = Double.MAX_VALUE;

        for(int i=0; i<colorPalette.length; i++){
            double temp = Math.abs(distance.colorDistance(colorPalette[i], p));
            if(temp<min){
                min = temp;
                index = i;
            }
        }

        return index;
    }

    private Pixel minDistPixel(Pixel[] colorPalette, Pixel p){
        Pixel output = null;
        double min = Double.MAX_VALUE;

        for(int i=0; i<colorPalette.length; i++){
            double temp = distance.colorDistance(colorPalette[i], p);
            if(temp<min){
                min = temp;
                output = colorPalette[i];
            }
        }

        return output;
    }
}