package com.cubeia.poker;

import java.io.Serializable;
import java.util.Map;

import com.cubeia.poker.rounds.betting.BetStrategyName;
import com.cubeia.poker.timing.TimingProfile;
import com.cubeia.poker.variant.PokerVariant;

@SuppressWarnings("serial")
public class PokerSettings implements Serializable {
	
	private final int anteLevel;
	private final int minBuyIn;
	private final int maxBuyIn;
	private final TimingProfile timing;
	private final PokerVariant variant;
    private final int tableSize;
    private final BetStrategyName betStrategy;
    private final RakeSettings rakeSettings;
    private final Map<Serializable, Serializable> attributes;
    
    /** Default is 1 minute */
    private long sitoutTimeLimitMilliseconds = 1 * 60 * 1000;

	public PokerSettings(
			int anteLevel,
			int minBuyIn,
			int maxBuyIn,
			TimingProfile timing,
			PokerVariant variant,
			int tableSize,
			BetStrategyName betStrategy,
			RakeSettings rakeSettings,
			Map<Serializable, Serializable> attributes) {
		
		this.anteLevel = anteLevel;
		this.minBuyIn = minBuyIn;
		this.maxBuyIn = maxBuyIn;
		this.timing = timing;
		this.variant = variant;
		this.tableSize = tableSize;
		this.betStrategy = betStrategy;
		this.rakeSettings = rakeSettings;
		this.attributes = attributes;
		
	}

	public Map<Serializable, Serializable> getAttributes() {
		return attributes;
	}
	
	public int getAnteLevel() {
		return anteLevel;
	}

	public int getMaxBuyIn() {
		return maxBuyIn;
	}

	public TimingProfile getTiming() {
		return timing;
	}

	public PokerVariant getVariant() {
		return variant;
	}
	
	public int getTableSize() {
        return tableSize;
    }
	
	public BetStrategyName getBetStrategy() {
		return betStrategy;
	}
	
	public int getMinBuyIn() {
		return minBuyIn;
	}

	public RakeSettings getRakeSettings() {
        return rakeSettings;
    }
	
	public long getSitoutTimeLimitMilliseconds() {
		return sitoutTimeLimitMilliseconds;
	}
	
	public void setSitoutTimeLimitMilliseconds(long sitoutTimeLimitMilliseconds) {
		this.sitoutTimeLimitMilliseconds = sitoutTimeLimitMilliseconds;
	}
	
	public int getEntryBetLevel() {
		switch (variant)
		{
			case TELESINA:
				return anteLevel*2;
			default:
				return anteLevel;
		}
	}

}
