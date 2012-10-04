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

package com.cubeia.games.poker.adapter;

import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.adapter.HandEndStatus;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.hand.Card;
import com.cubeia.poker.hand.ExposeCardsHolder;
import com.cubeia.poker.hand.HandType;
import com.cubeia.poker.hand.Rank;
import com.cubeia.poker.player.PokerPlayer;
import com.cubeia.poker.player.PokerPlayerStatus;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotTransition;
import com.cubeia.poker.pot.RakeInfoContainer;
import com.cubeia.poker.result.HandResult;
import com.cubeia.poker.util.SitoutCalculator;
import com.cubeia.poker.tournament.RoundReport;

import java.util.Collection;
import java.util.List;

public abstract class ServerAdapterProxy implements ServerAdapter {

    public abstract ServerAdapter getAdaptee();

    @Override
    public void notifyBuyInInfo(int playerId, boolean mandatoryBuyin) {
        if (getAdaptee() != null) {
            getAdaptee().notifyBuyInInfo(playerId, mandatoryBuyin);
        }
    }

    @Override
    public void scheduleTimeout(long millis) {
        if (getAdaptee() != null) {
            getAdaptee().scheduleTimeout(millis);
        }
    }

    @Override
    public void requestAction(ActionRequest request) {
        if (getAdaptee() != null) {
            getAdaptee().requestAction(request);
        }
    }

    @Override
    public void requestMultipleActions(Collection<ActionRequest> requests) {
        if (getAdaptee() != null) {
            getAdaptee().requestMultipleActions(requests);
        }
    }

    @Override
    public void notifyCommunityCards(List<Card> cards) {
        if (getAdaptee() != null) {
            getAdaptee().notifyCommunityCards(cards);
        }
    }

    @Override
    public void notifyDealerButton(int seatId) {
        if (getAdaptee() != null) {
            getAdaptee().notifyDealerButton(seatId);
        }
    }

    @Override
    public void notifyPrivateCards(int playerId, List<Card> cards) {
        if (getAdaptee() != null) {
            getAdaptee().notifyPrivateCards(playerId, cards);
        }
    }

    @Override
    public void notifyBestHand(int playerId, HandType handType, List<Card> cardsInHand, boolean publicHand) {
        if (getAdaptee() != null) {
            getAdaptee().notifyBestHand(playerId, handType, cardsInHand, publicHand);
        }
    }

    @Override
    public void notifyPrivateExposedCards(int playerId, List<Card> cards) {
        if (getAdaptee() != null) {
            getAdaptee().notifyPrivateExposedCards(playerId, cards);
        }
    }

    @Override
    public void exposePrivateCards(ExposeCardsHolder holder) {
        if (getAdaptee() != null) {
            getAdaptee().exposePrivateCards(holder);
        }
    }

    @Override
    public void notifyNewHand() {
        if (getAdaptee() != null) {
            getAdaptee().notifyNewHand();
        }
    }

    @Override
    public void notifyRakeInfo(RakeInfoContainer rakeInfoContainer) {
        if (getAdaptee() != null) {
            getAdaptee().notifyRakeInfo(rakeInfoContainer);
        }
    }

    @Override
    public void notifyHandEnd(HandResult handResult, HandEndStatus handEndStatus, boolean tournamentTable) {
        if (getAdaptee() != null) {
            getAdaptee().notifyHandEnd(handResult, handEndStatus, tournamentTable);
        }
    }

    @Override
    public void notifyPlayerBalance(PokerPlayer player) {
        if (getAdaptee() != null) {
            getAdaptee().notifyPlayerBalance(player);
        }
    }

    @Override
    public void notifyActionPerformed(PokerAction action, PokerPlayer pokerPlayer) {
        if (getAdaptee() != null) {
            getAdaptee().notifyActionPerformed(action, pokerPlayer);
        }
    }

    @Override
    public void notifyNewRound() {
        if (getAdaptee() != null) {
            getAdaptee().notifyNewRound();
        }
    }

    @Override
    public void reportTournamentRound(RoundReport report) {
        if (getAdaptee() != null) {
            getAdaptee().reportTournamentRound(report);
        }
    }

    @Override
    public void cleanupPlayers(SitoutCalculator sitoutCalculator) {
        if (getAdaptee() != null) {
            getAdaptee().cleanupPlayers(sitoutCalculator);
        }
    }

    @Override
    public void notifyPotUpdates(Collection<Pot> iterable, Collection<PotTransition> potTransitions) {
        if (getAdaptee() != null) {
            getAdaptee().notifyPotUpdates(iterable, potTransitions);
        }
    }

    @Override
    public void notifyHandStartPlayerStatus(int playerId, PokerPlayerStatus status) {
        if (getAdaptee() != null) {
            getAdaptee().notifyHandStartPlayerStatus(playerId, status);
        }
    }

    @Override
    public void notifyPlayerStatusChanged(int playerId, PokerPlayerStatus status, boolean isInCurrentHand) {
        if (getAdaptee() != null) {
            getAdaptee().notifyPlayerStatusChanged(playerId, status, isInCurrentHand);
        }
    }

    @Override
    public void notifyDeckInfo(int size, Rank rankLow) {
        if (getAdaptee() != null) {
            getAdaptee().notifyDeckInfo(size, rankLow);
        }
    }

    @Override
    public void unseatPlayer(int playerId, boolean setAsWatcher) {
        if (getAdaptee() != null) {
            getAdaptee().unseatPlayer(playerId, setAsWatcher);
        }
    }

    @Override
    public void notifyTakeBackUncalledBet(int playerId, int amount) {
        if (getAdaptee() != null) {
            getAdaptee().notifyTakeBackUncalledBet(playerId, amount);
        }
    }

    @Override
    public void notifyExternalSessionReferenceInfo(int playerId, String externalTableReference,
                                                   String externalTableSessionReference) {
        if (getAdaptee() != null) {
            getAdaptee().notifyExternalSessionReferenceInfo(playerId, externalTableReference, externalTableSessionReference);
        }
    }

    @Override
    public void notifyFutureAllowedActions(PokerPlayer player, List<PokerActionType> optionList) {
        if (getAdaptee() != null) {
            getAdaptee().notifyFutureAllowedActions(player, optionList);
        }
    }

    @Override
    public void performPendingBuyIns(Collection<PokerPlayer> players) {
        if (getAdaptee() != null) {
            getAdaptee().performPendingBuyIns(players);
        }
    }

    @Override
    public void notifyDisconnected(int playerId) {
        if (getAdaptee() != null) {
            getAdaptee().notifyDisconnected(playerId);
        }
    }

}
