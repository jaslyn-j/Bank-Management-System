package com.bank.services;

import com.bank.dao.CardDAO;
import com.bank.models.Card;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class CardService {

    private CardDAO cardDAO;

    public CardService() {
        this.cardDAO = new CardDAO();
    }

    // Customer applies for a new card on a specific account
    public boolean applyForCard(int accountId, String cardType) {
        Card card = new Card();
        card.setAccountId(accountId);
        card.setCardType(cardType);
        card.setCardNumber(generateCardNumber());
        card.setCvvHash(generateCVV());
        card.setExpiryDate(LocalDate.now().plusYears(4));
        return cardDAO.applyForCard(card);
    }

    // Retrieve all cards linked to a specific account
    public List<Card> getCardsForAccount(int accountId) {
        return cardDAO.getCardsByAccount(accountId);
    }

    // Retrieve all pending card applications for a branch (admin queue)
    public List<Card> getPendingCardsForBranch(int branchId) {
        return cardDAO.getPendingCardsByBranch(branchId);
    }

    // Retrieve all cards for a branch (full admin view)
    public List<Card> getAllCardsForBranch(int branchId) {
        return cardDAO.getAllCardsByBranch(branchId);
    }

    // Admin approves a card application
    public boolean approveCard(int cardId, int managerId) {
        return cardDAO.approveCard(cardId, managerId);
    }

    // Admin declines a card application
    public boolean declineCard(int cardId, int managerId) {
        return cardDAO.declineCard(cardId, managerId);
    }

    // Admin or system blocks a card
    public boolean blockCard(int cardId) {
        return cardDAO.updateCardStatus(cardId, "blocked");
    }

    // Admin unblocks a card
    public boolean unblockCard(int cardId) {
        return cardDAO.updateCardStatus(cardId, "active");
    }

    // Generates a random 16-digit card number formatted in groups of 4
    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    // Generates a random 3-digit CVV
    // In production this should be hashed before storing
    private String generateCVV() {
        Random random = new Random();
        int cvv = 100 + random.nextInt(900);
        return String.valueOf(cvv);
    }
}