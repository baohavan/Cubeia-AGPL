/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.poker.variant.telesina;

import com.cubeia.poker.adapter.ServerAdapterHolder;
import com.cubeia.poker.context.PokerContext;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.pot.RakeInfoContainer;
import com.cubeia.poker.pot.RakeInfoContainer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.variant.HandFinishedListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.SortedMap;
import java.util.TreeMap;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class TelesinaCancelHandTest {

    @Mock
    private PokerContext context;

    @Mock
    private ServerAdapterHolder serverAdapterHolder;

    @Mock
    private ServerAdapter serverAdapter;

    @Mock
    private HandFinishedListener handFinishedListener;

    @Mock
    private PotHolder potHolder;
    
    private Telesina telesina;

    @Before
    public void setup() {
        initMocks(this);
        when(serverAdapterHolder.get()).thenReturn(serverAdapter);
        when(context.getPotHolder()).thenReturn(potHolder);
        telesina = new Telesina(null, null, null, null);
        telesina.setPokerContextAndServerAdapter(context, serverAdapterHolder);
        telesina.addHandFinishedListener(handFinishedListener);
    }

    @Test
    public void testCancelHand() {
        Integer player1Id = 1222;
        Integer player2Id = 2333;

        PokerPlayer player1 = mock(PokerPlayer.class);
        when(player1.getId()).thenReturn(player1Id);
        when(player1.getBetStack()).thenReturn(0L);

        PokerPlayer player2 = mock(PokerPlayer.class);
        when(player2.getId()).thenReturn(player2Id);
        when(player2.getBetStack()).thenReturn(100L);

        SortedMap<Integer, PokerPlayer> playerMap = new TreeMap<Integer, PokerPlayer>();
        playerMap.put(player1Id, player1);
        playerMap.put(player2Id, player2);
        when(context.getCurrentHandPlayerMap()).thenReturn(playerMap);

        SortedMap<Integer, PokerPlayer> seatingMap = new TreeMap<Integer, PokerPlayer>();
        seatingMap.put(0, player1);
        seatingMap.put(1, player2);
        when(context.getCurrentHandSeatingMap()).thenReturn(seatingMap);

        telesina.handleCanceledHand();

        verify(handFinishedListener).handFinished(Mockito.any(HandResult.class), Mockito.eq(HandEndStatus.CANCELED_TOO_FEW_PLAYERS));
        verify(serverAdapter, never()).notifyTakeBackUncalledBet(player1.getId(), 0);
        verify(serverAdapter).notifyTakeBackUncalledBet(player2.getId(), 100);
        verify(serverAdapter).notifyRakeInfo(Matchers.<RakeInfoContainer>any());
    }
}
