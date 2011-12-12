package com.cubeia.poker.variant.telesina;

import static com.cubeia.poker.action.PokerActionType.ANTE;
import static com.cubeia.poker.action.PokerActionType.BET;
import static com.cubeia.poker.action.PokerActionType.CHECK;
import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.hamcrest.CoreMatchers;
import org.mockito.Mockito;

import com.cubeia.poker.AbstractTexasHandTester;
import com.cubeia.poker.MockPlayer;
import com.cubeia.poker.NonRandomRNGProvider;
import com.cubeia.poker.TestUtils;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.player.SitOutStatus;
import com.cubeia.poker.variant.PokerVariant;

public class TelesinaSitoutTest extends AbstractTexasHandTester {

	Logger log = Logger.getLogger(this.getClass());
	private MockPlayer[] mp;
	int[] p;
	
	@Override
	protected void setUp() throws Exception {
		variant = PokerVariant.TELESINA;
		rng = new NonRandomRNGProvider();
		sitoutTimeLimitMilliseconds = 1;
		super.setUp();
		setAnteLevel(10);
		
		mp = TestUtils.createMockPlayers(3, 100);
		p = TestUtils.createPlayerIdArray(mp);
		addPlayers(game, mp);
		
		// Force start
		game.timeout();
		
		// ANTE
		act(p[1], ANTE);
		act(p[2], ANTE);
		act(p[0], ANTE);
		
		// make deal initial pocket cards round end
		game.timeout();
	}
	
	public void testAllSittingOutButOne() throws InterruptedException {
		// Disconnect player 0 & 1
		game.playerIsSittingOut(p[0], SitOutStatus.SITTING_OUT);
		game.playerIsSittingOut(p[1], SitOutStatus.SITTING_OUT);
		assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[0]));
		assertEquals(PokerPlayerStatus.SITOUT, mockServerAdapter.getPokerPlayerStatus(p[1]));
		
		// Player 2 checks
		act(p[2], CHECK);
		
		// Verify that sit out players are checked and not folded
		Assert.assertFalse(mp[0].hasFolded());
		Assert.assertFalse(mp[1].hasFolded());
		
		// Move to next betting round
		game.timeout();
		
		// Assert that player 2 is requested to act
		act(p[2], CHECK);
		
		game.timeout();
		
		Assert.assertEquals(4, mp[2].getPocketCards().getCards().size());
		
		act(p[2], BET); // This should force the other players to auto-fold
		
		assertTrue(game.isFinished());
	}
	
	public void testAllSittingOutButOneFirstBettingRoundBug() throws InterruptedException {
		game.playerIsSittingOut(p[0], SitOutStatus.SITTING_OUT);
		game.playerIsSittingOut(p[1], SitOutStatus.SITTING_OUT);
		
		Assert.assertEquals(2, mp[2].getPocketCards().getCards().size());
		assertNotNull(mp[2].getActionRequest().getOption(CHECK));
		
		act(p[2], CHECK); // Player 2 acts, the other should be auto-checked
		
		game.timeout();
		
		Assert.assertEquals(3, mp[2].getPocketCards().getCards().size());
		
		// Assert that player 2 is now asked to act (This was the bug)
		assertNotNull(mp[2].getActionRequest().getOption(CHECK));
		act(p[2], CHECK); // Player 2 acts, the other should be auto-checked
		
		game.timeout();
		Assert.assertEquals(4, mp[2].getPocketCards().getCards().size());
		
	}
	
	public void testSitoutTwiceOnlyNotifiesOnce(){
		game.playerIsSittingOut(p[0], SitOutStatus.SITTING_OUT);
		game.playerIsSittingIn(p[0]);
		org.junit.Assert.assertThat(mockServerAdapter.getPokerPlayerStatus(p[0]), CoreMatchers.is(PokerPlayerStatus.SITIN));
		game.playerIsSittingOut(p[0], SitOutStatus.SITTING_OUT);
		org.junit.Assert.assertThat(mockServerAdapter.getPokerPlayerStatus(p[0]), CoreMatchers.is(PokerPlayerStatus.SITOUT));
		//this is a hack for setting the players status without going the normal way
		mockServerAdapter.notifyPlayerStatusChanged(p[0], PokerPlayerStatus.SITIN);
		//now we can try to sitout again but it should not notify the player since we already are sitout
		game.playerIsSittingOut(p[0], SitOutStatus.SITTING_OUT);
		org.junit.Assert.assertThat(mockServerAdapter.getPokerPlayerStatus(p[0]), CoreMatchers.is(PokerPlayerStatus.SITIN));
	}
	
	public void testSitinTwiceOnlyNotifiesOnce(){
		game.playerIsSittingOut(p[0], SitOutStatus.SITTING_OUT);
		org.junit.Assert.assertThat(mockServerAdapter.getPokerPlayerStatus(p[0]), CoreMatchers.is(PokerPlayerStatus.SITOUT));
		game.playerIsSittingIn(p[0]);
		org.junit.Assert.assertThat(mockServerAdapter.getPokerPlayerStatus(p[0]), CoreMatchers.is(PokerPlayerStatus.SITIN));
		//this is a hack for setting the players status without going the normal way
		mockServerAdapter.notifyPlayerStatusChanged(p[0], PokerPlayerStatus.SITOUT);
		//now we can try to sitin again but it should not notify the player since we already are sitin
		game.playerIsSittingIn(p[0]);
		org.junit.Assert.assertThat(mockServerAdapter.getPokerPlayerStatus(p[0]), CoreMatchers.is(PokerPlayerStatus.SITOUT));
	}
}
