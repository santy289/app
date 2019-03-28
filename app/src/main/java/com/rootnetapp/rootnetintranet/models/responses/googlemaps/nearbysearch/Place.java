
package com.rootnetapp.rootnetintranet.models.responses.googlemaps.nearbysearch;

import com.squareup.moshi.Json;

import java.util.List;

public class Place {

    @Json(name = "geometry")
    private Geometry geometry;
    @Json(name = "icon")
    private String icon;
    @Json(name = "id")
    private String id;
    @Json(name = "name")
    private String name;
    @Json(name = "opening_hours")
    private OpeningHours openingHours;
    @Json(name = "photos")
    private List<Photo> photos = null;
    @Json(name = "place_id")
    private String placeId;
    @Json(name = "plus_code")
    private PlusCode plusCode;
    @Json(name = "price_level")
    private Integer priceLevel;
    @Json(name = "rating")
    private Double rating;
    @Json(name = "reference")
    private String reference;
    @Json(name = "scope")
    private String scope;
    @Json(name = "types")
    private List<String> types = null;
    @Json(name = "user_ratings_total")
    private Integer userRatingsTotal;
    @Json(name = "vicinity")
    private String vicinity;
    @Json(name = "formatted_address")
    private String formattedAddress;

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public PlusCode getPlusCode() {
        return plusCode;
    }

    public void setPlusCode(PlusCode plusCode) {
        this.plusCode = plusCode;
    }

    public Integer getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(Integer priceLevel) {
        this.priceLevel = priceLevel;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public Integer getUserRatingsTotal() {
        return userRatingsTotal;
    }

    public void setUserRatingsTotal(Integer userRatingsTotal) {
        this.userRatingsTotal = userRatingsTotal;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }
}
