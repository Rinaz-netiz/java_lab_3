package com.taxi.model;

public enum TaxiState {
    FREE("Свободен"),
    HEADING_TO_CLIENT("Едет к клиенту"),
    TRANSPORTING("Везёт пассажира"),
    OFFLINE("Не работает");
    
    private final String description;
    
    TaxiState(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
