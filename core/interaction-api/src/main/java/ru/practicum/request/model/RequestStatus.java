package ru.practicum.request.model;

public enum RequestStatus {
    PENDING,     // Ожидает подтверждения
    CONFIRMED,   // Подтверждена
    REJECTED,    // Отклонена
    CANCELED     // Отменена пользователем
}
