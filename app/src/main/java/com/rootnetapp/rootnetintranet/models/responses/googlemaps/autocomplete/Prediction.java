
package com.rootnetapp.rootnetintranet.models.responses.googlemaps.autocomplete;

import java.util.List;
import com.squareup.moshi.Json;

public class Prediction {

    @Json(name = "description")
    private String description;
    @Json(name = "id")
    private String id;
    @Json(name = "matched_substrings")
    private List<MatchedSubstring> matchedSubstrings = null;
    @Json(name = "place_id")
    private String placeId;
    @Json(name = "reference")
    private String reference;
    @Json(name = "structured_formatting")
    private StructuredFormatting structuredFormatting;
    @Json(name = "terms")
    private List<Term> terms = null;
    @Json(name = "types")
    private List<String> types = null;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MatchedSubstring> getMatchedSubstrings() {
        return matchedSubstrings;
    }

    public void setMatchedSubstrings(List<MatchedSubstring> matchedSubstrings) {
        this.matchedSubstrings = matchedSubstrings;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public StructuredFormatting getStructuredFormatting() {
        return structuredFormatting;
    }

    public void setStructuredFormatting(StructuredFormatting structuredFormatting) {
        this.structuredFormatting = structuredFormatting;
    }

    public List<Term> getTerms() {
        return terms;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

}
