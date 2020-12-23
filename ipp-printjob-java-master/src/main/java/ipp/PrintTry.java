package ipp;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;

public class PrintTry {
    public static void main(String[] args){
        try{
            URI uri = new URI("ipp://192.168.223.1:9100");
            File file = new File("E:\\new\\binary\\binary");

            if (args.length > 0) {
                uri = new URI(args[0]);
            }
            if (args.length > 1) {
                file = new File(args[1]);
            }
            new PrintTry(uri).printJob(file);
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
        //httpUrlConnection.setRequestProperty("Content-Type", "application/ipp");

        dataOutputStream=new DataOutputStream(httpUrlConnection.getOutputStream());
        dataOutputStream.write(27);
        //dataOutputStream.write(27); //esc
        dataOutputStream.write("%-12345X@PJL\n".getBytes());
        dataOutputStream.write("hello test".getBytes());
        dataOutputStream.write(27); //esc
        dataOutputStream.write("%-12345X".getBytes());

        new FileInputStream(file).transferTo(dataOutputStream);

        dataInputStream = new DataInputStream(httpUrlConnection.getInputStream());
        System.out.println(readStringValue());

    }

    private URI uri;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    PrintTry(URI uri){
        this.uri=uri;
    }

    private String readStringValue() throws IOException {
        byte[] valueBytes = new byte[dataInputStream.readShort()];
        dataInputStream.read(valueBytes);
        return new String(valueBytes);
        // Java 11: return dataInputStream.readNBytes(dataInputStream.readShort());
    }
}
