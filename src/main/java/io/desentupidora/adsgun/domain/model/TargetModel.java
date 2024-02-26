package io.desentupidora.adsgun.domain.model;

import io.desentupidora.adsgun.domain.model.input.TargetInput;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedList;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TargetModel {

    private UUID uuid;
    private String title;
    private String url;
    private String whatsappNumber;
    private LinkedList<String> adUrls;

    public TargetModel(TargetInput targetInput) {
        this.title = targetInput.title();
        this.uuid = targetInput.uuid();
        this.url = targetInput.url();
        this.whatsappNumber = targetInput.whatsappNumber();
    }


}
