package com.cubeia.poker.variant.texasholdem;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.variant.FutureActionsCalculator;
import com.cubeia.poker.variant.texasholdem.TexasHoldemFutureActionsCalculator;

public class TexasHoldemFutureActionsCalculatorTest {

	@Test
	public void testAllIn() {
		
		FutureActionsCalculator calc = new TexasHoldemFutureActionsCalculator();
		
		PokerPlayer player = Mockito.mock(PokerPlayer.class);
		when(player.isAllIn()).thenReturn(true);
		when(player.hasFolded()).thenReturn(false);
		
		List<PokerActionType> options = calc.calculateFutureActionOptionList(player, 100L);
		assertThat(options.isEmpty(), is(true));
	}
	
	@Test
	public void testNotAllIn() {
		
		FutureActionsCalculator calc = new TexasHoldemFutureActionsCalculator();
		
		PokerPlayer player = Mockito.mock(PokerPlayer.class);
		when(player.isAllIn()).thenReturn(false);
		when(player.hasFolded()).thenReturn(false);
		
		List<PokerActionType> options = calc.calculateFutureActionOptionList(player, 100L);
		assertThat(options.isEmpty(), not(true));
	}
	
	
	@Test
	public void testNotFolded() {
		
		FutureActionsCalculator calc = new TexasHoldemFutureActionsCalculator();
		
		PokerPlayer player = Mockito.mock(PokerPlayer.class);
		when(player.isAllIn()).thenReturn(false);
		when(player.hasFolded()).thenReturn(false);
		
		List<PokerActionType> options = calc.calculateFutureActionOptionList(player, 100L);
		assertThat(options.isEmpty(), not(true));
	}
	
	@Test
	public void testFolded() {
		
		FutureActionsCalculator calc = new TexasHoldemFutureActionsCalculator();
		
		PokerPlayer player = Mockito.mock(PokerPlayer.class);
		when(player.isAllIn()).thenReturn(false);
		when(player.hasFolded()).thenReturn(true);
		
		List<PokerActionType> options = calc.calculateFutureActionOptionList(player, 100L);
		assertThat(options.isEmpty(), is(true));
	}
	
	
	@Test
	public void testHavingHighestBet() {
		
		FutureActionsCalculator calc = new TexasHoldemFutureActionsCalculator();
		
		PokerPlayer player = Mockito.mock(PokerPlayer.class);
		when(player.isAllIn()).thenReturn(false);
		when(player.hasFolded()).thenReturn(false);
		when(player.getBetStack()).thenReturn(100L);
		when(player.getBalance()).thenReturn(2000L);
		
		List<PokerActionType> options = calc.calculateFutureActionOptionList(player, 100L);
		assertThat(options, hasItems(PokerActionType.CHECK, PokerActionType.FOLD));
		assertThat(options.size(), is(2));
		
	}
	
	@Test
	public void testNotHavingHighestBet() {
		
		FutureActionsCalculator calc = new TexasHoldemFutureActionsCalculator();
		
		PokerPlayer player = Mockito.mock(PokerPlayer.class);
		when(player.isAllIn()).thenReturn(false);
		when(player.hasFolded()).thenReturn(false);
		when(player.getBetStack()).thenReturn(50L);
		when(player.getBalance()).thenReturn(2000L);
		
		List<PokerActionType> options = calc.calculateFutureActionOptionList(player, 100L);
		assertThat(options, hasItems( PokerActionType.FOLD));
		assertThat(options.size(), is(1));
	}
	
	@Test
	public void testHavingHighestBetButHaveActed() {
		
		FutureActionsCalculator calc = new TexasHoldemFutureActionsCalculator();
		
		PokerPlayer player = Mockito.mock(PokerPlayer.class);
		when(player.isAllIn()).thenReturn(false);
		when(player.hasFolded()).thenReturn(false);
		when(player.hasActed()).thenReturn(true);
		when(player.getBetStack()).thenReturn(100L);
		when(player.getBalance()).thenReturn(2000L);
		
		List<PokerActionType> options = calc.calculateFutureActionOptionList(player, 100L);
		assertThat(options, hasItems(PokerActionType.CHECK, PokerActionType.FOLD));
		assertThat(options.size(), is(2));
		
	}
	


}
