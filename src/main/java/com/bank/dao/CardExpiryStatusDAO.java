package com.bank.dao;

import com.bank.db.DBConnection;
import com.bank.models.CardExpiryStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardExpiryStatusDAO {

    private Connection connection;

    public CardExpiryStatusDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    public List<CardExpiryStatus> getAllCardExpiryStatus(int branchId) {
        List<CardExpiryStatus> results = new ArrayList<>();
        String sql = "SELECT * FROM vw_card_expiry_status WHERE branch_id = ? " +
                "ORDER BY expiry_date ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching card expiry status: "
                    + e.getMessage());
        }

        return results;
    }

    public List<CardExpiryStatus> getExpiringCards(int branchId) {
        List<CardExpiryStatus> results = new ArrayList<>();
        String sql = "SELECT * FROM vw_card_expiry_status WHERE branch_id = ? AND expiry_status != 'Valid' ORDER BY expiry_date ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching expiring cards: "
                    + e.getMessage());
        }

        return results;
    }

    public List<CardExpiryStatus> getExpiredCards(int branchId) {
        List<CardExpiryStatus> results = new ArrayList<>();
        String sql = "SELECT * FROM vw_card_expiry_status WHERE branch_id = ? AND expiry_status = 'Expired' ORDER BY expiry_date ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching expired cards: "
                    + e.getMessage());
        }

        return results;
    }

    public List<CardExpiryStatus> getCardsExpiringWithinDays(
            int branchId, int days) {

        List<CardExpiryStatus> results = new ArrayList<>();
        String sql = "SELECT * FROM vw_card_expiry_status WHERE branch_id = ? " +
                "AND expiry_date BETWEEN CURRENT_DATE AND DATE_ADD(CURRENT_DATE, INTERVAL ? DAY) ORDER BY expiry_date ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            stmt.setInt(2, days);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching cards expiring within days: "
                    + e.getMessage());
        }

        return results;
    }

    public List<Object[]> getExpiryStatusCounts(int branchId) {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT expiry_status, COUNT(*) AS total FROM vw_card_expiry_status " +
                "WHERE branch_id = ? GROUP BY expiry_status ORDER BY total DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(new Object[]{
                        rs.getString("expiry_status"),
                        rs.getInt("total")
                });
            }

        } catch (SQLException e) {
            System.err.println("Error counting expiry statuses: "
                    + e.getMessage());
        }

        return results;
    }

    public CardExpiryStatus getByCardId(int cardId) {
        String sql = "SELECT * FROM vw_card_expiry_status WHERE card_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cardId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching card expiry by card ID: "
                    + e.getMessage());
        }

        return null;
    }

    private CardExpiryStatus mapResultSet(ResultSet rs)
            throws SQLException {

        CardExpiryStatus card = new CardExpiryStatus();
        card.setCardId(rs.getInt("card_id"));
        card.setCardNumber(rs.getString("card_number"));
        card.setCardType(rs.getString("card_type"));
        card.setStatus(rs.getString("status"));
        card.setAccountId(rs.getInt("account_id"));
        card.setAccountNumber(rs.getString("account_number"));
        card.setBranchId(rs.getInt("branch_id"));
        card.setExpiryStatus(rs.getString("expiry_status"));

        Date expiryDate = rs.getDate("expiry_date");
        if (expiryDate != null) {
            card.setExpiryDate(expiryDate.toLocalDate());
        }

        return card;
    }

    public List<CardExpiryStatus> getCardExpiryStatusByCustomer(
            int customerId) {

        List<CardExpiryStatus> results = new ArrayList<>();
        String sql = "SELECT v.* FROM vw_card_expiry_status v " +
                "INNER JOIN Account a ON v.account_id = a.account_id " +
                "WHERE a.customer_id = ? " +
                "ORDER BY v.expiry_date ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching card expiry by customer: "
                    + e.getMessage());
        }

        return results;
    }
}