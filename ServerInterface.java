/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bret
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote 
{
    FileContents download(String myIPName, String fileName, String mode) throws 
            RemoteException;
    public boolean upload(String clientIpName, String fileName,
            FileContents file) throws RemoteException;
    
}
