package io.desentupidora.adsgun.domain.model.input;

import java.util.UUID;

public record TargetInput(UUID uuid, String title, String url, String whatsappNumber) {
}
