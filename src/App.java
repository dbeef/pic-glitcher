import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by dbeef on 02.04.17.
 */
public class App {

    private static final String VERSION = "0.1";
    private static final String SCAN_BEGINNING = "ffda";
    private static final String SCAN_END = "ffd9";
    private static final int BUFFER_SIZE = 32768;

    public static void main(String[] args) throws IOException {

        InputStream is = null;
        OutputStream os = null;

        String inputFileName = null;
        int numberOfCopies = 0;
        int numberOfBytes = 0;
        boolean swapBytesAcrossAllSections = false;

        createDirectory();

        System.out.println("pic-glitcher " + VERSION);
        if (args.length == 4) {
            inputFileName = args[0];
            numberOfCopies = Integer.parseInt(args[1]);
            numberOfBytes = Integer.parseInt(args[2]);
            swapBytesAcrossAllSections = Boolean.getBoolean(args[3]);
        } else {
            System.out.println("No arguments provided on start.");
            System.out.println("Correct order to run pic-glitcher is java -jar picture.jpg 500 2 false");
            System.out.println("Where picture.jpg stands for the picture to glitch");
            System.out.println("500 is the number of new, randomly glitched images");
            System.out.println("2 is the number of bytes to swap per one image (suggested 2 or more)");
            System.out.println("false is an argument for swapping bytes not only from image scan section");
            System.exit(0);
        }

        System.out.println("Started reading input bytes.");

        is = new FileInputStream(new File(inputFileName));
        byte[] buffer = new byte[BUFFER_SIZE];
        is.read(buffer);
        is.close();

        int beginIndex = 0;
        int endIndex = 0;

        //Wyszukuje ostatnie wystapienie
        for (int a = 0; a < buffer.length; a++) {
            if (Integer.toHexString(buffer[a]).contains(SCAN_END))
                endIndex = a;
        }
        //Wyszukuje pierwsze wystapienie
        for (int a = 0; a < buffer.length; a++) {
            if (Integer.toHexString(buffer[a]).contains(SCAN_BEGINNING)) {
                beginIndex = a;
                break;
            }
        }

        for (int b = 0; b < numberOfCopies; b++) {

            byte[] tempBuffer = buffer.clone();

            os = new FileOutputStream(new File("pic-glitcher-pictures/copy-" + b + ".jpg"));

            for (int g = 0; g < numberOfBytes; g++) {
                int whichToChange = 0;
                if (swapBytesAcrossAllSections)
                    whichToChange = ThreadLocalRandom.current().nextInt(0, buffer.length);
                else
                    whichToChange = ThreadLocalRandom.current().nextInt(beginIndex, endIndex);

                byte valueOfSwappedByte = (byte) ThreadLocalRandom.current().nextInt(-127, 128 + 1);
                tempBuffer[whichToChange] = valueOfSwappedByte;
            }
            os.write(tempBuffer);
            os.close();
        }
    }


    private static void createDirectory() {
        File file = new File("pic-glitcher-pictures");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created.");
            } else {
                System.out.println("Failed to create directory.");
            }
        }
    }
}