package com.buaa.pdfpacker;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class PDFPacker {
	private static PDFPacker instance = null;
	public static PDFPacker getInstance() {
		if (instance == null) {
			synchronized (PDFPacker.class) {
				if (instance == null) {
					instance = new PDFPacker();
				}
			}
		}
		return instance;
	}

	private PDFPacker() {}

	/**
	 * Pack images to PDF file.
	 * @param srcDir
	 * @param destDir
	 * @param filename
	 * @throws IOException
	 */
	public void packImagesToPDF(String srcDir, String destDir, String filename) throws IOException {
		File srcDirFile = new File(srcDir);
		File destDirFile = new File(destDir);
		if (!(srcDirFile.exists() && srcDirFile.isDirectory())) {
			throw new IOException();
		}
		if (destDirFile.exists()) {
			if (!destDirFile.isDirectory()) {
				throw new IOException();
			}
		} else {
			destDirFile.mkdirs();
		}

		if (!filename.endsWith(".pdf")) {
			filename += ".pdf";
		}

		File pdfFile = new File(Paths.get(destDir, filename).toString());
		PdfWriter pdfWriter = new PdfWriter(pdfFile);
		PdfDocument pdfDocument = new PdfDocument(pdfWriter);
		Document document = new Document(pdfDocument, PageSize.A4);
		document.setMargins(0f, 0f, 0f, 0f);

		File[] files = srcDirFile.listFiles();
		for (File file : files) {
			ImageData imageData = ImageDataFactory.create(file.getPath());
			Image image = new Image(imageData);
			adjustImage(image);
			document.add(image);
		}

		document.close();
	}

	/**
	 * Adjust image to fit page size.
	 * @param image the image to adjust.
	 */
	private void adjustImage(Image image) {
		float targetWidth = PageSize.A4.getWidth();
		float targetHeight = PageSize.A4.getHeight();

		image.scaleToFit(targetWidth, targetHeight);
		float height = image.getImageScaledHeight();
		float width = image.getImageScaledWidth();

		if (width < targetWidth) {
			float margin = (targetWidth - width) / 2.0f;
			image.setMarginLeft(margin).setMarginRight(margin);
		}

		if (height < targetHeight) {
			float margin = (targetHeight - height) / 2.0f;
			image.setMarginTop(margin).setMarginBottom(margin);
		}
	}
}
