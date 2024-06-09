package API;

// 위치데이터 getter, setter
public class LocationGetterSetter {
    private double latitude;
    private double longitude;

    public LocationGetterSetter() {
    }

    public LocationGetterSetter(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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
}
