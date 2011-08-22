package com.cubeia.poker.rounds;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.cubeia.poker.GameType;
import com.cubeia.poker.IPokerState;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.rounds.blinds.BlindsInfo;

public class AnteRoundTest {

    @Mock private GameType game;
    @Mock private IPokerState state;
    @Mock private PokerPlayer player1;
    @Mock private PokerPlayer player2;
    @Mock private AnteRoundHelper anteRoundHelper;
    private ActionRequest actionRequest1;
    private ActionRequest actionRequest2;
    private int dealerButtonSeatId = 1;
    private BlindsInfo blindsInfo;
    
    @Before
    public void setUp() {
        initMocks(this);
        when(game.getState()).thenReturn(state);
        
        actionRequest1 = new ActionRequest();
        actionRequest2 = new ActionRequest();
        when(player1.getActionRequest()).thenReturn(actionRequest1);
        when(player2.getActionRequest()).thenReturn(actionRequest2);
        
        
        SortedMap<Integer, PokerPlayer> playerMap = new TreeMap<Integer, PokerPlayer>();
        playerMap.put(0, player1);
        playerMap.put(1, player2);
        when(state.getCurrentHandSeatingMap()).thenReturn(playerMap);
        
        blindsInfo = mock(BlindsInfo.class);
        when(blindsInfo.getDealerButtonSeatId()).thenReturn(dealerButtonSeatId );
        when(game.getBlindsInfo()).thenReturn(blindsInfo);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAnteRoundCreation() {
        
        int anteLevel = 1000;
        when(blindsInfo.getAnteLevel()).thenReturn(anteLevel);
        when(anteRoundHelper.getNextPlayerToAct(Mockito.eq(dealerButtonSeatId), Mockito.any(SortedMap.class))).thenReturn(player1);
        
        AnteRound anteRound = new AnteRound(game, anteRoundHelper);
        
        verify(player1).clearActionRequest();
        verify(player2).clearActionRequest();
        verify(state).notifyDealerButton(dealerButtonSeatId);
        verify(anteRoundHelper).requestAnte(player1, anteLevel, game);
        
        assertThat(anteRound.isFinished(), is(false));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testActOnSmallBlind() {
        AnteRound anteRound = new AnteRound(game, anteRoundHelper);
        int player1Id = 1337;
        when(state.getPlayerInCurrentHand(player1Id)).thenReturn(player1);
        int anteLevel = 1000;
        when(blindsInfo.getAnteLevel()).thenReturn(anteLevel);
        
        when(anteRoundHelper.hasAllPlayersActed(Mockito.anyCollection())).thenReturn(false);
        when(anteRoundHelper.getNextPlayerToAct(Mockito.eq(0), Mockito.any(SortedMap.class))).thenReturn(player2);
        
        anteRound.act(new PokerAction(player1Id, PokerActionType.SMALL_BLIND));
        
        verify(player1).addBet(anteLevel);
        verify(player1).setHasActed(true);
        verify(player1).setHasPostedEntryBet(true);
        
        verify(anteRoundHelper).requestAnte(player2, anteLevel, game);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testActOnDeclineSmallBlind() {
        AnteRound anteRound = new AnteRound(game, anteRoundHelper);
        int player1Id = 1337;
        when(state.getPlayerInCurrentHand(player1Id)).thenReturn(player1);
        int anteLevel = 1000;
        when(blindsInfo.getAnteLevel()).thenReturn(anteLevel);
        
        when(anteRoundHelper.hasAllPlayersActed(Mockito.anyCollection())).thenReturn(false);
        when(anteRoundHelper.getNextPlayerToAct(Mockito.eq(0), Mockito.any(SortedMap.class))).thenReturn(player2);
        
        anteRound.act(new PokerAction(player1Id, PokerActionType.DECLINE_ENTRY_BET));
        
        verify(player1, never()).addBet(anteLevel);
        verify(player1).setHasActed(true);
        verify(player1).setHasPostedEntryBet(false);
        
        verify(anteRoundHelper).requestAnte(player2, anteLevel, game);
    }    

    @Test
    public void testIsFinished() {
        AnteRound anteRound = new AnteRound(game, anteRoundHelper);
        
        when(player1.hasActed()).thenReturn(true);
        when(player1.hasPostedEntryBet()).thenReturn(true);
        when(player2.hasActed()).thenReturn(false);
        assertThat(anteRound.isFinished(), is(false));
        
        when(player1.hasActed()).thenReturn(true);
        when(player1.hasPostedEntryBet()).thenReturn(true);
        when(player2.hasActed()).thenReturn(true);
        when(player2.hasPostedEntryBet()).thenReturn(true);
        assertThat(anteRound.isFinished(), is(true));
    }
    
    @Test
    public void testIsFinishedFalseOnTooFewAntes() {
        AnteRound anteRound = new AnteRound(game, anteRoundHelper);
        
        when(player1.hasActed()).thenReturn(true);
        when(player1.hasPostedEntryBet()).thenReturn(false);
        when(player2.hasActed()).thenReturn(true);
        when(player2.hasPostedEntryBet()).thenReturn(false);
        assertThat(anteRound.isFinished(), is(true));
    }
    

}
