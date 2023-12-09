public class CircularHueMetric implements DistanceMetric_Inter{
    public double colorDistance(Pixel p1, Pixel p2){
        int h1 = p1.getHue();
        int h2 = p2.getHue();
        int dist = Math.abs(h2-h1);

        if(dist>=180) return 360-dist;  // CHECK THIS
        return dist;
    }

}
