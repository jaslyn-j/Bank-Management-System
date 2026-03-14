package com.bank.services;

import com.bank.dao.CardExpiryStatusDAO;
import com.bank.models.CardExpiryStatus;

import java.util.List;

public class CardExpiryService {

    private CardExpiryStatusDAO dao;

    public CardExpiryService() {
        this.dao = new CardExpiryStatusDAO();
    }

    // Get all active cards with their expiry status for a branch
    public List<CardExpiryStatus> getAllCardExpiryStatus(int branchId) {
        return dao.getAllCardExpiryStatus(branchId);
    }

    // Get all cards that are expired or expiring soon for a branch
    // Used to populate the admin card expiry monitoring panel
    public List<CardExpiryStatus> getExpiringCards(int branchId) {
        return dao.getExpiringCards(branchId);
    }

    // Get all cards that have already expired for a branch
    // Used for admin to take action on expired cards
    public List<CardExpiryStatus> getExpiredCards(int branchId) {
        return dao.getExpiredCards(branchId);
    }

    // Get cards expiring within a specific number of days
    // For example passing 7 returns cards expiring this week
    public List<CardExpiryStatus> getCardsExpiringWithinDays(
            int branchId, int days) {
        return dao.getCardsExpiringWithinDays(branchId, days);
    }

    // Get a count summary grouped by expiry status
    // Used to display a summary panel on the admin dashboard
    public List<Object[]> getExpiryStatusCounts(int branchId) {
        return dao.getExpiryStatusCounts(branchId);
    }

    // Get the expiry status for one specific card
    public CardExpiryStatus getCardExpiryStatus(int cardId) {
        return dao.getByCardId(cardId);
    }

    // Check if a specific card is expired
    public boolean isCardExpired(int cardId) {
        CardExpiryStatus card = dao.getByCardId(cardId);
        return card != null && card.getExpiryStatus().equals("Expired");
    }

    // Check if a specific card is expiring soon (within 30 days)
    public boolean isCardExpiringSoon(int cardId) {
        CardExpiryStatus card = dao.getByCardId(cardId);
        return card != null && card.getExpiryStatus().equals("Expiring Soon");
    }

    public List<CardExpiryStatus> getCardExpiryStatusForCustomer(
            int customerId) {
        return dao.getCardExpiryStatusByCustomer(customerId);
    }
}