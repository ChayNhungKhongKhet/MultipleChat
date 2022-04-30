import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private String clientUserName;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(),true);
            clientUserName = in.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: "+ clientUserName +" join group");
            System.out.println(clientUserName+" has join group");
        } catch (IOException e) {
            e.printStackTrace();
            close(socket,in,out);
        }
    }


    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = in.readLine();
                broadcastMessage(messageFromClient);
            }catch (IOException e) {
                close(socket, in, out);
                remove();
                break;
            }
        }
    }

    public void broadcastMessage(String msgToClients) {
        for (ClientHandler client : clientHandlers) {
            if (!client.clientUserName.equals(clientUserName)) {
                client.out.println(msgToClients);
            }
        }
    }

    public void sendMsgToOneClient(String msgToClients, String clientUserName) {
        clientUserName = "aljsla";
    }

    public void remove() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: "+clientUserName+" has left group !!!");
        System.out.println(clientUserName+" has left group !!");
    }

    public void close(Socket socket, BufferedReader in, PrintWriter out) {
        try {
            if (socket != null)
                socket.close();
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
