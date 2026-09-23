package edu.neu.csye6200.lab2.model;

import javax.swing.ImageIcon;

public class UserInfo {

    private String firstName;
    private String lastName;
    private String gender;
    private int age;
    private String phone;
    private String email;
    private ImageIcon pic;

    public UserInfo() {

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ImageIcon getPic() {
        return pic;
    }

    public void setPic(ImageIcon pic) {
        this.pic = pic;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getFormattedPhone() {
        if (phone == null || phone.length() != 10) {
            return phone;
        }
        return "(" + phone.substring(0, 3) + ") " + phone.substring(3, 6) + "-" + phone.substring(6);
    }

    @Override
    public String toString() {
        return getFullName() + " | " + gender + " | " + age + " | " + getFormattedPhone() + " | " + email;
    }
}
