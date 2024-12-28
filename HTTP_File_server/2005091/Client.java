import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client implements Runnable {
    private Socket socket;
    private File file;

    public Client(Socket socket, File file) {
        this.socket=socket;
        this.file=file;
    }

    public static void main(String[] args) throws IOException {

        Scanner scanner=new Scanner(System.in);
        while (true)
        {
            System.out.println("Enter file name:\n");
            String fileName=scanner.nextLine();
            File file = new File(fileName);
            Socket s = new Socket("localhost",5091);
            Client client = new Client(s, file);
            Thread thread = new Thread(client);
            thread.start();
        }
    }

    @Override
    public void run() {
        try {
            OutputStream outputStream=socket.getOutputStream();
            if(file.exists())
            {
                try {
                    byte[] buffer = new byte[4096];
                    FileInputStream fileInputStream = new FileInputStream(file);
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    String string="";
                    string+="UPLOAD "+file.getName();
                    string+="\r\n";
                    outputStream.write(string.getBytes(StandardCharsets.UTF_8));
                    int length;
                    while((length=fileInputStream.read(buffer))>0)
                    {
                        dataOutputStream.write(buffer, 0, length);
                    }
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    fileInputStream.close();
                    outputStream.flush();
                    System.out.println("File Uploaded");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            else
            {
                try {
                    String string="";
                    string+="NOTFOUND";
                    string+="\r\n";
                    outputStream.write(string.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("File not found");
            }
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
