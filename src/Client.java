import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private String userName;
    private BufferedReader in;
    private PrintWriter out;

    public Client(Socket socket, String userName) {
        try {
            this.socket = socket;
            this.userName = userName;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
            close(socket, in, out);
        }
    }

    public void sendMessage() {
        out.println(userName);
        out.flush();
        while (socket.isConnected()) {
            Scanner sc = new Scanner(System.in);
            out.println(userName + ": " + sc.nextLine());
        }
    }

    public void listen() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroup;
                while (socket.isConnected()) {
                    try {
                        msgFromGroup = in.readLine();
                        System.out.println(msgFromGroup);
                    } catch (IOException e) {
                        e.printStackTrace();
                        close(socket, in, out);
                    }
                }
            }
        }).start();

    }

    private void close(Socket socket, BufferedReader in, PrintWriter out) {
        try {
            if (socket != null)
                socket.close();
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your userName to join group chat: ");
        String user = sc.nextLine();
        Socket socket = new Socket("localhost",9999);
        Client client = new Client(socket, user);
        client.listen();
        client.sendMessage();
    }
}
