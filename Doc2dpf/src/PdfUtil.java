import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PdfUtil {

    private static boolean getLicense() {
        boolean result = false;
        try {

            //String licenseStr = "xml内容";
            String licenseStr = "<License>\n" +
                    "<Data>\n" +
                    "<Products>\n" +
                    "<Product>Aspose.Total for Java</Product>\n" +
                    "<Product>Aspose.Words for Java</Product>\n" +
                    "</Products>\n" +
                    "<EditionType>Enterprise</EditionType>\n" +
                    "<SubscriptionExpiry>20991231</SubscriptionExpiry>\n" +
                    "<LicenseExpiry>20991231</LicenseExpiry>\n" +
                    "<SerialNumber>8bfe198c-7f0c-4ef8-8ff0-acc3237bf0d7</SerialNumber>\n" +
                    "</Data>\n" +
                    "<Signature>sNLLKGMUdF0r8O1kKilWAGdgfs2BvJb/2Xp8p5iuDVfZXmhppo+d0Ran1P9TKdjV4ABwAgKXxJ3jcQTqE/2IRfqwnPf8itN8aFZlV3TJPYeD3yWE7IT55Gz6EijUpC7aKeoohTb4w2fpox58wWoF3SNp6sK6jDfiAUGEHYJ9pjU=</Signature>\n" +
                    "</License>";
            InputStream license = new ByteArrayInputStream(licenseStr.getBytes("UTF-8"));
            License asposeLic = new License();

            asposeLic.setLicense(license);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void doc2pdf(String wordPath, String pdfPath) {
        if (!getLicense()) { // 验证License 若不验证则转化出的pdf文档会有水印产生
            return;
        }
        try {
            long old = System.currentTimeMillis();
            File file = new File(pdfPath); //新建一个pdf文档
            FileOutputStream os = new FileOutputStream(file);
            Document doc = new Document(wordPath); //Address是将要被转化的word文档
            doc.save(os, com.aspose.words.SaveFormat.PDF);//全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            long now = System.currentTimeMillis();
            os.close();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); //转化用时
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void docTurnPdf(String sourceFileName,String newFileName) throws Exception {
        if (!getLicense()) {// 验证License 若不验证则转化出的pdf文档会有水印产生
            return;
        }
        File file = new File(newFileName);  //新建一个空白pdf文档
        FileOutputStream os = new FileOutputStream(file);
        Document doc = new Document(sourceFileName);//Address是将要被转化的word文档
        doc.save(os, SaveFormat.PDF);//全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
        os.close();
        //删除doc文件，用不到就删掉
        File f = new File(sourceFileName);
        if(f.exists()){
            f.delete();
        }
    }

    public static void main(String[] args) {
        PdfUtil.doc2pdf("D:\\test\\mytest.docx","D:\\test\\test.pdf");
        //docTurnPdf
    }
}