/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vectorworld;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 *
 * @author zachary
 */
public class ImageLoader {

    String IMAGE_DIR = "";
    private HashMap imagesMap, gNamesMap;
    private GraphicsConfiguration gc;
    private int transparency;

    public ImageLoader(String imgconf) throws ConfigException, FileNameException {
        super();
        initLoader();
        loadImagesFile(imgconf);
    }

    public ImageLoader() {
        super();
        initLoader();
    }

    private void initLoader() {
        imagesMap = new HashMap();
        gNamesMap = new HashMap();

        gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }
    
    public ColorModel getColorModel() {
        return gc.getColorModel();
    }

    public BufferedImage getSprite(String key) throws NullPointerException {
        if (VectorWorld.DEBUG_GFX) {
            System.out.println("Trying to get the sprite with key "
                    + key + "...");
        }
        BufferedImage bi = (BufferedImage) imagesMap.get(key);
        if (bi == null) {
            if(VectorWorld.DEBUG_GFX) {
                System.err.println("Failed to get the sprite.");
            }
            throw new NullPointerException("No image for key: " + key);
        } else  if(VectorWorld.DEBUG_GFX) {
            System.out.println("Got the sprite.");
        }
        return bi;
    }

    public boolean ready() {
        return !imagesMap.isEmpty();
    }

    private void loadImagesFile(String fnm) throws ConfigException, FileNameException {
        String conffile = fnm;
        InputStream in = this.getClass().getResourceAsStream(fnm);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        if (VectorWorld.DEBUG_LOAD_IMG) {
            System.out.println("Reading image configuration file "
                    + conffile);
            if(in == null) {
                System.out.println("The input stream didn't instantiate");
            }
            if(br == null) {
                System.out.println("The buffered reader didn't instantiate");
            }
        }
        
        
        try {
            String line = br.readLine();
            if(VectorWorld.DEBUG_LOAD_IMG) {
                System.out.println("Processing the line: \n" +
                        line);
            }
            int lineno = 0;
            char ch;
            while (line != null) {
                try {
                    ch = Character.toLowerCase(line.charAt(0));
                    if (ch == 'o') {
                        getFileNameImage(line);
                    } else if (ch == 'n') {
                        getNumberedImages(line);
                    } else if (ch == 'g') {
                        getGroupImages(line);
                    } else if (line.isEmpty() || line.startsWith("#")) {
                        //walk on by
                    } else {
                        throw new ConfigException("Line has no valid character"
                                + " at the start: " + line);
                    }
                } catch (ConfigException e) {
                    System.err.println("Error reading line " + lineno
                            + "\n" + e);
                }
                if(VectorWorld.DEBUG_LOAD_IMG) {
                    System.out.println("Line #" + lineno + " processed.");
                }
                lineno++;
                line = br.readLine();
            }
            br.close();
            if(VectorWorld.DEBUG_LOAD_IMG) {
                System.out.println("Done loading images.");
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + conffile
                    + "\n" + e);
            System.exit(1);
        }
    }

    private void getGroupImages(String line) throws ConfigException,
            IOException {
        String[] tokens = line.split("\\s");
        if (tokens.length < 3) {
            throw new ConfigException("Wrong number of arguments: "
                    + line);
        }
        for (int i = 1; i < tokens.length; i++) {
            loadImage(tokens[i]);
        }
    }

    private void getNumberedImages(String line) throws ConfigException,
            IOException, FileNameException {
        String[] tokens = line.split("\\s");

        if (tokens.length != 3) {
            throw new ConfigException("Line has wrong number of arguments: "
                    + line);
        }

        String prefix = getPrefix(tokens[1]);
        String suffix = line.replaceFirst(prefix, "");
        //Just in case the extension contains numbers.
        //Far-fetched but what the hell.
        while ("0123456789".contains(suffix.substring(0, 1))) {
            suffix = suffix.substring(1, suffix.length());
        }
        int start = prefix.length();
        int stop = tokens[1].length() - suffix.length();
        String num = tokens[1].substring(start, stop);
        int numImages = Integer.parseInt(num);

        for (int i = 0; i < numImages; i++) {
            loadImage(prefix + i + suffix);
        }
    }

    private void getFileNameImage(String line) throws ConfigException {
        if(VectorWorld.DEBUG_LOAD_IMG) {
            System.out.println("Getting image filename given by line:");
        }
        String[] tokens = line.split("[ \t]+");
        if(VectorWorld.DEBUG_LOAD_IMG) {
            Puzzle.describeLoadLine(tokens);
        }
        if (tokens.length != 2) {
            throw new ConfigException("Wrong number of arguments for " + line);
        } else {
            loadSingleImage(tokens[1]);
        }
    }

    public void loadSingleImage(String fnm) throws ConfigException {
        String name;
        if(VectorWorld.DEBUG_LOAD_IMG) {
            System.out.println("Loading image from file: " +
                    IMAGE_DIR + fnm);
        }
        try {
            name = getPrefix(fnm);
        } catch (FileNameException e) {
            throw new ConfigException("Could not get the image from the file: "
                    + fnm + "\n" + e);
        }
        if (imagesMap.containsKey(name)) {
            throw new ConfigException("Image has already been loaded: "
                    + fnm);
        }
        try {
            BufferedImage bi = loadImage(fnm);
            imagesMap.put(name, bi);
            System.out.println("Loaded image " + name);
        } catch (IOException ex) {
            throw new ConfigException(ex);
        }

    }

    private BufferedImage loadImage(String fnm) throws IOException {
        if(VectorWorld.DEBUG_LOAD_IMG) {
            System.out.println("Loading image file " + fnm + " from directory "
                    + IMAGE_DIR);
        }
        BufferedImage copy = null;
        try {
            InputStream in = this.getClass().getResourceAsStream( 
                    IMAGE_DIR+fnm);
            if(in == null) {
                throw new IOException("Couldn't open the image:\n" +
                        IMAGE_DIR + fnm);
            } else if(VectorWorld.DEBUG_LOAD_IMG) {
                System.out.println("Opened the image.");
            }
            BufferedImage im = ImageIO.read(in);
            if (im == null) {
                throw new IOException("Couldn't read the image:\n"
                        + IMAGE_DIR + fnm);
            } else if(VectorWorld.DEBUG_LOAD_IMG) {
                System.out.println("Read the image.");
            }
            // An image returned from ImageIO in J2SE <= 1.4.2 is 
            // _not_ a managed image, but is after copying!

            transparency = im.getColorModel().getTransparency();
            copy = gc.createCompatibleImage(
                    im.getWidth(), im.getHeight(),
                    transparency);
            // create a graphics context
            Graphics2D g2d = copy.createGraphics();
            if(VectorWorld.DEBUG_LOAD_IMG) {
                System.out.println("Got the graphics context. Drawing...");
            }
            g2d.drawImage(im, 0, 0, null);
            g2d.dispose();
            return copy;
        } catch (Exception e) {
            throw new IOException("Load Image error for "
                    + IMAGE_DIR + "/" + fnm + ":\n" + e + "\n"
                    + "transparency is " + transparency);
        }
    }

    private String getPrefix(String fnm) throws FileNameException {
        int startExt = fnm.lastIndexOf(".");
        if (startExt < 0) {
            throw new FileNameException("File has no extension: " + fnm);
        }
        return fnm.substring(0, startExt);
    }

    private void loadImgFile(String f) {
        String absPath = IMAGE_DIR + f;
        System.out.println("Reading " + absPath);
        try {
            InputStream in = this.getClass().getResourceAsStream(absPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = br.readLine();
            char ch;
            while (line != null) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("//")) {
                    continue;
                }
                ch = Character.toLowerCase(line.charAt(0));
                if (ch == 'o') {
                    getFileNameImage(line);
                }


            }
        } catch (Exception e) {
            System.err.println("Unspecified error in method "
                    + "loadImgFile of class ImageLoader");
        }
    }
}