package communication;

public enum ServerResponse {
    messageDeliveryFailed,
    messageDelivered,
    notAuthorized,
    alreadyAuthenticated,
    authSuccessful,
    authFailed,
    unknownPurpose,
}
