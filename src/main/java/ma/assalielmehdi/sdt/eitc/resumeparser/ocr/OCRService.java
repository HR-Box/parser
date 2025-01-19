package ma.assalielmehdi.sdt.eitc.resumeparser.ocr;

import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class OCRService {
  public String imagesToText(BufferedImage[] images) {
    var tesseract = new Tesseract1();
    tesseract.setDatapath("tessdata");

    try {
      var text = new StringBuilder();

      for (var image : images) {
        var imageText = tesseract.doOCR(image);
        text.append(imageText).append("\n");
      }

      return text.toString();
    } catch (TesseractException e) {
      throw new OCRException("OCR Error: " + e.getMessage());
    }
  }

  public String pdfToText(byte[] pdfBytes) {
    var pdfImages = pdfToImages(pdfBytes);
    return imagesToText(pdfImages);
  }

  private BufferedImage[] pdfToImages(byte[] pdfBytes) {
    try (var document = Loader.loadPDF(pdfBytes)) {
      var pdfRenderer = new PDFRenderer(document);
      var images = new BufferedImage[document.getNumberOfPages()];

      for (int page = 0; page < document.getNumberOfPages(); ++page) {
        images[page] = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
      }

      return images;
    } catch (IOException e) {
      throw new OCRException("OCR Error: " + e.getMessage());
    }
  }
}
