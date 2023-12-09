import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.util.Map;


public class ColorQuantizer{
    private Pixel[][] pixelArray;
    private ColorMapGenerator_Inter colorMapGenerator;

    public ColorQuantizer(Pixel[][] pixelArray, ColorMapGenerator_Inter gen){
        this.pixelArray = pixelArray;
        this.colorMapGenerator = gen;
    }

    public ColorQuantizer(String bmpFilename, ColorMapGenerator_Inter gen){
        this.colorMapGenerator = gen;

        try {
            BufferedImage image = ImageIO.read(new File(bmpFilename));
            this.pixelArray = convertImageToPixelArray(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Pixel[][] convertImageToPixelArray(BufferedImage image) {
        Pixel[][] pixelArray = new Pixel[image.getHeight()][image.getWidth()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));

                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                // Create a Pixel object and store it in the array
                pixelArray[y][x] = new Pixel(red, green, blue);
            }
        }

        return pixelArray;
    }

    public Pixel[][] quantizeTo2DArray(int numColors){
        if(numColors<1) throw new IllegalArgumentException();

        Pixel[] colorPalette = colorMapGenerator.generateColorPalette(pixelArray, numColors);
        Map<Pixel, Pixel> colorMap = colorMapGenerator.generateColorMap(pixelArray, colorPalette);

        // Initialize the quantized image array
        Pixel[][] quantizedImage = new Pixel[pixelArray.length][pixelArray[0].length];

        // Map each pixel in the original image to the quantized color
        for (int i = 0; i < pixelArray.length; i++) {
            for (int j = 0; j < pixelArray[0].length; j++) {
                Pixel temp = pixelArray[i][j];
                quantizedImage[i][j] = colorMap.get(temp);
            }
        }

        return quantizedImage;
    }

    public void quantizeToBMP(String fileName, int numColors){
        if(numColors<1) throw new IllegalArgumentException();
        Pixel[][] quantizedImage = quantizeTo2DArray(numColors);

        int width = quantizedImage[0].length;
        int height = quantizedImage.length;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                Pixel pixel = quantizedImage[col][row];
                int rgb = (pixel.getRed() << 16) | (pixel.getGreen() << 8) | pixel.getBlue();
                image.setRGB(row, col, rgb);
            }
        }

        try {
            File outputFile = new File(fileName);
            ImageIO.write(image, "bmp", outputFile);
            System.out.println("Image saved successfully to " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing image to file: " + e.getMessage());
        }

    }
}
