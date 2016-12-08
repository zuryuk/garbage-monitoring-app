package GarbageBin;

import java.io.Serializable;

import static java.lang.String.valueOf;

public class GarbageBin implements Serializable{
    private int ID;
    private double lat;
    private double lon;
    private String ip;
    private String status;
    private String emptied;

    public GarbageBin(int ID, double lat, double lon, String status, String emptied, String ip){
        this.ID = ID;
        this.lat = parseLat(lat);
        this.lon = parseLon(lon);
        this.status = status;
        this.emptied = emptied;
        this.ip = ip;}
    //Getters & setters
    public void setID(int ID) {this.ID = ID;}
    public void setLon(double lat){this.lat = lat;}
    public void setLat(double lon){this.lon = lon;}
    public void setStatus(String status){this.status = status;}
    public void setEmptied(String emptied){this.emptied = emptied;}
    public void setIp(String ip) {this.ip  = ip;}

    public int getID(){return ID;}
    public double getLat(){return lat;}
    public double getLon(){return lon;}
    public String getStatus(){return status;}
    public String getEmptied(){return emptied;}
    public String getCoord() { parseLat(lat);
        parseLon(lon);
        return lat + "," + lon;}
    public String getIp() {return ip;}
    //Methods
    public void test()
    {
        System.out.println(this.status);
    }

    private double parseLat(double lat){
        double parsedlat;
        String str = Double.toString(lat).replace(".", "");
        str = new StringBuilder(str).insert(2, "-").toString();
        String[] split = str.split("-");
        int x = Integer.parseInt(split[1])/60;
        split[1] = String.valueOf(x);
        str = split[0] + "." + split[1];
        System.out.println(str);
        parsedlat = Double.parseDouble(str);
        return parsedlat;
    }
    private double parseLon(double lon){
        double parsedlon;
        String str = Double.toString(lon).replace(".", "");
        str = new StringBuilder(str).insert(3, "-").toString();
        String[] split = str.split("-");
        int x = Integer.parseInt(split[1])/60;
        split[1] = String.valueOf(x);
        str = split[0] + "." + split[1];
        System.out.println(str);
        parsedlon = Double.parseDouble(str);
        return parsedlon;
    }
}
