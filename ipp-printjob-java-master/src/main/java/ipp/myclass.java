package ipp;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class myclass {

    public static void main(String[] args){
        try{
            URI uri = new URI("ipp://192.168.223.1/ipp/print");
            File file = new File("E:\\new\\binary\\binary");

            if (args.length > 0) {
                uri = new URI(args[0]);
            }
            if (args.length > 1) {
                file = new File(args[1]);
            }
            new myclass(uri).printJob(file);
        }catch (Exception exception) {
            exception.printStackTrace(System.err);
        }
    }

    private void printJob(File file) throws IOException {
        System.out.println(String.format("send %s to %s", file.getName(), uri));
        String httpScheme = uri.getScheme().replace("ipp", "http");
        URI httpUri = URI.create(String.format("%s:%s", httpScheme, uri.getSchemeSpecificPart()));
        HttpURLConnection httpUrlConnection = (HttpURLConnection) httpUri.toURL().openConnection();
        httpUrlConnection.setConnectTimeout(5000);
        httpUrlConnection.setDoOutput(true);
        //httpUrlConnection.setRequestMethod("POST");
        httpUrlConnection.setRequestProperty("Content-Type", "application/ipp");

        dataOutputStream=new DataOutputStream(httpUrlConnection.getOutputStream());
        //dataOutputStream.writeUTF("\u0002  \u000B\u0002Ра9\u0001G \u0012attributes-charset \u0005utf-8H \u001Battributes-natural-language \u0005en-usE \u000Bprinter-uri .ipp://localhost:631/printers/EPSON_L310_SeriesD \u0014requested-attributes \u0015printer-state-reasons\u0003");
        new FileInputStream(file).transferTo(dataOutputStream);

        dataInputStream = new DataInputStream(httpUrlConnection.getInputStream());
        System.out.println(readStringValue());

    }

    private URI uri;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    myclass(URI uri){
        this.uri=uri;
    }

    private String readStringValue() throws IOException {
        byte[] valueBytes = new byte[dataInputStream.readShort()];
        dataInputStream.read(valueBytes);
        return new String(valueBytes);
        // Java 11: return dataInputStream.readNBytes(dataInputStream.readShort());
    }

}
