package org.apoiasuas.cargaFamilias

import fr.opensagres.odfdom.converter.pdf.PdfConverter
import fr.opensagres.odfdom.converter.pdf.PdfOptions
import org.apache.commons.io.FileUtils
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.odftoolkit.odfdom.doc.OdfTextDocument

/**
 * Created by clessio on 28/12/2018.
 */
class TestePdf {

    static void geraPdf(File origem) {
        // 1) Load DOCX into XWPFDocument
        InputStream ist = new FileInputStream(origem);
//        XWPFDocument document = new XWPFDocument(ist);
        OdfTextDocument document = OdfTextDocument.loadDocument(ist);

        // 2) Prepare Pdf options
        PdfOptions options = PdfOptions.create();

        // 3) Convert XWPFDocument to Pdf
        OutputStream out = new FileOutputStream(new File(origem.absolutePath+".pdf"));
        PdfConverter.getInstance().convert(document, out, options);
    }

    static void main(String[] args) {
        File folder = new File("C:\\temp\\old\\teste");
//        FileUtils.listFiles(folder, new String[] { 'odt' }, true).each {
        folder.listFiles(new FilenameFilter() {
                 public boolean accept(File dir, String filename)
                      { return filename.endsWith(".odt"); }
        } ).each {
            geraPdf(it);
        }
    }

}
