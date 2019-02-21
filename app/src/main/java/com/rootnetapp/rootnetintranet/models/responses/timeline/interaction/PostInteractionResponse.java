
package com.rootnetapp.rootnetintranet.models.responses.timeline.interaction;

import com.squareup.moshi.Json;

public class PostInteractionResponse {

    @Json(name = "interaction")
    private Interaction interaction;

    public Interaction getInteraction() {
        return interaction;
    }

    public void setInteraction(Interaction interaction) {
        this.interaction = interaction;
    }
}
