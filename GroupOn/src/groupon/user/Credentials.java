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
public class Credentials {
    private String username, email, password, profileImage;

    public Credentials(){
        
    }
        
    public Credentials(String username , String email, String password, String profileImage){
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }
    
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
    public String toString(){
        return username + " " + email + " " + password;
    }
}
