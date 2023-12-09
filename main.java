import java.awt.image.BufferedImage;
import java.io.*;

public class main {
    final int DEFAULT_TIMEOUT = 10;
    static DistanceMetric_Inter dm;
    static ColorMapGenerator_Inter generator;
    static ColorQuantizer cq;

    public static void main(String[] args) {

        // try {
        //     // Load bitmap image
        //     BufferedImage image = ImageIO.read(new File("build/resources/main/image.bmp"));

        //     // Create pixel matrix
        //     Pixel[][] pixelMatrix = convertBitmapToPixelMatrix(image);

        //     // Save pixel matrix to text file of pixel representations
        //     savePixelMatrixToFile("build/resources/main/pixel_matrix.txt", pixelMatrix);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
            // testOne();
        // testTwo();


        // dm = new CircularHueMetric();
        dm = new SquaredEuclideanMetric();

        // generator = new BucketingMapGenerator();
        generator = new ClusteringMapGenerator(dm);

        // ColorQuantizer c = new ColorQuantizer("src/main/resources/image.bmp", generator);
        // ColorQuantizer c = new ColorQuantizer("src/main/resources/image1.bmp", generator);
        ColorQuantizer c = new ColorQuantizer("src/main/resources/image2.bmp", generator);
        // ColorQuantizer c = new ColorQuantizer("src/main/resources/lbj.bmp", generator);
        // ColorQuantizer c = new ColorQuantizer("src/test/resources/test.bmp", generator);
        // ColorQuantizer c = new ColorQuantizer("src/test/resources/test.bmp", generator);

        c.quantizeToBMP("src/main/resources/output2.bmp", 4);
    }

    private static Pixel[][] genStripedArr() {
        return new Pixel[][]{
            {new Pixel(5, 5, 5), new Pixel(5, 5, 5), new Pixel(5, 5, 5)},
            {new Pixel(50, 50, 50), new Pixel(50, 50, 50), new Pixel(50, 50, 50)},
            {new Pixel(100, 100, 100), new Pixel(100, 100, 100), new Pixel(100, 100, 100)},
            {new Pixel(150, 150, 150), new Pixel(150, 150, 150), new Pixel(150, 150, 150)},
            {new Pixel(200, 200, 200), new Pixel(200, 200, 200), new Pixel(200, 200, 200)},
            {new Pixel(250, 250, 250), new Pixel(250, 250, 250), new Pixel(250, 250, 250)}
        };
    }

    private static void testOne(){

        Pixel[][] stripedArr = genStripedArr();
        dm = new SquaredEuclideanMetric();
        generator = new ClusteringMapGenerator(dm);
        cq = new ColorQuantizer(stripedArr, generator);

        // Check for 1 color
        Pixel[][] result = cq.quantizeTo2DArray(1);
        Pixel single_expected = new Pixel(125, 125, 125);

        for (int row = 0; row < stripedArr.length; row++) {
            for (int col = 0; col < stripedArr[0].length; col++) {
                System.out.println(single_expected + " "+ result[row][col]);
            }
        }

        result = cq.quantizeTo2DArray(4);
        Pixel[] expectedMappings = new Pixel[]{
      			new Pixel(27, 27, 27),
      			new Pixel(125, 125, 125),
      			new Pixel(200, 200, 200),
      			new Pixel(250, 250, 250)
    		};


    		int expected = 0;
        for (int row = 0; row < stripedArr.length; row++) {
            for (int col = 0; col < stripedArr[0].length; col++) {
        				switch (row) {
          					case 0:
          					case 1:
            						expected = 0;
            						break;
          					case 2:
          					case 3:
            						expected = 1;
            						break;
          					case 4:
            						expected = 2;
            						break;
          					default:
            						expected = 3;
        				}
                System.out.println(expectedMappings[expected]+" "+ result[row][col]);
            }
        }

    }

    public static Pixel[][] convertBitmapToPixelMatrix(BufferedImage image) {
        Pixel[][] pixelMatrix = new Pixel[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                pixelMatrix[x][y] = new Pixel(red, green, blue);
            }
        }

        return pixelMatrix;
    }

    public static void savePixelMatrixToFile(String filePath, Pixel[][] matrix) {

        try {
            // Open file for writing
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

            // Write matrix to file
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    writer.write(matrix[i][j] + String.valueOf('\t'));
                }
                writer.newLine();
            }

            // Close file
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testTwo(){
        Pixel[][] stripedArr = genStripedArr();
        generator = new BucketingMapGenerator();
    
        // check 1 color
        Pixel[] result = generator.generateColorPalette(stripedArr, 1);
        Pixel expected = new Pixel(128, 0, 0);
    
        if (result.length != 1) {
            System.out.println("Incorrect number of colors returned from Basic Bucketing generateColorPalette");
        } else if (!expected.equals(result[0])) {
            System.out.println("Incorrect color returned for palette of a single color");
        }
    
        // Check with 4 colors that evenly divide 2^24
        result = generator.generateColorPalette(stripedArr, 4);
        Pixel[] expectedCT = new Pixel[]{new Pixel(32, 0, 0), new Pixel(96, 0, 0), new Pixel(160, 0, 0), new Pixel(224, 0, 0)};
    
        if (result.length != 4) {
            System.out.println("Incorrect number of colors returned from Basic Bucketing generateColorPalette");
        } else {
            for (int i = 0; i < expectedCT.length; i++) {
                if (!expectedCT[i].equals(result[i])) {
                    System.out.println("Incorrect color returned for palette of bucketing");
                    break;
                }
            }
        }
    
        // Check with 7 colors that do not evenly divide 2^24
        result = generator.generateColorPalette(stripedArr, 7);
        expectedCT = new Pixel[]{
            new Pixel(18, 73, 36),
            new Pixel(54, 219, 109),
            new Pixel(91, 109, 182),
            new Pixel(128, 0, 0),
            new Pixel(164, 146, 73),
            new Pixel(201, 36, 146),
            new Pixel(237, 182, 219)
        };
    
        if (result.length != 7) {
            System.out.println("Incorrect number of colors returned from Basic Bucketing generateColorPalette");
        } else {
            for (int i = 0; i < expectedCT.length; i++) {
                System.out.println(expectedCT[i] + " " + result[i]);
                // if (!expectedCT[i].equals(result[i])) {
                //     System.out.println("Incorrect color returned for palette of Bucketing");
                //     break;
                // }
            }
        }
        
    }

}
