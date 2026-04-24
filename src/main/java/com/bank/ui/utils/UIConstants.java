package com.bank.ui.utils;

import java.awt.Color;
import java.awt.Font;

public class UIConstants {

    // -------------------------------------------------------
    // COLORS — Classic Banking Theme (Navy and Gold)
    // -------------------------------------------------------

    // Primary navy colors
    public static final Color PRIMARY           =
            new Color(15, 40, 80);     // Deep navy
    public static final Color PRIMARY_DARK      =
            new Color(8, 25, 55);      // Darker navy
    public static final Color PRIMARY_LIGHT     =
            new Color(25, 65, 120);    // Lighter navy
    public static final Color PRIMARY_HOVER     =
            new Color(20, 52, 100);    // Hover navy

    // Gold accent colors
    public static final Color GOLD              =
            new Color(197, 160, 80);   // Classic gold
    public static final Color GOLD_DARK         =
            new Color(160, 125, 55);   // Darker gold
    public static final Color GOLD_LIGHT        =
            new Color(220, 190, 120);  // Lighter gold
    public static final Color GOLD_SUBTLE       =
            new Color(250, 243, 224);  // Very light gold

    // Background colors
    public static final Color BACKGROUND        =
            new Color(245, 245, 240);  // Warm off-white
    public static final Color SURFACE           =
            new Color(255, 255, 255);  // Pure white
    public static final Color SURFACE_ALT       =
            new Color(248, 247, 242);  // Alternate surface
    public static final Color SIDEBAR_BG        =
            new Color(15, 40, 80);     // Deep navy sidebar
    public static final Color SIDEBAR_HEADER    =
            new Color(8, 25, 55);      // Darker navy header

    // Text colors
    public static final Color TEXT_PRIMARY      =
            new Color(25, 30, 45);     // Near black
    public static final Color TEXT_SECONDARY    =
            new Color(90, 95, 110);    // Medium grey
    public static final Color TEXT_LIGHT        =
            new Color(255, 255, 255);  // White
    public static final Color TEXT_MUTED        =
            new Color(160, 165, 175);  // Light grey
    public static final Color TEXT_GOLD         =
            new Color(197, 160, 80);   // Gold text
    public static final Color TEXT_NAVY         =
            new Color(15, 40, 80);     // Navy text

    // Status colors
    public static final Color SUCCESS           =
            new Color(39, 130, 80);    // Banking green
    public static final Color SUCCESS_LIGHT     =
            new Color(220, 245, 230);  // Light green
    public static final Color WARNING           =
            new Color(180, 120, 20);   // Muted amber
    public static final Color WARNING_LIGHT     =
            new Color(252, 240, 210);  // Light amber
    public static final Color DANGER            =
            new Color(180, 45, 45);    // Deep red
    public static final Color DANGER_LIGHT      =
            new Color(252, 220, 220);  // Light red
    public static final Color INFO              =
            new Color(40, 90, 160);    // Info blue
    public static final Color INFO_LIGHT        =
            new Color(220, 232, 252);  // Light blue

    // Border colors
    public static final Color BORDER            =
            new Color(210, 205, 190);  // Warm border
    public static final Color BORDER_FOCUS      =
            new Color(197, 160, 80);   // Gold focus
    public static final Color BORDER_DARK       =
            new Color(180, 175, 160);  // Darker border

    // Sidebar item colors
    public static final Color SIDEBAR_ITEM      =
            new Color(255, 255, 255);  // White text
    public static final Color SIDEBAR_ITEM_HOVER =
            new Color(197, 160, 80);   // Gold hover
    public static final Color SIDEBAR_ITEM_ACTIVE =
            new Color(197, 160, 80);   // Gold active
    public static final Color SIDEBAR_ACTIVE_BG =
            new Color(25, 65, 120);    // Active background

    // Table colors
    public static final Color TABLE_HEADER_BG   =
            new Color(15, 40, 80);     // Navy header
    public static final Color TABLE_HEADER_FG   =
            new Color(255, 255, 255);  // White header text
    public static final Color TABLE_ROW_ALT     =
            new Color(248, 247, 242);  // Alt row color
    public static final Color TABLE_SELECTION   =
            new Color(225, 235, 252);  // Selection blue

    // -------------------------------------------------------
    // FONTS
    // -------------------------------------------------------
    public static final Font FONT_TITLE         =
            new Font("Georgia", Font.BOLD, 26);
    public static final Font FONT_SUBTITLE      =
            new Font("Georgia", Font.BOLD, 18);
    public static final Font FONT_HEADING       =
            new Font("Georgia", Font.BOLD, 14);
    public static final Font FONT_BODY          =
            new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL         =
            new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON        =
            new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_INPUT         =
            new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_TABLE_HEADER  =
            new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TABLE_BODY    =
            new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SIDEBAR       =
            new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SIDEBAR_ACTIVE =
            new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BANK_NAME     =
            new Font("Georgia", Font.BOLD, 22);
    public static final Font FONT_BANK_TAGLINE  =
            new Font("Georgia", Font.ITALIC, 12);
    public static final Font FONT_AMOUNT        =
            new Font("Georgia", Font.BOLD, 20);

    // -------------------------------------------------------
    // DIMENSIONS
    // -------------------------------------------------------
    public static final int WINDOW_WIDTH        = 1200;
    public static final int WINDOW_HEIGHT       = 750;
    public static final int SIDEBAR_WIDTH       = 230;
    public static final int HEADER_HEIGHT       = 65;
    public static final int BUTTON_HEIGHT       = 40;
    public static final int INPUT_HEIGHT        = 40;
    public static final int CARD_PADDING        = 20;
    public static final int SECTION_PADDING     = 24;
    public static final int BORDER_RADIUS       = 4;
    public static final int TABLE_ROW_HEIGHT    = 42;

    // -------------------------------------------------------
    // BANK DETAILS
    // -------------------------------------------------------
    public static final String BANK_NAME        =
            "BANK MANAGEMENT SYSTEM";
    public static final String BANK_TAGLINE     =
            "Trusted Since 2026";
}