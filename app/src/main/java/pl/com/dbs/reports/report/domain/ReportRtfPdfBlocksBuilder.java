/**
 * 
 */
package pl.com.dbs.reports.report.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.parser.RtfParser;

/**
 * Report blocks builder for rtf formats converted into pdf.
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @coptyright (c) 2014
 */
final class ReportRtfPdfBlocksBuilder extends ReportTextBlocksBuilder {
	private static final Logger logger = Logger.getLogger(ReportRtfPdfBlocksBuilder.class);
	
	ReportRtfPdfBlocksBuilder(final byte[] content) {
		super(content);
	}
	
	@Override
	public ReportRtfPdfBlocksBuilder construct() {
		super.construct();
		convert();
        return this;
	}

	/**
	 * http://blog.rubypdf.com/2009/11/24/convert-rtf-to-pdf-with-open-source-library-itext/
	 * http://stackoverflow.com/questions/1876678/itext-5-0-0-where-did-rtf-and-html-go
	 * 
	 * http://itext-general.2136553.n4.nabble.com/importRtfDocument-Unknown-Source-td2151636.html
	 */
	protected void convert() {
		try {
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, os);
			document.open();
			
			RtfParser parser = new RtfParser(null);
			parser.convertRtfDocument(new ByteArrayInputStream(content), document);
			document.close();
			logger.debug("Pdf document closed");
			content = os.toByteArray();			
		 } catch (DocumentException e) {
			 logger.error("Error converting rtf into pdf." + e.getMessage());
		 } catch (IOException e) {
			 logger.error("Error converting rtf into pdf (content not found)." + e.getMessage());
		 }
	}
}