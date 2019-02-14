
package com.rootnetapp.rootnetintranet.models.responses.timeline;

import com.squareup.moshi.Json;

public class Description {

    @Json(name = "text")
    private String text;
    @Json(name = "arguments")
    private Arguments arguments;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Arguments getArguments() {
        return arguments;
    }

    public void setArguments(Arguments arguments) {
        this.arguments = arguments;
    }

}
