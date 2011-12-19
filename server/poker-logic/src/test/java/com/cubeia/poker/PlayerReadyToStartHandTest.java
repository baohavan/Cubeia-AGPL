package com.cubeia.poker;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.cubeia.poker.player.PokerPlayer;

public class PlayerReadyToStartHandTest {
    @Mock GameType gameType;
    @Mock PokerPlayer pokerPlayer;
    @Mock PokerSettings settings;
    PokerState state;

    @Before
    public void setup() {
        initMocks(this);
        state = new PokerState();
        state.gameType = gameType;
        state.settings = settings;
    }
    
    @Test
    public void testPlayerReadyToStartHand() {
        when(pokerPlayer.isSittingOut()).thenReturn(false);
        when(pokerPlayer.isBuyInRequestActive()).thenReturn(false);
        when(gameType.canPlayerAffordEntryBet(pokerPlayer, settings, false)).thenReturn(true);
        
        assertThat(state.playerReadyToStartHand(pokerPlayer), is(true));
    }
    
    @Test
    public void testPlayerNotReadyToStartHandWhenSittingOut() {
        when(pokerPlayer.isSittingOut()).thenReturn(true);
        when(pokerPlayer.isBuyInRequestActive()).thenReturn(false);
        when(gameType.canPlayerAffordEntryBet(pokerPlayer, settings, false)).thenReturn(true);
        
        assertThat(state.playerReadyToStartHand(pokerPlayer), is(false));
    }

    @Test
    public void testPlayerNotReadyToStartHandWhileBackendRequestActive() {
        when(pokerPlayer.isSittingOut()).thenReturn(false);
        when(pokerPlayer.isBuyInRequestActive()).thenReturn(true);
        when(gameType.canPlayerAffordEntryBet(pokerPlayer, settings, false)).thenReturn(true);
        
        assertThat(state.playerReadyToStartHand(pokerPlayer), is(false));
    }
    
    @Test
    public void testPlayerNotReadyToStartHandIfNoCashForEntryBet() {
        when(pokerPlayer.isSittingOut()).thenReturn(false);
        when(pokerPlayer.isBuyInRequestActive()).thenReturn(false);
        when(gameType.canPlayerAffordEntryBet(pokerPlayer, settings, false)).thenReturn(false);
        
        assertThat(state.playerReadyToStartHand(pokerPlayer), is(false));
    }
    
}