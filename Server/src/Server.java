import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    static final int PORT = 5555;
    private ArrayList<Client> clients = new ArrayList<Client>();

    public Server() {
        Socket clientSocket = null;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server running...");

            while (true) {
                clientSocket = serverSocket.accept();
                Client client = new Client(clientSocket, this);
                clients.add(client);
                new Thread(client).start();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                clientSocket.close();
                serverSocket.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void sendMessageToAll(String msg) {
        for (Client o : clients) {
            o.sendMsg(msg);
        }

    }

    public void removeClient(Client client) {
        clients.remove(client);
    }
}