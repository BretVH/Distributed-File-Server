/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Bret
 */
public class Server implements ServerInterface {

    /**
     * Constructor Server
     */
    List<CachingEntries> cachingEntries;

    public Server() {
        super();
        cachingEntries = null;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            ServerInterface server = new Server();
            ServerInterface stub
                    = (ServerInterface) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("Server", stub);
        } catch (Exception e) {
            System.err.println("Server exception:");
            e.printStackTrace();
        }
    }

    /**
     *
     * @param myIPName
     * @param fileName
     * @param mode
     * @return
     */
    @Override
    public FileContents download(String myIPName, String fileName, String mode) {
        if (cachingEntries == null) {
            CachingEntries entry = addNewCachingEntry(fileName);
            File file = new File(fileName);
            byte[] b = new byte[(int) file.length()];
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                fileInputStream.read(b);
                FileContents contents = new FileContents(b);
                entry.setFile(contents);
            } catch (FileNotFoundException e) {
                System.out.println("File Not Found.");
                e.printStackTrace();
            } catch (IOException e1) {
                System.out.println("Error Reading The File.");
                e1.printStackTrace();
            }

        }
        for (int i = 0; i < cachingEntries.size(); i++) {
            if (fileName.equals(cachingEntries.get(i).getFileName())) {
                if (mode.equals("r")) {
                    int state = cachingEntries.get(i).getState();
                    switch (state) {
                        case 0: {
                            cachingEntries.get(i).addReader(myIPName);
                            cachingEntries.get(i).setState(1);
                            return cachingEntries.get(i).getFile();
                        }
                        case 1: {
                            cachingEntries.get(i).addReader(myIPName);
                            return cachingEntries.get(i).getFile();
                        }
                        case 2: {
                            cachingEntries.get(i).addReader(myIPName);
                            return cachingEntries.get(i).getFile();
                        }
                        case 3: {
                            cachingEntries.get(i).addReader(myIPName);
                            return cachingEntries.get(i).getFile();
                        }
                    }

                } else if (mode.equals("w")) {
                    int state = cachingEntries.get(i).getState();
                    switch (state) {
                        case 0: {
                            cachingEntries.get(i).setOwner(myIPName);
                            return cachingEntries.get(i).getFile();
                        }
                        case 1: {
                            cachingEntries.get(i).setOwner(myIPName);
                            cachingEntries.get(i).setState(2);
                            return cachingEntries.get(i).getFile();
                        }
                        case 2: {
                            cachingEntries.get(i).setState(3);
                            //TODO call current owners writebackFunc;
                            //suspend download untill client calls upload
                            return null;
                        }
                        case 3: {
                            //TODO suspend download untill state = 2
                            return null;
                        }
                    }
                }
            }
        }
        CachingEntries entry = addNewCachingEntry(fileName);
        File file = new File(fileName);
        byte[] b = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
            FileContents contents = new FileContents(b);
            entry.setFile(contents);
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            e.printStackTrace();
        } catch (IOException e1) {
            System.out.println("Error Reading The File.");
            e1.printStackTrace();
        }
        for (int i = 0; i < cachingEntries.size(); i++) {
            if (fileName.equals(cachingEntries.get(i).getFileName())) {
                if (mode.equals("r")) {
                    int state = cachingEntries.get(i).getState();
                    switch (state) {
                        case 0: {
                            cachingEntries.get(i).addReader(myIPName);
                            cachingEntries.get(i).setState(1);
                            return cachingEntries.get(i).getFile();
                        }
                        case 1: {
                            cachingEntries.get(i).addReader(myIPName);
                            return cachingEntries.get(i).getFile();
                        }
                        case 2: {
                            cachingEntries.get(i).addReader(myIPName);
                            return cachingEntries.get(i).getFile();
                        }
                        case 3: {
                            cachingEntries.get(i).addReader(myIPName);
                            return cachingEntries.get(i).getFile();
                        }
                    }

                } else if (mode.equals("w")) {
                    int state = cachingEntries.get(i).getState();
                    switch (state) {
                        case 0: {
                            cachingEntries.get(i).setOwner(myIPName);
                            return cachingEntries.get(i).getFile();
                        }
                        case 1: {
                            cachingEntries.get(i).setOwner(myIPName);
                            cachingEntries.get(i).setState(2);
                            return cachingEntries.get(i).getFile();
                        }
                        case 2: {
                            cachingEntries.get(i).setState(3);
                            cachingEntries.get(i).callWriteBack();
                            //suspend download untill client calls upload?
                            return null;
                        }
                        case 3: {
                            //TODO suspend download untill state = 2?
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     *
     * @param clientIpName
     * @param fileName
     * @param file
     * @return
     */
    @Override
    public boolean upload(String clientIpName, String fileName,
            FileContents file) {
        for (int i = 0; i < cachingEntries.size(); i++) {
            if (cachingEntries.get(i).getState() == 2
                    || cachingEntries.get(i).getState() == 3) {
                cachingEntries.get(i).setFile(file);

            }
        }

        return false;
    }

    /**
     *
     * @param name
     * @return
     */
    private CachingEntries addNewCachingEntry(String name) {
        CachingEntries newEntry = new CachingEntries();
        newEntry.setFileName(name);
        newEntry.setState(0);
        return newEntry;
    }
}
/**
 * 
 * @author Bret
 */
class CachingEntries {

    private String fileName;
    private int state;
    private List<String> Readers;
    private String owner;
    FileContents theFile;

    public enum State {

        Not_Shared, Read_Shared, Write_Shared, Ownership_Change
    };
    /**
     * 
     */
    public void callInvalidate() {
        try {
            for (String ip : Readers) {
                Registry registry = LocateRegistry.getRegistry(ip);
                ClientInterface stub = (Client) registry.lookup("Client");
                stub.invalidate();
            }
        } catch (Exception e) {

        }

    }
    /**
     * 
     */
    public void callWriteBack() {
        try {
            Registry registry = LocateRegistry.getRegistry(owner);
            ClientInterface stub = (Client) registry.lookup("Client");
            stub.writeback();
        } catch (Exception e) {
        }
    }
    /**
     * 
     * @return 
     */
    public FileContents getFile() {
        return theFile;
    }
    /**
     * 
     * @param name 
     */
    public void setFile(FileContents name) {
        theFile = name;
    }
    /**
     * 
     * @return 
     */
    public String getFileName() {
        return fileName;
    }
    /**
     * 
     * @return 
     */
    public int getState() {
        return state;
    }
    /**
     * 
     * @return 
     */
    public List<String> getReaders() {
        return Readers;
    }
    /**
     * 
     * @return 
     */
    public String getOwner() {
        return owner;
    }
    /**
     * 
     * @param name 
     */
    public void setFileName(String name) {
        fileName = name;
    }
    /**
     * 
     * @param aState 
     */
    public void setState(int aState) {
        state = aState;
    }
    /**
     * 
     * @param readerList 
     */
    public void setReaders(List<String> readerList) {
        Readers = readerList;
    }
    /**
     * 
     * @param aReader 
     */
    public void addReader(String aReader) {
        Readers.add(aReader);
    }
    /**
     * 
     * @param newOwner 
     */
    public void setOwner(String newOwner) {
        owner = newOwner;
    }
}
