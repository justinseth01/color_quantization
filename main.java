public class main {
    public static void main(String[] args) {

        // BucketingMapGenerator generator = new BucketingMapGenerator();
        ColorMapGenerator_Inter generator = new ClusteringMapGenerator("euclidean");

        ColorQuantizer c = new ColorQuantizer("images/justin.bmp", generator);

        c.quantizeToBMP("output/justin16Colors.bmp", 16);
    }

}
