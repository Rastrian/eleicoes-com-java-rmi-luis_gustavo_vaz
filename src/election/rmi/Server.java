package election.rmi;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements Election  {
    private static Map<String, Integer> votes = new TreeMap<>();
    private static TreeMap<String, String> voteList = new TreeMap<>();
    private static final TreeMap<String, String> candidates = new TreeMap<>();

    private static final int PORT = 2103;
    private final String SENATORS_CSV = "./data/senadores.csv";
    private final String VOTES_TXT = "./data/votes.txt";
    private final String VOTE_LIST_TXT = "./data/voteList.txt";

    public Server() throws RemoteException {
        super();
        readInitData();
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(PORT);
            Server server = new Server();

            System.out.println("Starting server.");

            registry.rebind("Server", server);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String vote(String electorName, String candidate)
            throws IOException, ClassNotFoundException {
        if (!candidates.containsKey(candidate)) {
            return ("The candidate " + candidate + " doesn't exists.");
        } else if (voteList.containsKey(electorName)) {
            return ("You already had voted.");
        } else if (!voteList.containsKey(electorName) && candidates.containsKey(candidate)) {
            voteList.put(electorName, candidate);
            addOnFIle(VOTE_LIST_TXT);
            votes.replace(candidate, (votes.get(candidate) + 1));
            addOnFIle(VOTES_TXT);

            System.out.println("Elector "+ electorName +" voted on candidate " + candidates.get(candidate));
            return ("Success, vote computed.");
        }
        return ("ERROR");
    }

    @Override
    public String result(String candidate) throws IOException, ClassNotFoundException {

        if (!votes.containsKey(candidate)) return ("The candidate " + candidate + " doesn't exists.");
        else if (votes.containsKey(candidate)) {
            return ("The candidate " + candidates.get(candidate) + " has " + votes.get(candidate).toString() + " computed votes.");
        }
        return ("ERROR");
    }

    private void addOnFIle(String filePath) throws IOException, ClassNotFoundException {
        File file = new File(filePath);

        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
        if (filePath.equalsIgnoreCase(VOTES_TXT)) {
            outputStream.writeObject(votes);
            refreshList(VOTES_TXT);
        } else {
            outputStream.writeObject(voteList);
            refreshList(VOTE_LIST_TXT);
        }
        outputStream.close();
    }

    private void refreshList(String filePath) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filePath));

        if (filePath.equalsIgnoreCase(VOTES_TXT))
            votes = (Map<String, Integer>) objectInputStream.readObject();
        else voteList = (TreeMap<String, String>) objectInputStream.readObject();
        objectInputStream.close();
    }

    private void readInitData() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(SENATORS_CSV));
            ObjectOutputStream outputStream = null;

            String[] aux;

            for (String l = bufferedReader.readLine();
                l != null;
                l = bufferedReader.readLine()) {

                aux = l.split(";");
                candidates.put(aux[0], aux[1]);
                votes.put(aux[0], 0);
            }

            File voteFile = new File(VOTES_TXT);
            if (!voteFile.exists()) {
                voteFile.createNewFile();
            } else if (voteFile.length() == 0) {
                outputStream = new ObjectOutputStream(new FileOutputStream(voteFile));
                outputStream.writeObject(votes);
            } else {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(voteFile));
                votes = (Map<String, Integer>) objectInputStream.readObject();
                objectInputStream.close();
            }

            File voteListFile = new File(VOTE_LIST_TXT);
            if (!voteListFile.exists()) {
                voteListFile.createNewFile();
            } else if (voteListFile.length() != 0) {
                ObjectInputStream objectInputStream =
                        new ObjectInputStream(new FileInputStream(voteListFile));
                voteList = (TreeMap<String, String>) objectInputStream.readObject();
                objectInputStream.close();
            }

            bufferedReader.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        
    }
}
