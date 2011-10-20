package com.cubeia.poker.variant.telesina;

import static com.cubeia.poker.action.PokerActionType.ANTE;
import static com.cubeia.poker.action.PokerActionType.BET;
import static com.cubeia.poker.action.PokerActionType.CALL;
import static com.cubeia.poker.action.PokerActionType.CHECK;
import static com.cubeia.poker.action.PokerActionType.DECLINE_ENTRY_BET;

import org.junit.Test;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNGProvider;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.variant.PokerVariant;

public class TelesinaTimeoutTest extends AbstractTexasHandTester {

	@Override
	protected void setUp() throws Exception {
		variant = PokerVariant.TELESINA;
		rng = new NonRandomRNGProvider();
		super.setUp();
		setAnteLevel(10);
	}
	
	
	/**
	 * Verify that table does not hang on a timeout during ante round
	 */
	@Test
	public void testAnteTimeout() {
		MockPlayer[] mp = TestUtils.createMockPlayers(3);
		int[] p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);
		
		// Set initial balances
		mp[0].setBalance(100);
		mp[1].setBalance(100);
		mp[2].setBalance(100);
		
		// Force start
		game.timeout();
		
		// ANTE
		act(p[1], ANTE);
		assertTrue(mp[2].isActionPossible(ANTE));
		// Timeout player 2
		game.timeout();
		// Assert that player 0 has received an action request
		assertTrue(mp[0].isActionPossible(ANTE));
	}
	
	/**
	 * Verify timeout will exclude player from table
	 */
	@Test
	public void testAnteTimeoutHand() {
		MockPlayer[] mp = TestUtils.createMockPlayers(3);
		int[] p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);
		
		// Set initial balances
		mp[0].setBalance(100);
		mp[1].setBalance(100);
		mp[2].setBalance(100);
		
		// Force start
		game.timeout();
		
		// ANTE
		act(p[1], ANTE);
		// Timeout player 2
		game.timeout();
		act(p[0], ANTE);
	}
	
	@Test
	public void testAnteTimeoutHand2() {
		MockPlayer[] mp = TestUtils.createMockPlayers(3);
		int[] p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);
		
		// Set initial balances
		mp[0].setBalance(100);
		mp[1].setBalance(100);
		mp[2].setBalance(100);
		
		// Force start
		game.timeout();
		
		// ANTE
		act(p[1], ANTE);
		act(p[2], ANTE);
		// Timeout player 0
		game.timeout();
		
		assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[0]));
		
		assertTrue(mp[2].isActionPossible(CHECK));
		act(p[2], PokerActionType.CHECK);
		act(p[1], PokerActionType.CHECK);
		
		assertTrue(game.getPlayerInCurrentHand(p[0]).isSittingOut());
	}

	@Test
	public void testPlayerOutOfMoney() {
		MockPlayer[] mp = TestUtils.createMockPlayers(2);
		int[] p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);
		
		// Set initial balances
		mp[0].setBalance(100);
		mp[1].setBalance(100);
		
		// Force start
		game.timeout();
		act(p[1], ANTE);
		act(p[0], ANTE);
		act(p[1], BET, 90);
		act(p[0], CALL);
		// Progress until hand is complete
		game.timeout();
		game.timeout();
		game.timeout();
		game.timeout();
		game.timeout();
		game.timeout();
		game.timeout();
		game.timeout();
		// End of hand
		
		assertTrue(mp[0].isSittingOut());
	}
	
	@Test
	public void testPlayerDeclinesAnte() {
		MockPlayer[] mp = TestUtils.createMockPlayers(3);
		int[] p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);
		
		// Set initial balances
		mp[0].setBalance(100);
		mp[1].setBalance(100);
		mp[2].setBalance(100);
		
		// Force start
		game.timeout();
		
		// ANTE
		act(p[1], ANTE);
		act(p[2], DECLINE_ENTRY_BET);
		act(p[0], ANTE);
		
		assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[2]));
		
		assertTrue(mp[1].isActionPossible(CHECK));
		act(p[1], PokerActionType.CHECK);
		act(p[0], PokerActionType.CHECK);
		
		assertTrue(game.getPlayerInCurrentHand(p[2]).isSittingOut());
	}
}