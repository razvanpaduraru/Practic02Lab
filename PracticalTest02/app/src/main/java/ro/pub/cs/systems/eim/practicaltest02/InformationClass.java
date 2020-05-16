package ro.pub.cs.systems.eim.practicaltest02;

public class InformationClass {
    private String countryName;
    private String continent;
    private Double lat;
    private Double lng;
    private String country_code;
    private static final String urlStart = "https://www.countryflags.io/";
    private static final String urlEnd = "/flat/64.png";

    public InformationClass(String countryName, String continent, String countryCode, Double lat, Double lng) {
        this.countryName = countryName;
        this.continent = continent;
        this.lat = lat;
        this.lng = lng;
        this.country_code = countryCode;
    }

    public InformationClass() {

    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    @Override
    public String toString() {
        return countryName + ", " + continent + ", " + country_code + ", " + lat.toString() +
                ", " + lng.toString() + ", " + urlStart + country_code + urlEnd;
    }
}
