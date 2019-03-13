
package com.rootnetapp.rootnetintranet.models.responses.googlemaps.autocomplete;

import java.util.List;
import com.squareup.moshi.Json;

public class StructuredFormatting {

    @Json(name = "main_text")
    private String mainText;
    @Json(name = "main_text_matched_substrings")
    private List<MainTextMatchedSubstring> mainTextMatchedSubstrings = null;
    @Json(name = "secondary_text")
    private String secondaryText;

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public List<MainTextMatchedSubstring> getMainTextMatchedSubstrings() {
        return mainTextMatchedSubstrings;
    }

    public void setMainTextMatchedSubstrings(List<MainTextMatchedSubstring> mainTextMatchedSubstrings) {
        this.mainTextMatchedSubstrings = mainTextMatchedSubstrings;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }

}
