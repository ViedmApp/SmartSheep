package cl.uach.inf.smartsheep.data.model;

import java.io.Serializable;

public class Sheep implements Serializable {

    private int _id;
    private String earring;
    private String earring_color;
    private String gender;
    private String breed;
    private double birth_weight;
    private String purpose;
    private String category;
    private int merit;
    private String is_dead;
    private int farms_id;

    public Sheep(String earring, String earringColor, String gender, String breed, double birth_weight, String purpose, String category, int merit, String isDead) {
        this.earring = earring;
        this.earring_color = earringColor;
        this.gender = gender;
        this.breed = breed;
        this.birth_weight = birth_weight;
        this.purpose = purpose;
        this.category = category;
        this.merit = merit;
        this.is_dead = isDead;
    }

    public Sheep(int _id, String earring, String earring_color, String gender, String breed, double birth_weight, String purpose, String category, int merit, String is_dead) {
        this._id = _id;
        this.earring = earring;
        this.earring_color = earring_color;
        this.gender = gender;
        this.breed = breed;
        this.birth_weight = birth_weight;
        this.purpose = purpose;
        this.category = category;
        this.merit = merit;
        this.is_dead = is_dead;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getEarring() {
        return earring;
    }

    public void setEarring(String earring) {
        this.earring = earring;
    }

    public String getEarringColor() {
        return earring_color;
    }

    public void setEarringColor(String earringColor) {
        this.earring_color = earringColor;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public double getBirth_weight() {
        return birth_weight;
    }

    public void setBirth_weight(double birth_weight) {
        this.birth_weight = birth_weight;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMerit() {
        return merit;
    }

    public void setMerit(int merit) {
        this.merit = merit;
    }

    public String getIs_dead() {
        return is_dead;
    }

    public void setIs_dead(String is_dead) {
        this.is_dead = is_dead;
    }

    public int getFarms_id() {
        return farms_id;
    }

    public void setFarms_id(int farms_id) {
        this.farms_id = farms_id;
    }
}
