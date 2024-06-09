package API;

// 최종 사용

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
