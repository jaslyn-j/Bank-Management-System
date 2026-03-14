package com.bank.dao;

import com.bank.db.DBConnection;
import com.bank.models.Card;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CardDAO {
    private final Connection connection;

    public CardDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    // Retrieve all cards linked to a specific account
    public List<Card> getCardsByAccount(int accountId) {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT * FROM Card WHERE account_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cards.add(mapResultSetToCard(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching cards by account: " + e.getMessage());
        }

        return cards;
    }

    // Retrieve all pending card applications for a branch (used by admin)
    public List<Card> getPendingCardsByBranch(int branchId) {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT c.* FROM Card c " +
                "JOIN Account a ON c.account_id = a.account_id " +
                "WHERE a.branch_id = ? AND c.status = 'pending'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cards.add(mapResultSetToCard(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching pending cards: " + e.getMessage());
        }

        return cards;
    }

    // Retrieve all cards for a branch (used by admin to manage all cards)
    public List<Card> getAllCardsByBranch(int branchId) {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT c.* FROM Card c " +
                "JOIN Account a ON c.account_id = a.account_id " +
                "WHERE a.branch_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cards.add(mapResultSetToCard(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all cards by branch: " + e.getMessage());
        }

        return cards;
    }

    // Insert a new card application
    public boolean applyForCard(Card card) {
        String sql = "INSERT INTO Card (account_id, card_type, card_number, cvv_hash, expiry_date, status) VALUES (?, ?, ?, ?, ?, 'pending')";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, card.getAccountId());
            stmt.setString(2, card.getCardType());
            stmt.setString(3, card.getCardNumber());
            stmt.setString(4, card.getCvvHash());
            stmt.setDate(5, Date.valueOf(card.getExpiryDate()));

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error applying for card: " + e.getMessage());
        }

        return false;
    }

    // Approve a card application
    public boolean approveCard(int cardId, int managerId) {
        String sql = "UPDATE Card SET status = 'active', approved_by = ? WHERE card_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, managerId);
            stmt.setInt(2, cardId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error approving card: " + e.getMessage());
        }

        return false;
    }

    // Decline a card application
    public boolean declineCard(int cardId, int managerId) {
        String sql = "UPDATE Card SET status = 'cancelled', approved_by = ? WHERE card_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, managerId);
            stmt.setInt(2, cardId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error declining card: " + e.getMessage());
        }

        return false;
    }

    // Update card status — used for blocking and unblocking
    public boolean updateCardStatus(int cardId, String status) {
        String sql = "UPDATE Card SET status = ? WHERE card_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, cardId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating card status: " + e.getMessage());
        }

        return false;
    }

    // Maps a ResultSet row to a Card object
    private Card mapResultSetToCard(ResultSet rs) throws SQLException {
        Card card = new Card();
        card.setCardId(rs.getInt("card_id"));
        card.setAccountId(rs.getInt("account_id"));
        card.setCardType(rs.getString("card_type"));
        card.setCardNumber(rs.getString("card_number"));
        card.setCvvHash(rs.getString("cvv_hash"));
        card.setStatus(rs.getString("status"));

        Date expiryDate = rs.getDate("expiry_date");
        if (expiryDate != null) {
            card.setExpiryDate(expiryDate.toLocalDate());
        }

        int approvedBy = rs.getInt("approved_by");
        if (!rs.wasNull()) {
            card.setApprovedBy(approvedBy);
        }


        return card;
    }
}
