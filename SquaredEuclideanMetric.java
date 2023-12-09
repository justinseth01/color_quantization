public class SquaredEuclideanMetric implements DistanceMetric_Inter{
    public double colorDistance(Pixel p1, Pixel p2){
        double r = p1.getRed()-p2.getRed();
        r = r*r;
        double g = p1.getGreen()-p2.getGreen();
        g = g*g;
        double b = p1.getBlue()-p2.getBlue();
        b = b*b;

        return r+g+b;
    }
}
