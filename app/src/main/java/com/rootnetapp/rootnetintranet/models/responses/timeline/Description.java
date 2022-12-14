
package com.rootnetapp.rootnetintranet.models.responses.timeline;

import com.rootnetapp.rootnetintranet.ui.timeline.TimelineAction;
import com.squareup.moshi.Json;

public class Description {

    @Json(name = "text")
    private @TimelineAction String text;
    @Json(name = "arguments")
    private Arguments arguments;

    public @TimelineAction String getText() {
        return text;
    }

    public void setText(@TimelineAction String text) {
        this.text = text;
    }

    public Arguments getArguments() {
        return arguments;
    }

    public void setArguments(Arguments arguments) {
        this.arguments = arguments;
    }

}
