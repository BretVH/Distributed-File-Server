/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bret
 */
public interface ClientInterface extends java.rmi.Remote {
    public void invalidate();
    public void writeback();
    
}
