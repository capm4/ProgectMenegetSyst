package model;

import java.util.List;

public class Company {
    private int id;
    private String name;
    private String address;
    private String country;
    private String city;
    private List projects;

    public Company() {}

    public Company(int id, String name, String address, String country, String city) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.country = country;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List getProjects() {
        return projects;
    }

    public void addProjects(Project project) {
        this.projects.add(project);
    }

    @Override
    public String toString() {
        return "Company [id=" + id + ", name=" + name + ", address=" + address + ", country=" + country + ", city="
                + city + "]";
    }

}
