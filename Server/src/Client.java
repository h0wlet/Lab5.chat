import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client implements Runnable {
    private Server server;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Client(Socket socket, Server server) {
        try {
            this.server = server;
            this.in = new ObjectInputStream(socket.getInputStream());
            this.out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                server.sendMessageToAll(new Message("SERVER","New client connected!"));
                break;
            }

            while (true) {
                if (in != null) {
                    Message clientMessage = (Message) in.readObject();
                    server.sendMessageToAll(clientMessage);
                }
                Thread.sleep(100);
            }
        }
        catch (InterruptedException | IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        finally {
            this.close();
        }
    }
    
    public void sendMsg(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void close() {
        server.removeClient(this);
    }
}