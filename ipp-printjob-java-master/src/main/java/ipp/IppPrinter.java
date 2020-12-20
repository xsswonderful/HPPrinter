package ipp;

// Author: Gerhard Muth

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

class IppPrinter {

  public static void main(final String[] args) {
    try {
      //URI uri = new URI("ipp://localhost:631/printers/laser");"ipp://169.254.122.5:631/ipp/print"
      //URI uri = new URI("ipp://192.168.223.1:631/ipp/print");
      URI uri = new URI("ipp://192.168.223.1:631/ipp/print");
      File file = new File("demo/A4-blank.pdf");
      //File file=new File("D:\\f3\\three.txt");
      if (args.length > 0) {
        uri = new URI(args[0]);
      }
      if (args.length > 1) {
        file = new File(args[1]);
      }
      new IppPrinter(uri).printJob(file);

    } catch (Exception exception) {
      exception.printStackTrace(System.err);
    }
  }

  private URI uri;
  private DataOutputStream dataOutputStream;
  private DataInputStream dataInputStream;

  IppPrinter(URI uri) {
    this.uri = uri;
  }

  // https://tools.ietf.org/html/rfc8011#section-4.2.1
  public void printJob(final File file) throws IOException {
    System.out.println(String.format("send %s to %s", file.getName(), uri));
    String httpScheme = uri.getScheme().replace("ipp", "http");
    URI httpUri = URI.create(String.format("%s:%s", httpScheme, uri.getSchemeSpecificPart()));
    HttpURLConnection httpUrlConnection = (HttpURLConnection) httpUri.toURL().openConnection();
    httpUrlConnection.setConnectTimeout(5000);
    httpUrlConnection.setDoOutput(true);
    //httpUrlConnection.setRequestMethod("POST");
    httpUrlConnection.setRequestProperty("Content-Type", "application/ipp");

    // rfc 8010 syntax of encoding: https://tools.ietf.org/html/rfc8010#page-15
    dataOutputStream = new DataOutputStream(httpUrlConnection.getOutputStream());
    dataOutputStream.writeShort(0x0101); // ipp version 1.1
    // operation -> https://tools.ietf.org/html/rfc8011#section-5.4.15
    dataOutputStream.writeShort(0x0002); // operation Print-Job
    dataOutputStream.writeInt(0x0001); // request id
    //dataOutputStream.writeShort(0x000b);
    dataOutputStream.writeByte(0x01); // operation group tag
    //dataOutputStream.writeByte(0x04);
    writeAttribute(0x47, "attributes-charset", "us-ascii"); // charset tag
    writeAttribute(0x48, "attributes-natural-language", "en"); // natural-language tag
    writeAttribute(0x45, "printer-uri", uri.toString()); // uri tag
    //writeAttribute(0x14,"requested-attribute","printer-state");
    //writeAttribute(0x14,"requested-attribute","printer-state-reasons");
    dataOutputStream.writeByte(0x03); // end tag

    // append document
//     byte[] buffer = new byte[4096]; // Java <9
//     for (int length; (length = documentInputStream.read(buffer)) != -1; outputStream.write(buffer, 0, length));
    new FileInputStream(file).transferTo(dataOutputStream); // Java >= 9

    // check http response
    if (httpUrlConnection.getResponseCode() != 200) {  //返回200即发送成功，401发送失败
      System.out.println(httpUrlConnection.getResponseMessage());
      System.err.println(new String(httpUrlConnection.getErrorStream().readAllBytes()));
      throw new IOException(String.format("post to %s failed with http status %d", uri, httpUrlConnection.getResponseCode()));
    }
    if (!"application/ipp".equals(httpUrlConnection.getHeaderField("Content-Type"))) {
      throw new IOException(String.format("response is not ipp"));
    }

    // decode ipp response
    dataInputStream = new DataInputStream(httpUrlConnection.getInputStream());
    System.out.println(String.format("version %d.%d", dataInputStream.readByte(), dataInputStream.readByte()));
    // status -> https://www.iana.org/assignments/ipp-registrations/ipp-registrations.xhtml#ipp-registrations-11
    int status=dataInputStream.readShort();
    printStatus(status);
    System.out.println(String.format("status 0x%04X", status));
    //System.out.println(String.format("status 0x%04X", dataInputStream.readShort())); //
    System.out.println(String.format("requestId %d", dataInputStream.readInt()));

    byte tag;
    do {
      tag = dataInputStream.readByte();

      if (tag < 0x10) {
        System.out.println(String.format("group %02X", tag));
        continue;
      }
      String name = readStringValue();
      Object value;
      // value tag -> https://tools.ietf.org/html/rfc8010#section-3.5.2
      switch (tag) {
        case 0x21: // integer
        case 0x23: // enum
          dataInputStream.readShort(); // value length: 4
          value = dataInputStream.readInt();
          break;
        case 0x41: // textWithoutLanguage
        case 0x44: // keyword
        case 0x45: // uri
        case 0x47: // charset
        case 0x48: // naturalLanguage
          value = readStringValue();
          //System.out.println(value);
          break;
        default:
          readStringValue();
          value = "<decoding-not-implemented>";
      }
      System.out.println(String.format(" %s (0x%02X) = %s", name, tag, value));
    } while (tag != (byte) 0x03);
    // job-state -> https://tools.ietf.org/html/rfc8011#section-5.3.7
  }

  private void printStatus(int status) {

    if(status>=0x0000 && status<=0x00ff){
      System.out.println("successful");
    }else if(status>=0x0100 && status<= 0x01ff){
      System.out.println("informational");
    }else if((status>=0x0200 && status<= 0x02ff)||(status>=0x0600 && status>=0x7fff)){
      System.out.println("unassigned");
    }else if(status>=0x0300 && status<= 0x3ff){
      System.out.println("redirection");
    }else if(status >=0x0400 && status<= 0x04ff){
      System.out.println("client-error");
    }else if(status >=0x0500 && status <= 0x05ff){
      System.out.println("server-error");
    }
  }

  private void writeAttribute(final Integer tag, final String name, final String value) throws IOException {
    dataOutputStream.writeByte(tag);
    dataOutputStream.writeShort(name.length());
    dataOutputStream.write(name.getBytes());
    dataOutputStream.writeShort(value.length());
    dataOutputStream.write(value.getBytes());
  }

  private String readStringValue() throws IOException {
    byte[] valueBytes = new byte[dataInputStream.readShort()];
    dataInputStream.read(valueBytes);
    return new String(valueBytes);
    // Java 11: return dataInputStream.readNBytes(dataInputStream.readShort());
  }
}