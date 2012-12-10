/**
 * Copyright (C) 2012 Cubeia Ltd <info@cubeia.com>
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

package com.cubeia.games.poker.tournament.lobby;

import com.cubeia.firebase.api.mtt.MttInstance;
import com.cubeia.firebase.api.mtt.MttNotifier;
import com.cubeia.firebase.api.mtt.model.MttPlayer;
import com.cubeia.firebase.api.mtt.model.MttPlayerStatus;
import com.cubeia.firebase.api.mtt.support.MTTStateSupport;
import com.cubeia.firebase.api.mtt.support.registry.PlayerRegistry;
import com.cubeia.firebase.io.StyxSerializer;
import com.cubeia.games.poker.common.SystemTime;
import com.cubeia.games.poker.io.protocol.TournamentPlayerList;
import com.cubeia.games.poker.tournament.state.PokerTournamentState;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static com.cubeia.firebase.api.mtt.model.MttPlayerStatus.OUT;
import static com.cubeia.firebase.api.mtt.model.MttPlayerStatus.PLAYING;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TournamentLobbyTest {

    private static final Logger log = Logger.getLogger(TournamentLobbyTest.class);

    private TournamentLobby lobby;

    @Mock
    private MttInstance instance;

    private StyxSerializer serializer = null;

    @Mock
    private MttNotifier notifier;

    @Mock
    private PokerTournamentState pokerState;

    @Mock
    private MTTStateSupport state;

    @Mock
    private PlayerRegistry playerRegistry;

    @Mock
    private SystemTime dateFetcher;

    @Before
    public void setup() {
        initMocks(this);
        when(state.getPlayerRegistry()).thenReturn(playerRegistry);
        lobby = new TournamentLobby(instance, serializer, state, pokerState, dateFetcher);
    }

    @Test
    public void testCreatePlayerList() {
        // Given that the tournament has these players.
        MttPlayer p1 = createPlayer(1, "Adam", 540, -1, PLAYING);
        MttPlayer p2 = createPlayer(2, "Ben", 520, -1, PLAYING);
        MttPlayer p3 = createPlayer(3, "Caesar", 520, -1, PLAYING);
        MttPlayer p4 = createPlayer(4, "Dave", 500, -1, PLAYING);
        MttPlayer p5 = createPlayer(5, "Eva", 0, 5, OUT);
        List<MttPlayer> players = ImmutableList.of(p1, p2, p3, p4, p5);
        when(playerRegistry.getPlayers()).thenReturn(players);

        // When we create the list.
        TournamentPlayerList list = lobby.getPlayerList();

        log.debug("List: " + list);

        // It should start with p1.
        assertThat(list.players.get(0).name, is(p1.getScreenname()));

        // Ben and Caesar should share place 2.
        assertThat(list.players.get(1).position, is(2));
        assertThat(list.players.get(2).position, is(2));

        // And Dave should be in place 4.
        assertThat(list.players.get(3).position, is(4));

        // Finally, Eva should be in place 5.
        assertThat(list.players.get(4).position, is(5));

        // A message to the "one assertion per test case" camp: sue me!
    }

    public MttPlayer createPlayer(int playerId, String name, long balance, int position, MttPlayerStatus status) {
        when(pokerState.getPlayerBalance(playerId)).thenReturn(balance);
        MttPlayer player = new MttPlayer(playerId, name);
        player.setStatus(status);
        player.setPosition(position);
        return player;
    }

}
