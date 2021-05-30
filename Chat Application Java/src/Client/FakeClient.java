/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.Serializable;

/**
 *
 * @author Mert
 */
public class FakeClient implements Serializable{
    
    public int id;
    public String name;

    public FakeClient(int id, String name) {
    
        this.id=id;
        this.name = name;
        
    }
    
    
    
}
