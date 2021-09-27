package election.rmi;

import java.rmi.Remote;
import java.io.IOException;

public interface Election extends Remote  {
    String vote(String electorName, String candidate) throws IOException, ClassNotFoundException;
    String result(String candidate) throws IOException, ClassNotFoundException;
}
