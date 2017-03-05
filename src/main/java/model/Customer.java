package model;


public class Customer {
    private int id;
    private String name;
    private int inn;
    private int edrpou;

    public Customer() {
    }

    public Customer(int id, String name, int inn, int edrpou) {
        this.id = id;
        this.name = name;
        this.inn = inn;
        this.edrpou = edrpou;
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

    public int getInn() {
        return inn;
    }

    public void setInn(int inn) {
        this.inn = inn;
    }

    public int getEdrpou() {
        return edrpou;
    }

    public void setEdrpou(int edrpou) {
        this.edrpou = edrpou;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", inn=" + inn +
                ", edrpou=" + edrpou +
                '}';
    }
}


