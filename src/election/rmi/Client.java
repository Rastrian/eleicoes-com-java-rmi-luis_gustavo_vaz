package election.rmi;

import java.util.Scanner;
import java.io.IOException;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;
import java.rmi.registry.LocateRegistry;
import java.security.NoSuchAlgorithmException;

public class Client {
    static final String QUIT_MESSAGE = "exit";
    static final int PORT = 2103;
    static Election election;
    static Registry registry;

    public static void main(String[] args) {
        String option, candidate, electorName;
        int count = 0;
        final Scanner inReader = new Scanner(System.in);

        try {
            registry = LocateRegistry.getRegistry(2103);
            election = (Election) registry.lookup("Server");

            System.out.println("Tip your name:");
            electorName = inReader.nextLine();

            do {
                System.out.println("Choose an option:\n\nA - Vote\nB - Preview results.\n\nTip 'exit' to quit the system.");
                option = inReader.nextLine();

                switch (option) {
                    case "A":
                        System.out.println("Insert the candidate number:");
                        candidate = inReader.nextLine();
                        System.out.println();
                        try {
                            System.out.println(election.vote(getElectorMD5(electorName), candidate));
                        } catch (RemoteException e) {
                            System.out.println("Trying reconnect to the server.");
                            while (true) {
                                try {
                                    election = (Election) registry.lookup("Server");
                                    System.out.println(election.vote(getElectorMD5(electorName), candidate));
                                    break;
                                } catch (RemoteException e2) {
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(1000);
                                        count++;
                                        if (count == 9) throw new InterruptedException();
                                    } catch (InterruptedException e3) {
                                        System.out.println("Error: Can't connect to the server.");
                                        count = 0;
                                        break;
                                    }
                                } catch (ClassNotFoundException classNotFoundException) {
                                    classNotFoundException.printStackTrace();
                                }
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;

                    case "B":
                        System.out.println("Insert the candidate number to see the number of votes:");
                        candidate = inReader.nextLine();
                        System.out.println();
                        try {
                            System.out.println(election.result(candidate));
                        } catch (RemoteException e) {
                            System.out.println("Trying reconnect to the server.");
                            while (true) {
                                try {
                                    election = (Election) registry.lookup("Server");
                                    System.out.println(election.result(candidate));
                                    break;
                                } catch (RemoteException e2) {
                                    try {
                                        TimeUnit.MILLISECONDS.sleep(1000);
                                        count++;
                                        if (count == 4) throw new InterruptedException();
                                    } catch (InterruptedException e3) {
                                        System.out.println("Error: Can't connect to the server.");
                                        count = 0;
                                        break;
                                    }
                                } catch (ClassNotFoundException classNotFoundException) {
                                    classNotFoundException.printStackTrace();
                                }
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        if (option.equalsIgnoreCase(QUIT_MESSAGE)) break;
                        System.out.println("Invalid option.");
                        break;
                }
            } while (!option.equalsIgnoreCase(QUIT_MESSAGE));

        } catch (IOException e) {
            System.out.println("Server Exception: " + e.getMessage());
        } catch (NotBoundException e) {
            System.out.println("Client Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getElectorMD5(String elector) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(elector.getBytes());
            byte[] md5 = md.digest();
            BigInteger numMd5 = new BigInteger(1, md5);
            return String.format("%022x", numMd5);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
