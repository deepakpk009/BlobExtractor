/* -----------------------------------
 * Blob Extractor v 0.0.1
 * -------------------------------------
 * a java based blob extractor
 * -------------------------------------
 * Developed By : deepak pk
 * Email : deepakpk009@yahoo.in
 * -------------------------------------
 * This Project is Licensed under LGPL
 * -------------------------------------
 *
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.deepak.blobextractor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author deepak
 */

/*
 * the class which provides methods for blob extraction from an binary image
 *
 * blob extraction based on labeling process using a 3x3 kernel
 */
public class BlobExtractor {

    // the source image
    private BufferedImage sourceImage = null;
    // the blob colour in image
    public static final int BLOBCOLOR = Color.black.getRGB();
    // the background color in image
    public static final int BACKGROUNDCOLOR = Color.white.getRGB();
    // new background color for processing
    private static final int WHITE = 0;
    // new blob color for processing
    private static final int BLACK = 1;
    // the label index
    // this is initialised to 2 as it will be incremented durying processing
    // and it should not ever be equal to new blob or background color
    // if it does then it will hinder the labeling process
    private int labelIndex = 2;
    // counter for core processing of the image pixel
    private int coreProcessingCount = 0;

    /*
     * method to load new color values to the binary image
     */
    private void loadNewColorValues() {
        // for all pixels in the image
        for (int y = 0; y < sourceImage.getHeight(); y++) {
            for (int x = 0; x < sourceImage.getWidth(); x++) {
                // if old blob color found then replace it with new blob color
                if (sourceImage.getRGB(x, y) == BLOBCOLOR) {
                    sourceImage.setRGB(x, y, BLACK);
                } // else replace it with new background color
                else {
                    sourceImage.setRGB(x, y, WHITE);
                }
            }
        }
    }

    /*
     * method to extract all blob images from an image
     * has the source image as parameter and outputs an arraylist of
     * detected blob images
     */
    public ArrayList<BufferedImage> extractBlobs(BufferedImage img) {

        // create a blob image list
        ArrayList<BufferedImage> blobList = null;

        // check whether the image is a valid image
        if (isValidImage(img)) {
            // take the copy of the original image
            // the type is set to int argb insted of the original imags byte binary as
            // durying processing pixel rgb values are to be replaced by the label values
            this.sourceImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            // draw the source image on to the created copy
            this.sourceImage.getGraphics().drawImage(img, 0, 0, null);
            System.out.println("Image Copied.");

            // now load new color values for the source image
            loadNewColorValues();
            System.out.println("New color values set.");

            // start labeling blobs
            System.out.println("Labeling Started");
            // the no of pass counter
            int pass = 0;
            while (doLabeling()) {
                pass++;
            }
            System.out.println("Labeling Completed.");
            System.out.println("Labeling Pass Count:" + pass);
            System.out.println("Core processing count:" + coreProcessingCount);

            // initialise the blob image array list
            blobList = new ArrayList<BufferedImage>();
            // for every labeled blob get the blob and load it onto the blobs list
            for (Integer i : getLabels()) {
                blobList.add(getBlob(i));
            }
        }
        // return the blob list
        return blobList;
    }

    /*
     * method to get the lable list from labeled source image
     */
    private ArrayList<Integer> getLabels() {
        // create a label list
        ArrayList<Integer> labelList = new ArrayList<Integer>();

        // for every pixel in the source image
        for (int y = 0; y < sourceImage.getHeight(); y++) {
            for (int x = 0; x < sourceImage.getWidth(); x++) {
                // get pixel label
                // check if present in label list; if present then dont add
                // else add it to the label list
                if (sourceImage.getRGB(x, y) != WHITE && labelList.indexOf(Integer.valueOf(sourceImage.getRGB(x, y))) == -1) {
                    labelList.add(sourceImage.getRGB(x, y));
                }
            }
        }
        System.out.println("Label List Size: " + labelList.size());
        // return the label list
        return labelList;
    }

    /*
     * method to check whether the inputed image is a valid image or not
     */
    public boolean isValidImage(BufferedImage img) {
        // the image should not be null
        if (img == null) {
            System.out.println("Error : Invalid Image! null image.");
            return false;
        } // the image resolution should not be less than 9x9
        else if (img.getWidth() < 9 || img.getHeight() < 9) {
            System.out.println("Error : Invalid Image! minimum image resolution should be 9X9.");
            return false;
        } // the image should be of type byte binary
        else if (img.getType() != BufferedImage.TYPE_BYTE_BINARY) {
            System.out.println("Error : Invalid Image! image should be binary image.");
            return false;
        }
        System.out.println("Valid Image Input.");
        // if all the abouve conditions are satisfied then it is a valid image for blob extraction
        return true;
    }

    /*
     * method to get the blob size (resolution) of an blob with the specified label
     */
    private Rectangle getBlobSize(int blobLabel) {

        // x1, y1 are the starting left top most cordinates
        // assign the x1 with the width of the image which is the maximum value
        // and will will be finding out the smallest value for it from the image
        // and the same goes for y1
        int x1 = sourceImage.getWidth();
        int y1 = sourceImage.getHeight();
        // the x2, y2 is the right bottom most cordinate of a blob in an image
        // it is assigned to 0 as we are to find the maximum value for it
        int x2 = 0;
        int y2 = 0;
        int w = 0;
        int h = 0;

        // scan through every pixels for the specified lable
        for (int j = 0; j < sourceImage.getHeight(); j++) {
            for (int i = 0; i < sourceImage.getWidth(); i++) {
                // on fining a pixel with the specified label
                if (sourceImage.getRGB(i, j) == blobLabel) {
                    // if pixel position x is less than x1 then set x1 as the pixel position
                    if (i < x1) {
                        x1 = i;
                    }
                    // if pixel position x is greater than x2 then set x2 as the pixel position
                    if (i > x2) {
                        x2 = i;
                    }
                    // if pixel position y is less than y1 then set y1 as the pixel position
                    if (j < y1) {
                        y1 = j;
                    }
                    // if pixel position y is greater than y2 then set y2 as the pixel position
                    if (j > y2) {
                        y2 = j;
                    }
                }
            }
        }
        // calculate the width and height of the blob in the image
        w = (x2 - x1);
        h = (y2 - y1);
        // if the width or height is zero then reset it to 1
        // this correction has to be done in case of a single pixel line
        // where if it is a horrizontal line then height becomes zero
        // and if it is a vertical line then the width becomes zero
        w = w < 1 ? 1 : w;
        h = h < 1 ? 1 : h;

        System.out.println("Blob Label :" + blobLabel);
        System.out.println("x: " + x1);
        System.out.println("y: " + y1);
        System.out.println("w: " + w);
        System.out.println("h: " + h);
        // return the blobs size (resolution)
        return new Rectangle(x1, y1, w, h);
    }

    /*
     * method to get the blob image for the specified label
     */
    private BufferedImage getBlob(int blobLabel) {
        // get the blob size
        Rectangle blobSize = getBlobSize(blobLabel);
        // create image buffer for blob with the blob size
        BufferedImage blob = new BufferedImage((int) blobSize.getWidth(), (int) blobSize.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        // get the graphics of the blob image buffer
        Graphics blobGraphics = blob.getGraphics();
        // set the color to white
        blobGraphics.setColor(new Color(BACKGROUNDCOLOR));
        // paint the whole buffer with white paint
        blobGraphics.fillRect(0, 0, blob.getWidth(), blob.getHeight());

        // get the blob sub image from the original image
        BufferedImage subSourceImage = sourceImage.getSubimage((int) blobSize.getX(), (int) blobSize.getY(), blob.getWidth(), blob.getHeight());

        // blob pixel counter
        int blobPixelCount = 0;
        // scan through every pixels for the specified lable
        for (int j = 0; j < subSourceImage.getHeight() - 1; j++) {
            for (int i = 0; i < subSourceImage.getWidth() - 1; i++) {
                // on fining a pixel with the specified label
                if (subSourceImage.getRGB(i, j) == blobLabel) {
                    // set the blob pixel to blob color
                    blob.setRGB(i, j, BLOBCOLOR);
                    // increment blob pixel counter
                    blobPixelCount++;
                }
            }
        }
        System.out.println("Blob Pixel Count:" + blobPixelCount);
        // return the blob image
        return blob;
    }

    /*
     * method which does the labeling process on the image
     *
     * returns false if no labeling is done on any pixel int the image
     * which indicates all blobs are labeled completly
     */
    private boolean doLabeling() {

        // flag to indicate pixel labeling
        boolean labelingDoneOnAnyPixel = false;
        // temporary label
        int tempLabel = 0;

        // scan through every pixels of the image for the specified lable
        for (int y = 1; y < sourceImage.getHeight() - 1; y++) {
            for (int x = 1; x < sourceImage.getWidth() - 1; x++) {
                // on fining a pixel with color other than white
                // and if all the adjacent pixels are not the same
                if (sourceImage.getRGB(x, y) > WHITE && !isAllKernelPixelsSame(x, y)) {
                    coreProcessingCount++;
                    // check if adjacent pixels have any label
                    if (doesAdjacentPixelsHaveLabel(x, y)) {
                        // if so then get the smallest weight adjacent label
                        tempLabel = getSmallestWeightedLabelFromKernel(x, y);
                    } else {
                        // if note set temp label to the label index
                        tempLabel = labelIndex;
                        // increment the label index
                        labelIndex++;
                    }
                    // set the temp label to all the pixel of the kernel
                    // check if any pixel has been labled
                    if (labelKernelPixels(x, y, tempLabel) > 0) {
                        // if any pixel is labled then set pixel labeling flag as true;
                        labelingDoneOnAnyPixel = true;
                    }
                }
            }
        }

        // return the pixel labeling flag
        return labelingDoneOnAnyPixel;
    }

    /*
     * method to check whether adjacent pixels have any label or not
     *
     * input is the center pixel of the kernel (1,1) of (3X3 kernel)
     */
    private boolean doesAdjacentPixelsHaveLabel(int x, int y) {
        // return whether if any pixel has any value greater than black
        // indicating a lable
        return sourceImage.getRGB(x - 1, y - 1) > BLACK
                || sourceImage.getRGB(x, y - 1) > BLACK
                || sourceImage.getRGB(x + 1, y - 1) > BLACK
                || sourceImage.getRGB(x - 1, y) > BLACK
                || sourceImage.getRGB(x + 1, y) > BLACK
                || sourceImage.getRGB(x - 1, y + 1) > BLACK
                || sourceImage.getRGB(x, y + 1) > BLACK
                || sourceImage.getRGB(x + 1, y + 1) > BLACK;
    }

    /*
     * method to get the smallest weighted adjacent label from the kernel
     *
     * input is the center pixel of the kernel (1,1) of (3X3 kernel)
     */
    private int getSmallestWeightedLabelFromKernel(int x, int y) {
        // the min label value is initilised to the maximum value that is the value of the label index.
        int minLabel = labelIndex;

        /*
         * check for every pixel in the kernel whether they have any label.
         * if it has a label, check whether the valule of the label is smaller than the minLabel value
         * if smaller value, then assign it to the minLabel
         */
        if (sourceImage.getRGB(x - 1, y - 1) > BLACK && minLabel > sourceImage.getRGB(x - 1, y - 1)) {
            minLabel = sourceImage.getRGB(x - 1, y - 1);
        }
        if (sourceImage.getRGB(x, y - 1) > BLACK && minLabel > sourceImage.getRGB(x, y - 1)) {
            minLabel = sourceImage.getRGB(x, y - 1);
        }
        if (sourceImage.getRGB(x + 1, y - 1) > BLACK && minLabel > sourceImage.getRGB(x + 1, y - 1)) {
            minLabel = sourceImage.getRGB(x + 1, y - 1);
        }
        if (sourceImage.getRGB(x - 1, y) > BLACK && minLabel > sourceImage.getRGB(x - 1, y)) {
            minLabel = sourceImage.getRGB(x - 1, y);
        }
        if (sourceImage.getRGB(x, y) > BLACK && minLabel > sourceImage.getRGB(x, y)) {
            minLabel = sourceImage.getRGB(x, y);
        }
        if (sourceImage.getRGB(x + 1, y) > BLACK && minLabel > sourceImage.getRGB(x + 1, y)) {
            minLabel = sourceImage.getRGB(x + 1, y);
        }
        if (sourceImage.getRGB(x - 1, y + 1) > BLACK && minLabel > sourceImage.getRGB(x - 1, y + 1)) {
            minLabel = sourceImage.getRGB(x - 1, y + 1);
        }
        if (sourceImage.getRGB(x, y + 1) > BLACK && minLabel > sourceImage.getRGB(x, y + 1)) {
            minLabel = sourceImage.getRGB(x, y + 1);
        }
        if (sourceImage.getRGB(x + 1, y + 1) > BLACK && minLabel > sourceImage.getRGB(x + 1, y + 1)) {
            minLabel = sourceImage.getRGB(x + 1, y + 1);
        }

        // return the minLabel value
        return minLabel;
    }

    /*
     * method to set lables to the kernel pixels
     *
     * input is the center pixel of the kernel (1,1) of (3X3 kernel) and the label value
     */
    private int labelKernelPixels(int x, int y, int newLabel) {
        // the labeling counter initilised to 0
        int labelingCount = 0;

        // if a pixel value is greater than white (which indicates black or any label) and not equal to the input label
        // and
        // if it is greater than black (which means labeled pixel) then
        //     replace all occurance of that label with the input new label
        // else set that pixel value to the input label
        if (sourceImage.getRGB(x - 1, y - 1) > WHITE && sourceImage.getRGB(x - 1, y - 1) != newLabel) {
            if (sourceImage.getRGB(x - 1, y - 1) > BLACK) {
                replaceLabel(sourceImage.getRGB(x - 1, y - 1), newLabel);
            } else {
                sourceImage.setRGB(x - 1, y - 1, newLabel);
            }
            labelingCount++;
        }
        if (sourceImage.getRGB(x, y - 1) > WHITE && sourceImage.getRGB(x, y - 1) != newLabel) {
            if (sourceImage.getRGB(x, y - 1) > BLACK) {
                replaceLabel(sourceImage.getRGB(x, y - 1), newLabel);
            } else {
                sourceImage.setRGB(x, y - 1, newLabel);
            }
            labelingCount++;
        }
        if (sourceImage.getRGB(x + 1, y - 1) > WHITE && sourceImage.getRGB(x + 1, y - 1) != newLabel) {
            if (sourceImage.getRGB(x + 1, y - 1) > BLACK) {
                replaceLabel(sourceImage.getRGB(x + 1, y - 1), newLabel);
            } else {
                sourceImage.setRGB(x + 1, y - 1, newLabel);
            }
            labelingCount++;
        }
        if (sourceImage.getRGB(x - 1, y) > WHITE && sourceImage.getRGB(x - 1, y) != newLabel) {
            if (sourceImage.getRGB(x - 1, y) > BLACK) {
                replaceLabel(sourceImage.getRGB(x - 1, y), newLabel);
            } else {
                sourceImage.setRGB(x - 1, y, newLabel);
            }
            labelingCount++;
        }
        if (sourceImage.getRGB(x, y) > WHITE && sourceImage.getRGB(x, y) != newLabel) {
            if (sourceImage.getRGB(x, y) > BLACK) {
                replaceLabel(sourceImage.getRGB(x, y), newLabel);
            } else {
                sourceImage.setRGB(x, y, newLabel);
            }
            labelingCount++;
        }
        if (sourceImage.getRGB(x + 1, y) > WHITE && sourceImage.getRGB(x + 1, y) != newLabel) {
            if (sourceImage.getRGB(x + 1, y) > BLACK) {
                replaceLabel(sourceImage.getRGB(x + 1, y), newLabel);
            } else {
                sourceImage.setRGB(x + 1, y, newLabel);
            }
            labelingCount++;
        }
        if (sourceImage.getRGB(x - 1, y + 1) > WHITE && sourceImage.getRGB(x - 1, y + 1) != newLabel) {
            if (sourceImage.getRGB(x - 1, y + 1) > BLACK) {
                replaceLabel(sourceImage.getRGB(x - 1, y + 1), newLabel);
            } else {
                sourceImage.setRGB(x - 1, y + 1, newLabel);
            }
            labelingCount++;
        }
        if (sourceImage.getRGB(x, y + 1) > WHITE && sourceImage.getRGB(x, y + 1) != newLabel) {
            if (sourceImage.getRGB(x, y + 1) > BLACK) {
                replaceLabel(sourceImage.getRGB(x, y + 1), newLabel);
            } else {
                sourceImage.setRGB(x, y + 1, newLabel);
            }
            labelingCount++;
        }
        if (sourceImage.getRGB(x + 1, y + 1) > WHITE && sourceImage.getRGB(x + 1, y + 1) != newLabel) {
            if (sourceImage.getRGB(x + 1, y + 1) > BLACK) {
                replaceLabel(sourceImage.getRGB(x + 1, y + 1), newLabel);
            } else {
                sourceImage.setRGB(x + 1, y + 1, newLabel);
            }
            labelingCount++;
        }
        // retunr the labeling count
        return labelingCount;
    }

    /*
     * method to check whether the pixels of the kernel are all same
     */
    private boolean isAllKernelPixelsSame(int x, int y) {
        // get the sum of the values of all kernel pixels
        int sumOfAllKernelPixelValues = sourceImage.getRGB(x - 1, y - 1)
                + sourceImage.getRGB(x, y - 1)
                + sourceImage.getRGB(x + 1, y - 1)
                + sourceImage.getRGB(x - 1, y)
                + sourceImage.getRGB(x, y)
                + sourceImage.getRGB(x + 1, y)
                + sourceImage.getRGB(x - 1, y + 1)
                + sourceImage.getRGB(x, y + 1)
                + sourceImage.getRGB(x + 1, y + 1);

        // if the average equals to the value of any one then all pixels are of same value
        // here dividing by 9 as kernel has 9 pixels (3x3)
        if (sumOfAllKernelPixelValues / 9 == sourceImage.getRGB(x, y)) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * method to replace all occurance of the old label with the new one
     */
    private void replaceLabel(int oldLabel, int newLabel) {
        for (int y = 0; y < sourceImage.getHeight(); y++) {
            for (int x = 0; x < sourceImage.getWidth(); x++) {
                // if old label found
                if (sourceImage.getRGB(x, y) == oldLabel) {
                    // replace it with the new label
                    sourceImage.setRGB(x, y, newLabel);
                }
            }
        }
    }
}
