package com.cubeia.poker.rake;

import java.math.BigDecimal;
import java.util.Map;

import com.cubeia.poker.pot.Pot;

public class RakeInfoContainer {
    
    private final int totalPot;
    private final int totalRake;
    private final Map<Pot, BigDecimal> potRakes;
    
    public RakeInfoContainer(int totalPot, int totalRake, Map<Pot, BigDecimal> potRakes) {
        super();
        this.totalPot = totalPot;
        this.totalRake = totalRake;
        this.potRakes = potRakes;
    }

    public int getTotalPot() {
        return totalPot;
    }

    public int getTotalRake() {
        return totalRake;
    }
    
    public Map<Pot, BigDecimal> getPotRakes() {
        return potRakes;
    }

    @Override
    public String toString() {
        return "RakeInfoContainer [totalPot=" + totalPot + ", totalRake=" + totalRake + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + totalPot;
        result = prime * result + totalRake;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RakeInfoContainer other = (RakeInfoContainer) obj;
        if (totalPot != other.totalPot)
            return false;
        if (totalRake != other.totalRake)
            return false;
        return true;
    }
    
}