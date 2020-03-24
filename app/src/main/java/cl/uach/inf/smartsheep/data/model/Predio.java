package cl.uach.inf.smartsheep.data.model;

public class Predio {
    private int id;
    private String name;
    private  int latitude;
    private int longitude;

    public Predio(int id, String name, int latitude, int longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId(){return id;}
    public void setId(){this.id = id; }

    public String getName(){return name;}
    public void setName(){this.name = name; }

    public int getLatitude(){return latitude;}
    public void setLatitude(){this.latitude = latitude; }

    public int getLongitude(){return longitude;}
    public void setLongitude(){this.longitude = longitude; }
}
