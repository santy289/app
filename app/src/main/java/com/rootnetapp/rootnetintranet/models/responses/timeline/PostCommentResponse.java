package com.rootnetapp.rootnetintranet.models.responses.timeline;

import com.squareup.moshi.Json;

/**
 * Created by root on 12/04/18.
 */

public class PostCommentResponse {

    @Json(name = "interaction")
    private Interaction interaction;

    public Interaction getInteraction() {
        return interaction;
    }

    public void setInteraction(Interaction interaction) {
        this.interaction = interaction;
    }

}
