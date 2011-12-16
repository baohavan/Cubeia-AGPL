package com.cubeia.games.poker.adapter;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Calculates min and max buy ins depending on the player's balance and
 * the rules of the table.
 * @author w
 */
public class BuyInCalculator {
    
    /**
     * Calculates the possible buy in range.
     * @param tableMinBuyIn min buy in on table
     * @param tableMaxBuyIn max buy in on table
     * @param anteLevel ante level on table
     * @param playerBalance players balance
     * @return a container for min, max and a buy in possible flag
     */
    public MinAndMaxBuyInResult calculateBuyInLimits(int tableMinBuyIn, int tableMaxBuyIn, int anteLevel, int playerBalance) {
        if (playerBalance >= tableMaxBuyIn) {
            return new MinAndMaxBuyInResult(0, 0, false);
        }
        
        return new MinAndMaxBuyInResult(
            calculateMinBuyIn(tableMinBuyIn, tableMaxBuyIn, anteLevel, playerBalance), 
            calculateMaxBuyIn(tableMinBuyIn, tableMaxBuyIn, anteLevel, playerBalance), 
            true);
    }
    
    public int calculateAmountToReserve(int tableMaxBuyIn, int playerBalanceIncludingPending, int amountRequestedByUser) {
        return min(amountRequestedByUser, tableMaxBuyIn - playerBalanceIncludingPending);
    }
    
    private int calculateMinBuyIn(int tableMinBuyIn, int tableMaxBuyIn, int anteLevel, int playerBalance) {
        if (playerBalance < tableMinBuyIn) {
            return max(anteLevel, tableMinBuyIn - playerBalance);
        } else {
            return min(anteLevel, tableMaxBuyIn - playerBalance);
        }
    }

    private int calculateMaxBuyIn(int tableMinBuyIn, int tableMaxBuyIn, int anteLevel, int playerBalance) {
        return tableMaxBuyIn - playerBalance;
    }
    
    /**
     * Buy in range result. If {@link MinAndMaxBuyInResult#isBuyInPossible()} is false both min and max are 
     * set to zero.
     * @author w
     */
    public static class MinAndMaxBuyInResult {
        private final int minBuyIn;
        private final int maxBuyIn;
        private final boolean buyInPossible;
        public MinAndMaxBuyInResult(int minBuyIn, int maxBuyIn, boolean buyInPossible) {
            this.minBuyIn = minBuyIn;
            this.maxBuyIn = maxBuyIn;
            this.buyInPossible = buyInPossible;
        }
        
        public int getMinBuyIn() {
            return minBuyIn;
        }
        
        public int getMaxBuyIn() {
            return maxBuyIn;
        }

        public boolean isBuyInPossible() {
            return buyInPossible;
        }
    }
}
