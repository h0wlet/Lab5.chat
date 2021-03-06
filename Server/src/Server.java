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
                addClient(client);
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

    public synchronized void addClient(Client client) {
        clients.add(client);
    }

    public synchronized void removeClient(Client client) {
        clients.remove(client);
    }

    public void sendMessageToAll(Message msg) {
        for (Client o : clients) {
            o.sendMsg(msg);
        }
    }


}