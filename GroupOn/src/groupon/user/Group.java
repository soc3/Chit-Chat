/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupon.user;

/**
 *
 * @author sushant oberoi
 */
public class Group {
    private String name;
    private String owner;
    private String[] members;
    
    public Group(String name, String owner){
        this.name = name;
        this.owner = owner;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setMembers(String[] members) {
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String[] getMembers() {
        return members;
    }
    
    
}
