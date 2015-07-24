/*
This file is part of Blob Extractor v 0.0.1

Blob Extractor is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Blob Extractor is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Blob Extractor.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.deepak.blobextractor.main;

import com.deepak.blobextractor.arrayprocessing.optimized.level1.BlobExtractor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author deepak
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // create an object of the BlobExtractor
        BlobExtractor be = new BlobExtractor();
        // read the input image
        BufferedImage img = ImageIO.read(new File("D:\\blob test\\blob test image.bmp"));
        // create an arrayList of blob images and get all blobs extracted using the BlobExtractor object
        ArrayList<BufferedImage> blobs = be.extractBlobs(img);

        // if blobs are present then save it to the same directory in which the sorce image is present
        if (blobs != null && blobs.size() > 0) {
            // blob counter
            int i = 0;
            // for all blobs
            for (BufferedImage blob : blobs) {
                // write the blob images to a file
                ImageIO.write(blob, "PNG", new File("D:\\blob test\\a\\blob" + i + ".png"));
                // increment blob counter
                i++;
            }
        }
    }
}
