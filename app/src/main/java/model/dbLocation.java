package model;

import java.io.Serializable;

/**
 * Created by hlkhjk_ok on 17/4/21.
 */

public class dbLocation implements Serializable {
    private double latitude;
    private double longitude;

    private String latlng;
    private String city;

    public dbLocation() {};

    public dbLocation(double lat, double lng, String city) {
        this.latitude = lat;
        this.longitude = lng;
        this.city = city;
        this.latlng = Double.toString(lat) + "," + Double.toString(lng);
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public String getLatlng() {
        return latlng;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Lat: ").append(Double.toString(latitude))
                .append("Lng: ").append(Double.toString(longitude)).append("latlng: " + latlng)
                .append("city: " + city).toString();
    }

    public boolean checkValid() {
        return ( latlng.length() > 1 );
    }
}
