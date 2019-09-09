package chat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javafx.scene.control.CheckBox;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**
 * A simple Swing-based client for the chat server.  Graphically
 * it is a frame with a text field for entering messages and a
 * textarea to see the whole dialog.
 *
 * The client follows the Chat Protocol which is as follows.
 * When the server sends "SUBMITNAME" the client replies with the
 * desired screen name.  The server will keep sending "SUBMITNAME"
 * requests as long as the client submits screen names that are
 * already in use.  When the server sends a line beginning
 * with "NAMEACCEPTED" the client is now allowed to start
 * sending the server arbitrary strings to be broadcast to all
 * chatters connected to the server.  When the server sends a
 * line beginning with "MESSAGE " then all characters following
 * this string should be displayed in its message area.
 */
public class ChatClient {

    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Chatter");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);
    //create list
    JList list;
    DefaultListModel model;
    //create checkbox
    JCheckBox checkBox;
    

    /**
     * Constructs the client by laying out the GUI and registering a
     * listener with the textfield so that pressing Return in the
     * listener sends the textfield contents to the server.  Note
     * however that the textfield is initially NOT editable, and
     * only becomes editable AFTER the client receives the NAMEACCEPTED
     * message from the server.
     */
    public ChatClient() {

        // Layout GUI
        model = new DefaultListModel();
        list = new JList(model);
        
        //Enable selecting multiple items fron the JList without pressing ctrl
        list.setSelectionModel(new DefaultListSelectionModel()
        {
            @Override
            public void setSelectionInterval(int index0, int index1) 
            {
                if(super.isSelectedIndex(index0)) 
                {
                    super.removeSelectionInterval(index0, index1);
                }
            else 
            {
                super.addSelectionInterval(index0, index1);
            }
            }
        });
        
        //checkbox value
        checkBox=new JCheckBox("Send multiple users");
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "West");
        frame.getContentPane().add(new JScrollPane(list), "East");
        frame.getContentPane().add(new JScrollPane(checkBox), "South");
        frame.pack();
        
        
              

        // Add Listeners
        textField.addActionListener(new ActionListener() {
            /**
             * Responds to pressing the enter key in the textfield by sending
             * the contents of the text field to the server.    Then clear
             * the text area in preparation for the next message.
             */
            public void actionPerformed(ActionEvent e) {
                
                
                //list to store the names of the selected users
                List<String> list1=list.getSelectedValuesList();
                //System.out.println("CB "+checkBox.isSelected());
               // System.out.println("List"+list1.isEmpty());
                //System.out.println(checkBox.isSelected() && !list1.isEmpty());
                
                String selectedUsers="SELECTEDUSERS";
                //check whether check box is check and users are selected
                if(checkBox.isSelected() && !list1.isEmpty())
                {
                    for(String x : list1)
                    {
                        selectedUsers=selectedUsers+x+",";
                    }
                    //send the users to the server in order to multicast the message
                    out.println(selectedUsers+" "+textField.getText());
                    textField.setText("");
                }
                else
                {
                    out.println(textField.getText());
                    textField.setText("");
                }
            }
            
        });
        
        
    }

    /**
     * Prompt for and return the address of the server.
     */
    private String getServerAddress() {
        return JOptionPane.showInputDialog(
            frame,
            "Enter IP Address of the Server:",
            "Welcome to the Chatter",
            JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Prompt for and return the desired screen name.
     */
    private String getName() {
        return JOptionPane.showInputDialog(
            frame,
            "Choose a screen name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Connects to the server then enters the processing loop.
     */
    private void run() throws IOException {

        // Make connection and initialize streams
        String serverAddress = getServerAddress();
        Socket socket = new Socket(serverAddress, 9001);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        while (true) {
            String line = in.readLine();
            //String onlineUsers=in.readLine();
            if (line.startsWith("SUBMITNAME")) {
                out.println(getName());
            } else if (line.startsWith("NAMEACCEPTED")) {
                textField.setEditable(true);
            } else if (line.startsWith("MESSAGE")) {
                messageArea.append(line.substring(8) + "\n");
            }
            //show online users in the JList
            else if(line.startsWith("ONLINEUSERS"))
            {
                //refresh the JList once a new user has joined
                if(model.getSize()!=0)
                {
                    model.clear();
                }
                
                //add usernames to the JList
                StringTokenizer tk=new StringTokenizer((line.substring(11)),",");
                while(tk.hasMoreTokens())
                {
                    model.addElement(tk.nextToken());
                }
                
                
            }
            //show the name of the online user in the title of the JFrame
            else if(line.startsWith("CLIENT"))
            {
                frame.setTitle("User : "+line.substring(6));
            }
             
        }
    }

    /**
     * Runs the client as an application with a closeable frame.
     */
    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}