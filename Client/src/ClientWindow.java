import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientWindow extends JFrame {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5555;
    
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    private final JTextField jMessage;
    private final JTextField jName;
    private final JTextArea jText;

    public ClientWindow() {
        try {
            clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        setSize(500, 500);
        setLocationRelativeTo(null);
        setTitle("Chat");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ImageIcon icon = new ImageIcon(getClass().getResource("img.png"));
        setIconImage(icon.getImage());

        jText = new JTextArea();
        jText.setEditable(false);
        jText.setLineWrap(true);

        JScrollPane jsp = new JScrollPane(jText);
        add(jsp, BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        add(panel, BorderLayout.SOUTH);

        JButton jbSendMessage = new JButton("send");
        panel.add(jbSendMessage, BorderLayout.EAST);
        
        jMessage = new JTextField("Your message: ");
        panel.add(jMessage, BorderLayout.CENTER);
        
        jName = new JTextField("Your nickname: ");
        panel.add(jName, BorderLayout.WEST);

        jbSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!jMessage.getText().trim().isEmpty() && !jName.getText().trim().isEmpty()) {
                    try {
                        sendMsg();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    jMessage.grabFocus();
                }
            }
        });

        jMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jMessage.setText("");
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (in.available() != 0) {
                            String inMes = in.readUTF();
                            jText.append(inMes + "\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    out.writeUTF("Client disconnected!");
                    out.flush();
                    out.close();
                    in.close();
                    clientSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        setVisible(true);
    }

    public void sendMsg() throws IOException {
        Message messageStr = new Message(jName.getText(), jMessage.getText());
        out.writeUTF(messageStr.getNickname() + ": " + messageStr.getMessage());
        out.flush();
        jMessage.setText("");
    }
}